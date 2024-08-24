package jo.vpl.core;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import jo.vpl.Config;
import org.reflections.Reflections;

/**
 *
 * @author joostmeulenkamp
 */
public class BlockLoader {

//    static final Map<String, Class> HUB_PATH_MAP = new HashMap<>();
    static final Map<String, Method> HUB_METHOD_MAP = new HashMap<>();
    static final Map<String, Class<?>> HUB_TYPE_MAP = new HashMap<>();
    static final ObservableList<String> HUB_TYPE_LIST = observableArrayList();
    public static final ObservableMap<String, Object> HUB_LIBRARY = javafx.collections.FXCollections.observableHashMap();

    /**
     * Retrieve all hubs from jo.vpl.hub package
     */
    public static void loadInternalBlocks() {
        Reflections reflections = new Reflections("jo.vpl.block");
        Set<Class<? extends Block>> hubTypes = reflections.getSubTypesOf(Block.class);

        for (Class<?> type : hubTypes) {
            addBlockType(type);
        }

        Collections.sort(HUB_TYPE_LIST);
    }

    private static void addBlockType(Class<?> hubType) {
        if (hubType.isAnnotationPresent(BlockInfo.class)) {
            BlockInfo info = hubType.getAnnotation(BlockInfo.class);
            HUB_TYPE_MAP.put(info.identifier(), hubType);
            HUB_TYPE_LIST.add(info.identifier());
            HUB_LIBRARY.put(info.identifier(), hubType);
        }
    }

    /**
     * Retrieve all hubs from external libraries
     */
    public static void loadExternalBlocks() {
        File dir = new File(Config.get().getLibraryDirectory());
        File[] libraries = Util.getFilesByExtensionFrom(dir, ".jar");

        for (File lib : libraries) {
            List<String> classNames = getClassNamesFromJarFile(lib);
            List<Class<?>> classes = getClassesFromJarFile(lib, classNames);

            for (Class<?> type : classes) {
                if (!type.getSuperclass().getName().equals("jo.vpl.core.Hub")) {
                    continue;
                }
                addBlockType(type);
            }
        }

        Collections.sort(HUB_TYPE_LIST);
    }

    private static List<String> getClassNamesFromJarFile(File file) {
        List<String> result = new ArrayList<>();
        try (JarFile jarFile = new JarFile(file)) {

            Enumeration<JarEntry> e = jarFile.entries();
            String packageName = jarFile.getManifest().getMainAttributes().getValue("package");
            while (e.hasMoreElements()) {
                JarEntry jarEntry = e.nextElement();
                if (jarEntry.getName().endsWith(".class")) {
                    String className = jarEntry.getName()
                            .replace("/", ".")
                            .replace(".class", "");
                    if (className.startsWith(packageName)) {
                        result.add(className);
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BlockLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    private static List<Class<?>> getClassesFromJarFile(File file, List<String> classNames) {
        List<Class<?>> result = new ArrayList<>();
        try {
            URL url = file.toURI().toURL();
            URL[] urls = new URL[]{url};
            ClassLoader cl = new URLClassLoader(urls);

            for (String className : classNames) {
                try {
                    result.add(cl.loadClass(className));
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(BlockLoader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(BlockLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * Retrieve all hubs from static methods
     */
    public static void loadStaticMethodsAsBlocks() {
        List<Method> methods = getStaticMethodsFromClass(jo.vpl.block.methods.StringMethods.class);
        methods.addAll(getStaticMethodsFromClass(jo.vpl.block.methods.JsonMethods.class));
        methods.addAll(getStaticMethodsFromClass(jo.vpl.block.methods.MathMethods.class));
        methods.addAll(getStaticMethodsFromClass(jo.vpl.block.methods.ObjectMethods.class));
        methods.addAll(getStaticMethodsFromClass(jo.vpl.block.methods.ListMethods.class));

        for (Method m : methods) {
            addBlockMethod(m);
//            System.out.println(m.getName() + " " + Arrays.asList(m.getParameters()).toString());
//            for (Annotation a : m.getAnnotations()) {
//
//                System.out.println("\t" + a.toString());
//
//            }
//            for (Parameter p : m.getParameters()) {
//
//                System.out.println("\t" + p.isNamePresent() + " " + p.getType().getSimpleName());
//
//            }
        }

        Collections.sort(HUB_TYPE_LIST);
    }

    public static List<Method> getStaticMethodsFromClass(Class<?> c) {
        List<Method> result = new ArrayList<>();
        Method[] methods = c.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            if (Modifier.isStatic(m.getModifiers())) {
                result.add(m);
            }
        }
        return result;
    }

    private static void addBlockMethod(Method hubMethod) {
        if (hubMethod.isAnnotationPresent(BlockInfo.class)) {
            BlockInfo info = hubMethod.getAnnotation(BlockInfo.class);
            HUB_METHOD_MAP.put(info.identifier(), hubMethod);
            HUB_TYPE_LIST.add(info.identifier());
            HUB_LIBRARY.put(info.identifier(), hubMethod);
        }
    }

    public static void loadStaticFieldsAsBlocks() {
        List<Field> fields = getStaticFieldsFromClass(Math.class);
        for (Field f : fields) {
            try {

                System.out.println(f.get(null));
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(BlockLoader.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public static List<Field> getStaticFieldsFromClass(Class<?> c) {
        List<Field> result = new ArrayList<>();
        Field[] fields = c.getFields();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            if (Modifier.isStatic(f.getModifiers())) {
                result.add(f);
            }
        }
        return result;
    }

}

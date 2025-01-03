package vplcore.graph.util;

import vplcore.graph.model.Block;
import vplcore.graph.model.BlockInfo;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
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

import vplcore.Config;
import org.reflections.Reflections;
import vplcore.util.FileUtils;
import vplcore.workspace.BlockModel;

/**
 *
 * @author joostmeulenkamp
 */
public class BlockLoader {

//    static final Map<String, Class> BLOCK_PATH_MAP = new HashMap<>();
//    static final Map<String, Method> BLOCK_METHOD_MAP = new HashMap<>();
    public static final Map<String, Class<?>> BLOCK_TYPE_MAP = new HashMap<>();
    public static final ObservableList<String> BLOCK_TYPE_LIST = observableArrayList();
    public static final ObservableMap<String, Object> BLOCK_LIBRARY = javafx.collections.FXCollections.observableHashMap();

    /**
     * Retrieve all blocks from vpllib.input package
     */
    public static void loadInternalBlocks() {
        Reflections reflections = new Reflections("vpllib.input");

        if (vplcore.App.BLOCK_MVC) {
            Set<Class<? extends BlockModel>> blockTypes = reflections.getSubTypesOf(BlockModel.class);
            for (Class<?> type : blockTypes) {
                addBlockType(type);
            }
        } else {
            Set<Class<? extends Block>> blockTypes = reflections.getSubTypesOf(Block.class);
            for (Class<?> type : blockTypes) {
                addBlockType(type);
            }
        }

        Collections.sort(BLOCK_TYPE_LIST);
    }

    /**
     * Retrieve all blocks from external libraries
     */
    public static void loadExternalBlocks() {
        File dir = new File(Config.get().libraryDirectory());
        File[] libraries = FileUtils.getFilesByExtensionFrom(dir, ".jar");

        List<Class<?>> classes = getClassesFromLibraries(libraries);
        classes = filterEligibleClasses(classes);

        for (Class<?> clazz : classes) {
            addBlockType(clazz);
        }

        Collections.sort(BLOCK_TYPE_LIST);
    }

    private static void addBlockType(Class<?> blockType) {
        BlockInfo info = blockType.getAnnotation(BlockInfo.class);
        BLOCK_TYPE_MAP.put(info.identifier(), blockType);
        BLOCK_TYPE_LIST.add(info.identifier());
        BLOCK_LIBRARY.put(info.identifier(), blockType);
    }

    private static List<Class<?>> getClassesFromLibraries(File[] libraries) {
        List<Class<?>> result = new ArrayList<>();
        for (File lib : libraries) {
            List<String> classNames = getClassNamesFromJarFile(lib);
            List<Class<?>> classes = getClassesFromJarFile(lib, classNames);
            result.addAll(classes);
        }
        return result;
    }

    private static List<String> getClassNamesFromJarFile(File file) {
        List<String> result = new ArrayList<>();
        try ( JarFile jarFile = new JarFile(file)) {

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

    private static List<Class<?>> filterEligibleClasses(List<Class<?>> classes) {
        List<Class<?>> result = new ArrayList<>();
        for (Class<?> clazz : classes) {
            if (vplcore.App.BLOCK_MVC) {
                if (BlockModel.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(BlockInfo.class)) {
                    result.add(clazz);
                }
            } else {
                if (Block.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(BlockInfo.class)) {
                    result.add(clazz);
                }
            }
        }
        return result;
    }

    /**
     * Retrieve all blocks from static methods
     */
    public static void loadInternalMethodBlocks() {
//        BlockInfo info = MethodBlock.class.getAnnotation(BlockInfo.class);
//        BLOCK_LIBRARY.put(info.identifier(), MethodBlock.class);
        List<Class<?>> classes = List.of(
                vpllib.method.JsonMethods.class,
                vpllib.method.ListMethods.class,
                vpllib.method.MathMethods.class,
                vpllib.method.ObjectMethods.class,
                vpllib.method.StringMethods.class
        );
        List<Method> methods = getStaticMethodsFromClasses(classes);

        methods = filterEligibleMethods(methods);
        for (Method method : methods) {
            addMethodBlockType(method);
        }

        Collections.sort(BLOCK_TYPE_LIST);
    }

    public static void loadExternalMethodBlocks() {
        File dir = new File(Config.get().libraryDirectory());
        File[] libraries = FileUtils.getFilesByExtensionFrom(dir, ".jar");
        List<Class<?>> classes = getClassesFromLibraries(libraries);
        List<Method> methods = getStaticMethodsFromClasses(classes);

        methods = filterEligibleMethods(methods);
        for (Method method : methods) {
            addMethodBlockType(method);
        }

        Collections.sort(BLOCK_TYPE_LIST);
    }

    private static List<Method> filterEligibleMethods(List<Method> methods) {
        List<Method> result = new ArrayList<>();
        for (Method method : methods) {
            if (method.isAnnotationPresent(BlockInfo.class)) {
                result.add(method);
            }
        }
        return result;
    }

    private static List<Method> getStaticMethodsFromClasses(List<Class<?>> classes) {
        List<Method> result = new ArrayList<>();
        for (Class<?> clazz : classes) {
            List<Method> methods = getStaticMethodsFromClass(clazz);
            result.addAll(methods);
        }
        return result;
    }

    private static List<Method> getStaticMethodsFromClass(Class<?> clazz) {
        List<Method> result = new ArrayList<>();
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            if (Modifier.isStatic(m.getModifiers())) {
                result.add(m);
            }
        }
        return result;
    }

    private static void addMethodBlockType(Method blockMethod) {
        BlockInfo info = blockMethod.getAnnotation(BlockInfo.class);
//        BLOCK_METHOD_MAP.put(info.identifier(), blockMethod);
        BLOCK_TYPE_LIST.add(info.identifier());
        BLOCK_LIBRARY.put(info.identifier(), blockMethod);
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

package jo.vpl.core;

import java.io.File;
import java.io.IOException;
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
import jo.vpl.Config;
import org.reflections.Reflections;

/**
 *
 * @author joostmeulenkamp
 */
public class HubLoader {

//    static final Map<String, Class> HUB_PATH_MAP = new HashMap<>();
    static final Map<String, Class> HUB_TYPE_MAP = new HashMap<>();
    static final ObservableList<String> HUB_TYPE_LIST = observableArrayList();

    /**
     * Retrieve all hubs from jo.vpl.hub package
     */
    public static void loadInternalHubs() {
        Reflections reflections = new Reflections("jo.vpl.hub");
        Set<Class<? extends Hub>> hubTypes = reflections.getSubTypesOf(Hub.class);

        for (Class<?> type : hubTypes) {
            addHubType(type);
        }

        Collections.sort(HUB_TYPE_LIST);
    }

    private static void addHubType(Class<?> hubType) {
        if (hubType.isAnnotationPresent(HubInfo.class)) {
            HubInfo info = hubType.getAnnotation(HubInfo.class);
            HUB_TYPE_MAP.put(info.name(), hubType);
            HUB_TYPE_LIST.add(info.name());
        }
    }

    /**
     * Retrieve all hubs from external libraries
     */
    public static void loadExternalHubs() {

        File dir = new File(Config.get().getLibraryDirectory());
        File[] libraries = Util.getFilesByExtensionFrom(dir, ".jar");

        for (File lib : libraries) {
            List<String> classNames = getClassNamesFromJarFile(lib);
            List<Class<?>> classes = getClassesFromJarFile(lib, classNames);

            for (Class<?> type : classes) {
                if (!type.getSuperclass().getName().equals("jo.vpl.core.Hub")) {
                    continue;
                }
                addHubType(type);
            }
        }

        Collections.sort(HUB_TYPE_LIST);
    }

    private static List<String> getClassNamesFromJarFile(File file) {
        List<String> result = new ArrayList<>();
        try (JarFile jarFile = new JarFile(file)) {
            Enumeration<JarEntry> e = jarFile.entries();
            while (e.hasMoreElements()) {
                JarEntry jarEntry = e.nextElement();
                if (jarEntry.getName().endsWith(".class")) {
                    String className = jarEntry.getName()
                            .replace("/", ".")
                            .replace(".class", "");
                    result.add(className);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(HubLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    private static List<Class<?>> getClassesFromJarFile(File file, List<String> classNames) {
        List<Class<?>> result = new ArrayList<>();
        try {
            URL url = file.toURI().toURL();
            URL[] urls = new URL[]{url};
            ClassLoader classLoader = new URLClassLoader(urls);

            for (String className : classNames) {
                try {
                    result.add(classLoader.loadClass(className));
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(HubLoader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(HubLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

}

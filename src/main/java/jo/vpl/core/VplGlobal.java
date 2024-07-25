/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jo.vpl.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
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
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.reflections.Reflections;

/**
 *
 * @author joost.meulenkamp
 */
public class VplGlobal {

    public static String[] EXTERNAL_LIBRARIES = {"build", "ext"};
    public static String[] EXTERNAL_LIBRARIES_DEPENDENCIES = {"build", "ext", "lib"};

//    static final Map<String, Class> HUB_PATH_MAP = new HashMap<>();
    static final Map<String, Class> HUB_TYPE_MAP = new HashMap<>();
    static final ObservableList<String> HUB_TYPE_LIST = observableArrayList();

    /**
     * Retrieve all hubs from jo.vpl.hub package
     */
    public static void loadInternalHubs() {
        Reflections reflections = new Reflections("jo.vpl.hub");
        Set<Class<? extends Hub>> hubTypes = reflections.getSubTypesOf(Hub.class);

        for (Class type : hubTypes) {
            if (type.isAnnotationPresent(HubInfo.class)) {
                HubInfo info = (HubInfo) type.getAnnotation(HubInfo.class);
//                HUB_PATH_MAP.put(type.getName(), type);
                HUB_TYPE_MAP.put(info.name(), type);
                HUB_TYPE_LIST.add(info.name());
            }
        }

        Collections.sort(HUB_TYPE_LIST);
    }


    /**
     * Retrieve all hubs from external libraries
     */
    public static void loadExternalHubs() {

        File dir = Util.getDirectory(EXTERNAL_LIBRARIES);
        if (!dir.exists() && !dir.isDirectory()) {
            return;
        }

        List<File> externalLibs = Util.filterFilesByRegex(dir.listFiles(), "^.*(.jar)$");

        for (File lib : externalLibs) {
            try {
                String pathToJar = lib.getPath();
                JarFile jarFile = new JarFile(pathToJar);

                Enumeration<JarEntry> e = jarFile.entries();

                ClassPool classPool = ClassPool.getDefault();
                classPool.insertClassPath(pathToJar);

                URL[] urls = {new URL("jar:file:" + pathToJar + "!/")};
                URLClassLoader classLoader = URLClassLoader.newInstance(urls);

                //https://stackoverflow.com/questions/17371748/find-all-dependencies-in-a-java-class
                while (e.hasMoreElements()) {
                    JarEntry jarEntry = e.nextElement();
                    if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")) {
                        continue;
                    }
                    // -6 because of .class
                    String className = jarEntry.getName().substring(0, jarEntry.getName().length() - 6);
                    className = className.replace('/', '.');

                    CtClass ctClass = classPool.get(className);

                    if (!ctClass.getSuperclass().getName().equals("jo.vpl.core.Hub")) {
                        continue;
                    }
                    System.out.println("Loaded : " + className);

                    Class type = classLoader.loadClass(className);

                    if (type.isAnnotationPresent(HubInfo.class)) {
                        HubInfo info = (HubInfo) type.getAnnotation(HubInfo.class);
//                        HUB_PATH_MAP.put(className, type);
                        HUB_TYPE_MAP.put(info.name(), type);
                        HUB_TYPE_LIST.add(info.name());
                    }
                }
            } catch (IOException | ClassNotFoundException | NotFoundException ex) {
                Logger.getLogger(VplGlobal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        Collections.sort(HUB_TYPE_LIST);
    }

    public static void defineRuntimePath() {

        String path = Util.getPathOfJAR(VplGlobal.class);
        if (path.toLowerCase().contains("build")) {
            String[] newPath = {"ext"};
            EXTERNAL_LIBRARIES = newPath;
            String[] newPathLib = {"ext", "lib"};
            EXTERNAL_LIBRARIES_DEPENDENCIES = newPathLib;
        }

    }

}

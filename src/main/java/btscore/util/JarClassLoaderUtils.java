package btscore.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joostmeulenkamp
 */
public class JarClassLoaderUtils {

    private static final Logger LOGGER = Logger.getLogger(JarClassLoaderUtils.class.getName());

    public static List<Class<?>> getClassesFromLibraries(File[] libraries) {
        List<Class<?>> result = new ArrayList<>();
        for (File jar : libraries) {
            List<String> classNames = getClassNamesFromJarFile(jar);
            List<Class<?>> classes = loadClasses(jar, classNames);
            result.addAll(classes);
        }
        return result;
    }

    private static List<String> getClassNamesFromJarFile(File jar) {
        List<String> result = new ArrayList<>();
        try (JarFile jarFile = new JarFile(jar)) {

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
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return result;
    }

    private static List<Class<?>> loadClasses(File file, List<String> classNames) {
        List<Class<?>> result = new ArrayList<>();
        try {
            URL url = file.toURI().toURL();
            URL[] urls = new URL[]{url};
            ClassLoader cl = new URLClassLoader(urls);

            for (String className : classNames) {
                try {
                    result.add(cl.loadClass(className));
                } catch (ClassNotFoundException ex) {
                    LOGGER.log(Level.SEVERE, null, ex);
                }
            }

        } catch (MalformedURLException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return result;
    }
}

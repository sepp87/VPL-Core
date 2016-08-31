package jo.vpl.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JoostMeulenkamp
 */
public class General {

    /**
     * Get the absolute path to the directory of the .jar
     *
     * @param any object within the project
     * @return the parent directory of the .jar
     */
    public static String getPath(Object any) {
        String jarPath = any.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        return jarPath.substring(0, jarPath.lastIndexOf('/') + 1);
    }

    /**
     * Get the extension of a file. <br><br>
     * Also works for e.g. path/to.a/file and config/.htaccess
     *
     * @param file in question
     * @return an empty string if none is found
     */
    public static String getExtension(File file) {
        String extension = "";
        String path = file.getPath();

        int dot = path.lastIndexOf('.');
        int slash = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));

        if (dot > slash + 1) {
            extension = path.substring(dot + 1);
        }
        return extension;
    }

    /**
     * @param value
     * @return the number of decimal places of a double.
     */
    public static int getNumberOfDecimalPlaces(Double value) {
        String str = value + "";
        return str.length() - str.indexOf('.') - 1;
    }

    // get the FontAwesome unicodes as ENUM types from the Cheatsheet
    public static void getFontAwesomeUnicodeENUM() {
        //How it somewhat looks like, but then a whole page full
        String raw = " fa-500px [&#xf26e;]  fa-adjust [&#xf042;]";

        String raw2 = raw.replaceAll(" ", "");
        String raw3 = raw2.replaceAll("&#x", "u");
        String rawE = raw3.replaceAll("-", "_");
        String RAW = rawE.replace("(alias)", "");
        String[] raw4 = RAW.split(";]");

        List<String> raw5 = new ArrayList<>();
        for (int i = 0; i < raw4.length; i++) {
            raw5.add(raw4[i].substring(1));
        }

        List<String> unicodes = new ArrayList<>();
        List<String> names = new ArrayList<>();

        for (String s : raw5) {
            String[] raw6 = s.split("\\[");
            if (raw6[0].startsWith(".")) {
                names.add(raw6[0].substring(3).toUpperCase());
            } else {
                names.add(raw6[0].toUpperCase());
            }

            unicodes.add(raw6[1]);
        }

        for (int i = 0; i < names.size(); i++) {
            System.out.println(names.get(i) + "('\\" + unicodes.get(i) + "'),");
        }
    }

}

package btscore.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Joost
 */
public class FontUtils {
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

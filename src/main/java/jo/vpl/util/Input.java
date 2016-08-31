package jo.vpl.util;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JoostMeulenkamp
 */
public class Input {

    /**
     * Read a file and return a list of strings
     * @param path the file location
     * @return the lines as list of strings
     */
    public static List<String> readFile(String path) {
        List<String> stringList = new ArrayList<>();
        File file = new File(path);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringList.add(line);
            }

        } catch (IOException ex) {
            Logger.getLogger(Input.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stringList;
    }
}

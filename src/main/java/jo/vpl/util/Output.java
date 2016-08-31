package jo.vpl.util;

import java.io.*;
import java.util.*;
import java.util.logging.*;

/**
 *
 * @author JoostMeulenkamp
 */
public class Output {

    /**
     * Write a list of strings to file 
     * @param list the list of strings
     * @param path the file location
     */
    public static void writeFile(List<String> list, String path) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(path));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            for (String s : list) {
                writer.append(s);
                writer.newLine();
            }
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Output.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Output.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void writeCSV(List<List<String>> lol, String filename) {

        List<String> sList = new ArrayList<>();
        lol.stream().forEach((list) -> {
            sList.add(getCommaSeperatedLine(list));
        });

        FileOutputStream fos;
        try {
            fos = new FileOutputStream(new File("src/main/resources/" + filename + ".csv"));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            for (String s : sList) {
                writer.append(s);
                writer.newLine();
            }
            writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Output.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Output.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Write a comma seperated line from a list of strings
     *
     * @param list the list of strings to concanate
     * @return a comma seperated string
     */
    public static String getCommaSeperatedLine(List<String> list) {
        StringBuilder b = new StringBuilder();
        String comma = ",";
        for (String s : list) {
            b.append(s);
            b.append(comma);
        }
        b.deleteCharAt(b.length() - 1);

        return b.toString();
    }
}

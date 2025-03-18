package vplcore.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Joost
 */
public class FileUtils {

    public static File getDirectory(String[] path) {
        return getFile(path, null);
    }

    public static File getFile(String[] path, String extension) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < path.length; i++) {
            sb.append(path[i]);
            if (i == path.length - 1) {
                continue;
            }
            sb.append(File.separatorChar);
        }
        if (extension != null) {
            sb.append(".").append(extension);
        }
        return new File(sb.toString());
    }

    public static File[] getFilesByExtensionFrom(File directory, String extension) {
        return directory.listFiles(getFileExtensionFilter(extension));
    }

    // https://howtodoinjava.com/java/io/java-filefilter-example/
    private static FileFilter getFileExtensionFilter(String extension) {
        FileFilter filter = new FileFilter() {
            //Override accept method
            public boolean accept(File file) {
                //if the file extension is .csv return true, else false
                return file.getName().toLowerCase().endsWith(extension);
            }
        };
        return filter;
    }

    /**
     * @param removeQuotations removes first and last character of the values if
     * true
     * @param file *.csv to read
     * @return a list of lists. Each list represents a line.
     */
    public static List<List<String>> readCommaSeperatedFile(boolean removeQuotations, File file) {
        return readDelimiterSeperatedFile(",", removeQuotations, file);
    }

    /**
     * @param delimiter to seperate the strings by
     * @param removeQuotations removes first and last character of the values if
     * true
     * @param file *.csv to read
     * @return a list of lists. Each list represents a line.
     */
    public static List<List<String>> readDelimiterSeperatedFile(String delimiter, boolean removeQuotations, File file) {
        List<List<String>> lol = new ArrayList<>();
        List<String> list = readFile(file);
        int size = list.size();
        int numOfColumns = 0;
        boolean initialized = false;
        for (int i = 0; i < size; i++) {
            String line = list.get(i);
            String[] raw = line.split(delimiter);
            if (!initialized) {
                numOfColumns = raw.length;
                initialized = true;
            }

            boolean isOneShort = false;
            int length;
            if (!removeQuotations) {
                //If last column has no value after the delimiter: raw.length is 1 less and therefor wrong
                length = line.length() - line.replaceAll(delimiter, "").length() + 1;
                if (raw.length + 1 == length) {
                    isOneShort = true;
                }
            } else {
                length = raw.length;
            }

            //Safeguard for when a row takes up multiple lines
            while (length != numOfColumns) {
                i++;
                System.out.println(file.getPath());
                line += list.get(i);
                raw = line.split(delimiter);
            }

            List<String> values = new ArrayList<>();
            for (int j = 0; j < raw.length; j++) {
                String val = raw[j];
                if (removeQuotations) {
                    //Safeguard for when a value is not surrounded by quotations
                    if (!val.startsWith("\"")) {
                        removeQuotations = false;
                    }

                    val = val.substring(1, val.length() - 1);
                }
                values.add(val);
            }

            if (isOneShort) {
                values.add("");
            }

            lol.add(values);
        }

        return lol;
    }

    /**
     * @param file to read from, e.g. a *.csv or *.txt
     * @return a list of strings. Each string represents a line.
     */
    public static List<String> readFile(File file) {
        List<String> stringList = new ArrayList<>();

        try ( FileReader fr = new FileReader(file);  BufferedReader bf = new BufferedReader(fr)) {

            String line = null;
            while ((line = bf.readLine()) != null) {
                stringList.add(line);
            }

        } catch (IOException ex) {
            Logger.getLogger(ParsingUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return stringList;
    }

    /**
     * @param file to read from, e.g. a *.csv or *.txt
     * @return a list of strings. Each string represents a line.
     */
    public static String readFileAsString(File file) {
        String result = "";
        try ( FileReader fr = new FileReader(file);  BufferedReader bf = new BufferedReader(fr)) {

            String line = null;
            while ((line = bf.readLine()) != null) {
                result = result + line;
            }

        } catch (IOException ex) {
            Logger.getLogger(ParsingUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public static File createFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(ParsingUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return file;
    }

    public static File createDirectory(File file) {
        if (!file.exists()) {
            file.mkdir();
        }
        return file;
    }

    public static Properties loadProperties(File file) {
        Properties properties = new Properties();
        try ( InputStream inputStream = new FileInputStream(file)) {
            properties.load(inputStream);
        } catch (IOException ex) {
            Logger.getLogger(ParsingUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return properties;
    }
}

package vplcore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author joostmeulenkamp
 */
public class Util {

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

    public static final Map<String, String> DATE_REGEX = new HashMap<String, String>() {
        { //http://balusc.omnifaces.org/2007/09/dateutil.html
            put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}$", "dd.MM.yyyy"); //01.04.2016
            put("^\\d{4}\\.\\d{1,2}\\.\\d{1,2}$", "YYYY.MM.dd"); //01.04.2016
            put("^[a-zA-Z]{2}\\s\\d{1,2}.\\d{1,2}.\\d{4}\\s\\d{1,2}:\\d{2}\\s[+|-]\\d{4}$", "EE dd.MM.yyyy HH:mm Z"); //Mi 06.07.2016 09:47 +0200
            put("^\\d{8}$", "yyyyMMdd");
            put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
            put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
            put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
            put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
            put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
            put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
            put("^\\d{12}$", "yyyyMMddHHmm");
            put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
            put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
            put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
            put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
            put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
            put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
            put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
            put("^\\d{14}$", "yyyyMMddHHmmss");
            put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
            put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss");
            put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");
            put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "MM/dd/yyyy HH:mm:ss");
            put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss");
            put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");
            put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss");
        }
    };

    /**
     * Determine SimpleDateFormat pattern matching with the given date string.
     * Returns null if format is unknown. You can simply extend DateUtil with
     * more formats if needed.
     *
     * @param dateString The date string to determine the SimpleDateFormat
     * pattern for.
     * @return The matching SimpleDateFormat pattern, or null if format is
     * unknown.
     * @see SimpleDateFormat
     */
    public static String getDateFormat(String dateString) {
        for (String regexp : DATE_REGEX.keySet()) {
            if (dateString.toLowerCase().matches(regexp)) {
                return DATE_REGEX.get(regexp);
            }
        }
        return null; // Unknown format.
    } //http://balusc.omnifaces.org/2007/09/dateutil.html

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
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    /**
     * @param rawValue the string to check
     * @return a double when the string is a valid number, otherwise null.
     */
    public static Double getDoubleValue(String rawValue) {
        Double newValue = null;
        //http://stackoverflow.com/questions/3133770/how-to-find-out-if-the-value-contained-in-a-string-is-double-or-not
        String regExp = "[\\x00-\\x20]*[+-]?(((((\\p{Digit}+)(\\.)?((\\p{Digit}+)?)([eE][+-]?(\\p{Digit}+))?)|(\\.((\\p{Digit}+))([eE][+-]?(\\p{Digit}+))?)|(((0[xX](\\p{XDigit}+)(\\.)?)|(0[xX](\\p{XDigit}+)?(\\.)(\\p{XDigit}+)))[pP][+-]?(\\p{Digit}+)))[fFdD]?))[\\x00-\\x20]*";
        boolean isDouble = rawValue.matches(regExp);

        if (isDouble) {
            newValue = Double.parseDouble(rawValue);
        }
        return newValue;
    }

    /**
     * @param rawValue the string to check
     * @return an integer when the string is a valid number, otherwise null.
     */
    public static Integer getIntegerValue(String rawValue) {
        Integer newValue = null;
        //http://stackoverflow.com/questions/16331423/whats-the-java-regular-expression-for-an-only-integer-numbers-string
//        String regExp = "^\\d+$";
        String regExp = "-?[0-9]{1,10}";
        boolean isInteger = rawValue.matches(regExp);

        if (isInteger) {
            newValue = Integer.parseInt(rawValue);
        }
        return newValue;
    }

    /**
     * @param rawValue the string to check
     * @return a long when the string is a valid number, otherwise null.
     */
    public static Long getLongValue(String rawValue) {
        Long newValue = null;

        //http://stackoverflow.com/questions/16331423/whats-the-java-regular-expression-for-an-only-integer-numbers-string
        String regExp = "-?[0-9]{1,19}";
        boolean isLong = rawValue.matches(regExp);

        if (isLong) {
            newValue = Long.parseLong(rawValue);
        }
        return newValue;
    }

    /**
     * @param rawValue the string to check
     * @return a boolean when the string is a valid boolean, otherwise null.
     */
    public static Boolean getBooleanValue(String rawValue) {
        Boolean newValue = null;

        if (rawValue.toLowerCase().equals("true")) {
            newValue = true;
        } else if (rawValue.toLowerCase().equals("false")) {
            newValue = false;
        }

        return newValue;
    }

    /**
     * Create a new dynamically typed list. <br>
     * https://stackoverflow.com/questions/15697775/dynamic-initialization-of-arraylistanyclassobject
     *
     * @param <T>
     * @param type
     * @return
     */
    public static <T> List<T> getList(Class<T> type) {
        List<T> arrayList = new ArrayList<>();
        return arrayList;
    }

    public static File createFile(File file) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        return properties;
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

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    /////////////////////////////// SYSTEM UTILS ///////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    /**
     *
     * @param any
     * @param fallbackPath
     * @return the app root directory if any object is inside a .jar file
     */
    public static String getAppRootDirectory(Object any, String fallbackPath) {
        try {
            URI uri = any.getClass().getProtectionDomain().getCodeSource().getLocation().toURI();
            String path = new File(uri).getAbsolutePath();
            String targetSeperatorCharClasses = "target" + File.separatorChar + "classes";
            if (path.endsWith(targetSeperatorCharClasses)) {
                fallbackPath = path.substring(0, path.length() - targetSeperatorCharClasses.length()) + fallbackPath;
            }
            return path.endsWith(".jar") ? path.substring(0, path.lastIndexOf(File.separatorChar) + 1) : fallbackPath;
        } catch (URISyntaxException ex) {
            Logger.getLogger(Util.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fallbackPath;
    }

    public static OperatingSystem determineOperatingSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return OperatingSystem.WINDOWS;
        } else if (osName.contains("mac")) {
            return OperatingSystem.MACOS;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return OperatingSystem.LINUX;
        } else if (osName.contains("sunos")) {
            return OperatingSystem.SOLARIS;
        } else {
            return OperatingSystem.OTHER_OS;
        }
    }

    public enum OperatingSystem {
        WINDOWS,
        MACOS,
        LINUX,
        SOLARIS,
        OTHER_OS
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    //////////////////////////////// EVENT UTILS ///////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    public static boolean isModifierDown(KeyEvent event) {
        switch (Config.get().operatingSystem()) {
            case WINDOWS:
                return event.isControlDown();
            case MACOS:
                return event.isMetaDown();
            case LINUX:
                return event.isMetaDown();
            default:
                return event.isControlDown();
        }
    }

    public static boolean isModifierDown(MouseEvent event) {
        switch (Config.get().operatingSystem()) {
            case WINDOWS:
                return event.isControlDown();
            case MACOS:
                return event.isMetaDown();
            case LINUX:
                return event.isMetaDown();
            default:
                return event.isControlDown();
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////// LISTVIEW UTILS //////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    public static void scrollToWrapped(ListView listView, int direction, int rowsVisible) {
        int previousIndex = listView.getSelectionModel().getSelectedIndex();
        int newIndex = shiftIndexWrapped(previousIndex, direction, listView.getItems().size());

        listView.getSelectionModel().select(newIndex);

        int firstVisibleCell = getFirstVisibleCell(listView);
        int lastVisibleCell = firstVisibleCell + rowsVisible - 1;

        // Handle scrolling based on direction and index boundaries
        if (direction > 0) {
            // If wrapping around from last to first, scroll to the top
            if (previousIndex == listView.getItems().size() - 1 && newIndex == 0) {
                listView.scrollTo(0);
            } else if (newIndex > lastVisibleCell) {
                // Otherwise, gradually scroll down
                listView.scrollTo(firstVisibleCell + 1);
            }
        } else {
            // If wrapping around from first to last, scroll to the bottom
            if (previousIndex == 0 && newIndex == listView.getItems().size() - 1) {
                listView.scrollTo(listView.getItems().size() - 1);
            } else if (newIndex < firstVisibleCell) {
                // Otherwise, gradually scroll up
                listView.scrollTo(firstVisibleCell - 1);
            }
        }
    }

    public static int shiftIndex(int index, int amount, int size) {
        int newIndex = index + amount;

        // Ensure the new index stays within the bounds [0, size]
        newIndex = newIndex < 0 ? 0 : newIndex;
        newIndex = newIndex > size - 1 ? size - 1 : newIndex;
        return newIndex;
    }

    public static int shiftIndexWrapped(int index, int amount, int size) {
        int newIndex = (index + amount) % size; // Calculate the new index by adding the amount to the current index
        if (newIndex < 0) { // If the new index is negative, wrap it around to the end of the list
            newIndex += size;
        }
        return newIndex;
    }

    public static Integer getFirstVisibleCell(ListView listView) {
        ListViewSkin<?> skin = (ListViewSkin<?>) listView.getSkin(); // Access the ListView's skin to get the VirtualFlow
        if (skin == null) {
            return null; // If the skin is not set, return
        }
        VirtualFlow<?> virtualFlow = (VirtualFlow<?>) skin.getChildren().get(0);
        if (virtualFlow == null) {
            return null; // If the VirtualFlow is not found, return
        }
        return virtualFlow.getFirstVisibleCell().getIndex(); // Get the index of the first visible cell
    }

    // Method to determine the height of a cell in the ListView
    public static double getCellHeight(ListView<String> listView) {
        return listView.lookup(".list-cell").getLayoutBounds().getHeight();
    }
}

package vplcore;

import java.io.File;
import java.util.Properties;
import java.util.prefs.Preferences;
import vplcore.util.FileUtils;
import vplcore.util.SystemUtils.OperatingSystem;
import vplcore.util.SystemUtils;

/**
 *
 * @author joostmeulenkamp
 */
public class Config {

    private static Config config;

    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(Config.class);
    private static final String LAST_DIRECTORY_KEY = "lastOpenedDirectory";

    private static final String LIBRARY_DIRECTORY = "lib" + File.separatorChar;
    private static final String BUILD_DIRECTORY = "build" + File.separatorChar;
    private static final String CONFIG_DIRECTORY = "config" + File.separatorChar;
    private static final String SETTINGS_FILE = "settings.txt";
    private static final String RESOURCES_DIRECTORY = "src" + File.separatorChar + "main" + File.separatorChar + "resources" + File.separatorChar;
    private static final String ICONS_DIRECTORY = RESOURCES_DIRECTORY + "fontawesome-svg" + File.separatorChar;

    private String appRootDirectory;
    private OperatingSystem operatingSystem;
    private Properties settings;

    private Config() {
//        this.appRootDirectory = Util.getAppRootDirectory(this, BUILD_DIRECTORY);
//        this.operatingSystem = Util.determineOperatingSystem();
    }

    public static Config get() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }

    private static void loadConfig() {
        config = new Config();
        config.appRootDirectory = SystemUtils.getAppRootDirectory(config, BUILD_DIRECTORY);

        FileUtils.createDirectory(new File(config.appRootDirectory + LIBRARY_DIRECTORY));
        FileUtils.createDirectory(new File(config.appRootDirectory + CONFIG_DIRECTORY));;
        File settingsFile = new File(config.appRootDirectory + CONFIG_DIRECTORY + SETTINGS_FILE);
        FileUtils.createFile(settingsFile);

        config.operatingSystem = SystemUtils.determineOperatingSystem();
        config.settings = FileUtils.loadProperties(settingsFile);
    }

    public String appRootDirectory() {
        return appRootDirectory;
    }

    public String libraryDirectory() {
        return appRootDirectory + LIBRARY_DIRECTORY;
    }

    public String resourcesDirectory() {
        return RESOURCES_DIRECTORY;
    }

    public String iconsDirectory() {
        return ICONS_DIRECTORY;
    }

    public OperatingSystem operatingSystem() {
        return operatingSystem;
    }

    public String stylesheets() {
//        String defaultStyle = "css/flat_dark.css";
//        String defaultStyle = "css/flat_singer.css";
        String defaultStyle = "css/flat_white.css";
        return settings.getProperty("stylesheets", defaultStyle);
    }

    public static File getLastOpenedDirectory() {
        String path = PREFERENCES.get(LAST_DIRECTORY_KEY, null);
        if (path != null) {
            File file = new File(path);
            if (file.exists() && file.isDirectory()) {
                return file;
            }
        }
        return null;
    }

    public static void setLastOpenedDirectory(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            PREFERENCES.put(LAST_DIRECTORY_KEY, file.getPath());
        } else {
            PREFERENCES.put(LAST_DIRECTORY_KEY, file.getParent());
        }
    }

}

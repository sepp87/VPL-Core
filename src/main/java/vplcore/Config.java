package vplcore;

import java.io.File;
import java.util.Properties;
import vplcore.Util.OperatingSystem;

/**
 *
 * @author joostmeulenkamp
 */
public class Config {

    private static Config config;

    private static final String LIBRARY_DIRECTORY = "lib" + File.separatorChar;
    private static final String BUILD_DIRECTORY = "build" + File.separatorChar;
    private static final String CONFIG_DIRECTORY = "config" + File.separatorChar;
    private static final String SETTINGS_FILE = "settings.txt";

    private String appRootDirectory;
    private OperatingSystem operatingSystem;
    private Properties settings;

    private Config() {

    }

    public static Config get() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }

    private static void loadConfig() {
        config = new Config();

        Util.createDirectory(new File(config.appRootDirectory + LIBRARY_DIRECTORY));
        Util.createDirectory(new File(config.appRootDirectory + CONFIG_DIRECTORY));;
        File settingsFile = new File(config.appRootDirectory + CONFIG_DIRECTORY + SETTINGS_FILE);
        Util.createFile(settingsFile);

        config.appRootDirectory = Util.getAppRootDirectory(config, BUILD_DIRECTORY);
        config.operatingSystem = Util.determineOperatingSystem();
        config.settings = Util.loadProperties(settingsFile);
    }

    public String appRootDirectory() {
        return appRootDirectory;
    }

    public String libraryDirectory() {
        return appRootDirectory + LIBRARY_DIRECTORY;
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

}

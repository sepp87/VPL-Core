package vplcore;

import java.io.File;
import vplcore.Util.OperatingSystem;

/**
 *
 * @author joostmeulenkamp
 */
public class Config {

    private static Config config;

    private static final String LIBRARY_DIRECTORY = "lib" + File.separatorChar;
    private static final String BUILD_DIRECTORY = "build" + File.separatorChar;

    private final String appRootDirectory;
    private final OperatingSystem operatingSystem;

    private Config(String root, OperatingSystem os) {
        this.appRootDirectory = root;
        this.operatingSystem = os;
    }

    public static Config get() {
        if (config == null) {
            loadConfig();
        }
        return config;
    }

    private static void loadConfig() {
        String root = Util.getAppRootDirectory(config, BUILD_DIRECTORY);
        OperatingSystem os = Util.determineOperatingSystem();
        config = new Config(root, os);
        Util.createDirectory(new File(config.appRootDirectory + LIBRARY_DIRECTORY));
    }

    public String getAppRootDirectory() {
        return appRootDirectory;
    }

    public String getLibraryDirectory() {
        return appRootDirectory + LIBRARY_DIRECTORY;
    }

    public OperatingSystem getOperatingSystem() {
        return operatingSystem;
    }
}

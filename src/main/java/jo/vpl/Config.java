package jo.vpl;

import java.io.File;
import jo.vpl.core.Util;

/**
 *
 * @author joostmeulenkamp
 */
public class Config {

    private static Config config;

    private String appRootDirectory;

    private static final String LIBRARY_DIRECTORY = "lib" + File.separatorChar;
    private static final String BUILD_DIRECTORY = "build" + File.separatorChar;

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
        config.appRootDirectory = Util.getAppRootDirectory(config, BUILD_DIRECTORY);
        Util.createDirectory(new File(config.appRootDirectory + LIBRARY_DIRECTORY));
    }

    public String getAppRootDirectory() {
        return appRootDirectory;
    }

    public String getLibraryDirectory() {
        return appRootDirectory + LIBRARY_DIRECTORY;
    }
}

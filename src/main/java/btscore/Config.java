package btscore;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Properties;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import btscore.utils.FileUtils;
import btscore.utils.SystemUtils.OperatingSystem;
import btscore.utils.SystemUtils;

/**
 *
 * @author joostmeulenkamp
 */
public class Config {

    private static Config config;

    private final BooleanProperty wirelessVisible = new SimpleBooleanProperty(this, "wirelessVisible", false);

    public static final boolean TYPE_SENSITIVE = true;
    public static final String XML_NAMESPACE = "btsxml";
    public static final String XML_FILE_EXTENSION = "btsxml";

    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(Config.class);
    private static final String LAST_DIRECTORY_KEY = "lastOpenedDirectory";
    private static final String SHOW_HELP_KEY = "showHelp";

    private static final String LIBRARY_DIRECTORY = "lib" + File.separatorChar;
    private static final String BUILD_DIRECTORY = "build" + File.separatorChar;
    private static final String CONFIG_DIRECTORY = "config" + File.separatorChar;
    private static final String SETTINGS_FILE = "settings.txt";
    private static final String ICONS_DIRECTORY = "fontawesome-svg" + File.separatorChar;

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

    public static boolean showHelpOnStartup() {
        boolean showWelcomeDialog = PREFERENCES.getBoolean(SHOW_HELP_KEY, true);
        return showWelcomeDialog;
    }

    public static void setShowHelpOnStartup(boolean show) {
        PREFERENCES.putBoolean(SHOW_HELP_KEY, show);
    }

    public static void setStylesheets(Scene scene) {
        // Load the CSS from classpath using ClassLoader
        String stylesheetPath = Config.get().stylesheets(); // Adjust based on your structure
        URL resourceUrl = Config.get().getClass().getClassLoader().getResource(stylesheetPath);

        if (resourceUrl != null) {
            scene.getStylesheets().add(resourceUrl.toExternalForm());
            System.out.println("CSS Loaded: " + resourceUrl.toExternalForm());
        } else {
            System.err.println("Stylesheet not found: " + stylesheetPath);
            return;
        }

        // Enable file watching only if running in IDE (not in JAR)
        Path filePath = Paths.get("src/main/resources/", stylesheetPath); // Path in IDE
        if (Files.exists(filePath)) {
            watchForCssChanges(scene, filePath);
        }
    }

    private static void watchForCssChanges(Scene scene, Path path) {
        new Thread(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                path.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
                while (true) {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        if (event.context().toString().equals(path.getFileName().toString())) {
                            Platform.runLater(() -> {
                                scene.getStylesheets().clear();
                                scene.getStylesheets().add(path.toUri().toString());
                                System.out.println("CSS Reloaded!");
                            });
                        }
                    }
                    key.reset();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

}

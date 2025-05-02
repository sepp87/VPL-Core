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
import java.util.HashMap;
import java.util.Map;

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
    private static final String PREF_LAST_DIRECTORY = "lastOpenedDirectory";
    private static final String PREF_SHOW_HELP = "showHelp";
    private static final String PREF_STYLESHEET = "stylesheet";

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

    public static File getLastOpenedDirectory() {
        String path = PREFERENCES.get(PREF_LAST_DIRECTORY, null);
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
            PREFERENCES.put(PREF_LAST_DIRECTORY, file.getPath());
        } else {
            PREFERENCES.put(PREF_LAST_DIRECTORY, file.getParent());
        }
    }

    public static boolean showHelpOnStartup() {
        boolean showWelcomeDialog = PREFERENCES.getBoolean(PREF_SHOW_HELP, true);
        return showWelcomeDialog;
    }

    public static void setShowHelpOnStartup(boolean show) {
        PREFERENCES.putBoolean(PREF_SHOW_HELP, show);
    }

    public String getStylesheet() {
        return PREFERENCES.get(PREF_STYLESHEET, STYLESHEETS.get("Light"));
    }

    public void setStylesheet(String css) {
        // save app provided stylesheet to preferences
        if (STYLESHEETS.containsKey(css)) {
            PREFERENCES.put(PREF_STYLESHEET, css);
        }
        if (true) {
            return;
        }
        // TODO save user provided stylesheet to preferences
        File stylesheet = new File(css);
        if (stylesheet.exists()) { // TODO check if file is really a stylesheet
            PREFERENCES.put(PREF_STYLESHEET, css);
        }
    }

    public static void setStylesheets(Scene scene) {
        // Load the CSS from classpath using ClassLoader
        String stylesheetPath = Config.get().getStylesheet(); // Adjust based on your structure
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

    public static final Map<String, String> STYLESHEETS = new HashMap<String, String>() {
        {
            put("Dark", "css/dark_mode.css");
            put("Light", "css/flat_white.css");
            put("Singer", "css/flat_singer.css");
        }
    };
}

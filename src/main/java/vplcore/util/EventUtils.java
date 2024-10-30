package vplcore.util;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import vplcore.Config;

/**
 *
 * @author Joost
 */
public class EventUtils {

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
}

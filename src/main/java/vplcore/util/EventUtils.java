package vplcore.util;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
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

    public static int leftClickCount(MouseEvent event) {
        int result = -1;
        if (event.getButton() == MouseButton.PRIMARY && event.isStillSincePress()) {
            result = event.getClickCount();
        }
        return result;
    }

    public static boolean isLeftClick(MouseEvent event) {
        return event.getButton() == MouseButton.PRIMARY && event.isStillSincePress();
    }

    public static boolean isDoubleClick(MouseEvent event) {
        return event.getButton() == MouseButton.PRIMARY && event.isStillSincePress() && event.getClickCount() == 2;
    }

    public static boolean isRightClick(MouseEvent event) {
        return event.getButton() == MouseButton.SECONDARY && event.isStillSincePress();
    }
}

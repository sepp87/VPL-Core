package vplcore.workspace.radialmenu;

import java.util.ArrayList;
import java.util.List;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import vplcore.IconType;

/**
 *
 * @author joostmeulenkamp
 */
public class NewRadialTest {

    public static void createNewRadial() {
        // create menu items
        NewRadialMenuItem openFileItem = new NewRadialMenuItem(IconType.FA_FOLDER_OPEN_O, "Open\nfile");
        NewRadialMenuItem saveFileItem = new NewRadialMenuItem(IconType.FA_FLOPPY_O, "Save\nfile");
        NewRadialMenuItem zoomToFitItem = new NewRadialMenuItem(IconType.FA_SEARCH, "Zoom\nto fit");
        NewRadialSubMenu alignItem = new NewRadialSubMenu(IconType.FA_SORT_AMOUNT_ASC, "Align");
        NewRadialMenuItem copyItem = new NewRadialMenuItem(IconType.FA_CLONE, "Copy");
        NewRadialMenuItem pasteItem = new NewRadialMenuItem(IconType.FA_CLIPBOARD, "Paste");
        NewRadialMenuItem groupItem = new NewRadialMenuItem(IconType.FA_OBJECT_GROUP, "Group");
        NewRadialMenuItem newFileItem = new NewRadialMenuItem(IconType.FA_FILE_O, "New\nfile");

        // add event handlers
        openFileItem.setOnMouseClicked(handle_openFileClicked);
        saveFileItem.setOnMouseClicked(handle_saveFileClicked);
        zoomToFitItem.setOnMouseClicked(handle_zoomToFitClicked);
        copyItem.setOnMouseClicked(handle_copyClicked);
        pasteItem.setOnMouseClicked(handle_pasteClicked);
        groupItem.setOnMouseClicked(handle_groupClicked);
        newFileItem.setOnMouseClicked(handle_newFileClicked);

        // create menu
        List<NewRadialMenuItem> items = new ArrayList<>();
        items.add(openFileItem);
        items.add(saveFileItem);
        items.add(zoomToFitItem);
        items.add(alignItem);
        items.add(copyItem);
        items.add(pasteItem);
        items.add(groupItem);
        items.add(newFileItem);
        NewRadialMenu menu = new NewRadialMenu(items);

        // build align sub menu
        List<NewRadialMenuItem> alignItems = new ArrayList<>();
        NewRadialMenuItem alignTopItem = new NewRadialMenuItem(IconType.FA_ALIGN_LEFT, "Align\ntop", 90.);
        NewRadialMenuItem alignVerticallyItem = new NewRadialMenuItem(IconType.FA_ALIGN_CENTER, "Align\nvertically");
        NewRadialMenuItem alignRightItem = new NewRadialMenuItem(IconType.FA_ALIGN_RIGHT, "Align\nright");
        NewRadialMenuItem alignBottomItem = new NewRadialMenuItem(IconType.FA_ALIGN_RIGHT, "Align\nbottom", 90.);
        NewRadialMenuItem alignHorizontallyItem = new NewRadialMenuItem(IconType.FA_ALIGN_CENTER, "Align\nhorizontally", 90.);
        NewRadialMenuItem alignLefItem = new NewRadialMenuItem(IconType.FA_ALIGN_LEFT, "Align\nleft");

        // create sub menu
        alignItems.add(alignTopItem);
        alignItems.add(alignVerticallyItem);
        alignItems.add(alignRightItem);
        alignItems.add(alignBottomItem);
        alignItems.add(alignHorizontallyItem);
        alignItems.add(alignLefItem);
        menu.addSubMenu(alignItem, alignItems);

        // add event handlers
        alignTopItem.setOnMouseClicked(handle_alignTopClicked);
        alignVerticallyItem.setOnMouseClicked(handle_alignVerticallyClicked);
        alignRightItem.setOnMouseClicked(handle_alignRightClicked);
        alignBottomItem.setOnMouseClicked(handle_alignBottomClicked);
        alignHorizontallyItem.setOnMouseClicked(handle_alignHorizontallyClicked);
        alignLefItem.setOnMouseClicked(handle_alignLeftClicked);
    }

    static EventHandler<MouseEvent> handle_openFileClicked = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
        }
    };

    static EventHandler<MouseEvent> handle_saveFileClicked = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
        }
    };

    static EventHandler<MouseEvent> handle_zoomToFitClicked = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
        }
    };

    static EventHandler<MouseEvent> handle_copyClicked = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
        }
    };
    static EventHandler<MouseEvent> handle_pasteClicked = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
        }
    };
    static EventHandler<MouseEvent> handle_groupClicked = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
        }
    };
    static EventHandler<MouseEvent> handle_newFileClicked = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
        }
    };
    static EventHandler<MouseEvent> handle_alignTopClicked = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
        }
    };
    static EventHandler<MouseEvent> handle_alignVerticallyClicked = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
        }
    };
    static EventHandler<MouseEvent> handle_alignRightClicked = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
        }
    };
    static EventHandler<MouseEvent> handle_alignBottomClicked = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
        }
    };
    static EventHandler<MouseEvent> handle_alignHorizontallyClicked = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
        }
    };
    static EventHandler<MouseEvent> handle_alignLeftClicked = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
        }
    };
}

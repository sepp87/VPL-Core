package vplcore.editor;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

/**
 *
 * @author joostmeulenkamp
 */
public class MenuBarView extends MenuBar {

    private final MenuItem group;

    public MenuBarView() {
        this.setUseSystemMenuBar(false);

        Menu fileMenu = new Menu("File");
        MenuItem newFile = new MenuItem("New file", "NEW_FILE");
        MenuItem openFile = new MenuItem("Open file", "OPEN_FILE");
        MenuItem save = new MenuItem("Save", "SAVE_FILE");
        fileMenu.getItems().addAll(newFile, openFile, save);

        Menu editMenu = new Menu("Edit");
        MenuItem copy = new MenuItem("Copy", "COPY_BLOCKS");
        MenuItem paste = new MenuItem("Paste", "PASTE_BLOCKS");
        MenuItem delete = new MenuItem("Delete", "DELETE_SELECTED_BLOCKS");
        this.group = new MenuItem("Group", "GROUP_BLOCKS");
        Menu alignMenu = new Menu("Align");
        editMenu.getItems().addAll(copy, paste, delete, group, alignMenu);

        MenuItem alignLeft = new MenuItem("Align left", "ALIGN_LEFT");
        MenuItem alignVertically = new MenuItem("Align vertically", "ALIGN_VERTICALLY");
        MenuItem alignRight = new MenuItem("Align right", "ALIGN_RIGHT");
        MenuItem alignTop = new MenuItem("Align top", "ALIGN_TOP");
        MenuItem alignHorizontally = new MenuItem("Align horizontally", "ALIGN_HORIZONTALLY");
        MenuItem alignBottom = new MenuItem("Align bottom", "ALIGN_BOTTOM");
        alignMenu.getItems().addAll(alignLeft, alignVertically, alignRight, alignTop, alignHorizontally, alignBottom);

        Menu viewMenu = new Menu("View");
        MenuItem zoomToFit = new MenuItem("Zoom to fit", "ZOOM_TO_FIT");
        MenuItem zoomIn = new MenuItem("Zoom in", "ZOOM_IN");
        MenuItem zoomOut = new MenuItem("Zoom out", "ZOOM_OUT");
        viewMenu.getItems().addAll(zoomToFit, zoomIn, zoomOut);

        Menu extrasMenu = new Menu("Extras");
        MenuItem reloadPlugins = new MenuItem("Reload plugins", "RELOAD_PLUGINS");
        MenuItem logErrors = new MenuItem("Log errors", "LOG_ERRORS");
        extrasMenu.getItems().addAll(reloadPlugins, logErrors);

        this.getMenus().addAll(fileMenu, editMenu, viewMenu, extrasMenu);
    }

    public MenuItem getGroupMenuItem() {
        return group;
    }

    public class MenuItem extends javafx.scene.control.MenuItem {

        public MenuItem(String name, String id) {
            super(name);
            this.idProperty().set(id);
        }
    }

    public List<MenuItem> getAllMenuItems() {
        List<MenuItem> result = new ArrayList<>();
        for (Menu menu : this.getMenus()) {
            result.addAll(getMenuItemsFrom(menu));
        }
        return result;
    }

    private List<MenuItem> getMenuItemsFrom(Menu menu) {
        List<MenuItem> result = new ArrayList<>();
        for (javafx.scene.control.MenuItem item : menu.getItems()) {
            if (item instanceof MenuItem menuItem) {
                result.add(menuItem);
            } else if (item instanceof Menu subMenu) {
                result.addAll(getMenuItemsFrom(subMenu));
            }
        }
        return result;
    }

}

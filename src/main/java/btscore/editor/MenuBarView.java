package btscore.editor;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;

/**
 *
 * @author joostmeulenkamp
 */
public class MenuBarView extends MenuBar {

    private final Menu fileMenu;
    private final MenuItem save;

    private final Menu editMenu;
    private final MenuItem undo;
    private final MenuItem redo;
    private final MenuItem group;
    
    private final Menu styleMenu;

    public MenuBarView() {
        this.setUseSystemMenuBar(false);

        fileMenu = new Menu("File");
        MenuItem newFile = new MenuItem("New file", "NEW_FILE");
        MenuItem openFile = new MenuItem("Open file", "OPEN_FILE");
        save = new MenuItem("Save", "SAVE_FILE");
        MenuItem saveAs = new MenuItem("Save as", "SAVE_AS_FILE");
        fileMenu.getItems().addAll(newFile, openFile, save, saveAs);

        this.editMenu = new Menu("Edit");
        this.undo = new MenuItem("Undo", "UNDO");
        this.redo = new MenuItem("Redo", "REDO");
        MenuItem copy = new MenuItem("Copy", "COPY_BLOCKS");
        MenuItem paste = new MenuItem("Paste", "PASTE_BLOCKS");
        MenuItem delete = new MenuItem("Delete", "DELETE_SELECTED_BLOCKS");
        this.group = new MenuItem("Group", "GROUP_BLOCKS");
        Menu alignMenu = new Menu("Align");
        editMenu.getItems().addAll(undo, redo, copy, paste, delete, group, alignMenu);

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
        MenuItem help = new MenuItem("Help", "HELP");
        this.styleMenu = new Menu("Style");
        extrasMenu.getItems().addAll(reloadPlugins, logErrors, help, styleMenu);

        MenuItem light = new MenuItem("Light", "STYLESHEET");
        MenuItem dark = new MenuItem("Dark", "STYLESHEET");
        MenuItem singer = new MenuItem("Singer", "STYLESHEET");
        styleMenu.getItems().addAll(light, dark, singer);

        logErrors.setDisable(true);

        this.getMenus().addAll(fileMenu, editMenu, viewMenu, extrasMenu);
    }

    public Menu getFileMenu() {
        return fileMenu;
    }

    public MenuItem getSaveMenuItem() {
        return save;
    }

    public Menu getEditMenu() {
        return editMenu;
    }

    public MenuItem getUndoMenuItem() {
        return undo;
    }

    public MenuItem getRedoMenuItem() {
        return redo;
    }

    public MenuItem getGroupMenuItem() {
        return group;
    }
    
    public List<MenuItem> getStyleMenuItems(){
        return getMenuItemsFrom(styleMenu);
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

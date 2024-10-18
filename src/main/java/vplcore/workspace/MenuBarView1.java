package vplcore.workspace;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import vplcore.workspace.Actions.ActionType;

/**
 *
 * @author joostmeulenkamp
 */
public class MenuBarView1 {

    private MenuBar menuBar;

    public MenuBarView1(Workspace workspace) {
        this.menuBar = buildMenuBar();
    }

    private MenuBar buildMenuBar() {
        MenuBar menu = new MenuBar();
        menu.setUseSystemMenuBar(false);

        Menu file = new Menu("File");
        MenuBarItem newFile = new MenuBarItem(ActionType.NEW_FILE, "New file");
        MenuBarItem openFile = new MenuBarItem(ActionType.OPEN_FILE, "Open file");
        MenuBarItem save = new MenuBarItem(ActionType.SAVE_FILE, "Save");
        file.getItems().addAll(newFile, openFile, save);

        Menu edit = new Menu("Edit");
        MenuBarItem copy = new MenuBarItem(ActionType.COPY_BLOCKS, "Copy");
        MenuBarItem paste = new MenuBarItem(ActionType.PASTE_BLOCKS, "Paste");
        MenuBarItem delete = new MenuBarItem(ActionType.DELETE_BLOCKS, "Delete");
        MenuBarItem group = new MenuBarItem(ActionType.GROUP_BLOCKS, "Group");
        Menu align = new Menu("Align");
        edit.getItems().addAll(copy, paste, delete, group, align);

        MenuBarItem alignLeft = new MenuBarItem(ActionType.ALIGN_LEFT, "Align left");
        MenuBarItem alignVertically = new MenuBarItem(ActionType.ALIGN_VERTICALLY, "Align vertically");
        MenuBarItem alignRight = new MenuBarItem(ActionType.ALIGN_RIGHT, "Align right");
        MenuBarItem alignTop = new MenuBarItem(ActionType.ALIGN_TOP, "Align top");
        MenuBarItem alignHorizontally = new MenuBarItem(ActionType.ALIGN_HORIZONTALLY, "Align horizontally");
        MenuBarItem alignBottom = new MenuBarItem(ActionType.ALIGN_BOTTOM, "Align bottom");
        align.getItems().addAll(alignLeft, alignVertically, alignRight, alignTop, alignHorizontally, alignBottom);

        Menu view = new Menu("View");
        MenuBarItem zoomToFit = new MenuBarItem(ActionType.ZOOM_TO_FIT, "Zoom to fit");
        MenuBarItem zoomIn = new MenuBarItem(ActionType.ZOOM_IN, "Zoom in");
        MenuBarItem zoomOut = new MenuBarItem(ActionType.ZOOM_OUT, "Zoom out");
        view.getItems().addAll(zoomToFit, zoomIn, zoomOut);

        menu.getMenus().addAll(file, edit, view);

        return menu;
    }

    public class MenuBarItem extends MenuItem {

        ActionType action;

        MenuBarItem(ActionType action, String string) {
            super(string);
            this.action = action;
        }

        public ActionType getAction() {
            return action;
        }
    }

    public List<MenuBarItem> getAllMenuBarItems() {
        List<MenuBarItem> result = new ArrayList<>();
        for (Menu menu : menuBar.getMenus()) {
            result.addAll(getMenuBarItemsFrom(menu));
        }
        return result;
    }

    private List<MenuBarItem> getMenuBarItemsFrom(Menu menu) {
        List<MenuBarItem> result = new ArrayList<>();
        for (MenuItem item : menu.getItems()) {
            if (item instanceof MenuBarItem menuBarItem) {
                result.add(menuBarItem);
            } else if (item instanceof Menu subMenu) {
                result.addAll(getMenuBarItemsFrom(subMenu));
            }
        }
        return result;
    }

}

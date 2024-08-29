package vplcore.workspace;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import vplcore.workspace.Actions.ActionType;

/**
 *
 * @author joostmeulenkamp
 */
public class MenuBarConfigurator {

    private Workspace workspace;
    private MenuBar menuBar;

    public MenuBarConfigurator(Workspace workspace) {
        this.workspace = workspace;
    }

    public MenuBar configure() {
        menuBar = new MenuBar();
        menuBar.setUseSystemMenuBar(false);

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

        menuBar.getMenus().addAll(file, edit, view);
        
        // add event handlers
        newFile.setOnAction(handleMenuBarItemClicked);
        openFile.setOnAction(handleMenuBarItemClicked);
        save.setOnAction(handleMenuBarItemClicked);
        copy.setOnAction(handleMenuBarItemClicked);
        paste.setOnAction(handleMenuBarItemClicked);
        delete.setOnAction(handleMenuBarItemClicked);
        group.setOnAction(handleMenuBarItemClicked);
        alignLeft.setOnAction(handleMenuBarItemClicked);
        alignVertically.setOnAction(handleMenuBarItemClicked);
        alignRight.setOnAction(handleMenuBarItemClicked);
        alignTop.setOnAction(handleMenuBarItemClicked);
        alignHorizontally.setOnAction(handleMenuBarItemClicked);
        alignBottom.setOnAction(handleMenuBarItemClicked);
        zoomToFit.setOnAction(handleMenuBarItemClicked);
        zoomIn.setOnAction(handleMenuBarItemClicked);
        zoomOut.setOnAction(handleMenuBarItemClicked);
        
        return menuBar;
    }


    class MenuBarItem extends MenuItem {

        ActionType action;

        MenuBarItem(ActionType action, String string) {
            super(string);
            this.action = action;
        }

        public ActionType getAction() {
            return action;
        }
    }

    private final EventHandler<ActionEvent> handleMenuBarItemClicked = new EventHandler<>() {
        @Override
        public void handle(ActionEvent event) {
            @SuppressWarnings("unchecked")
            MenuBarItem item = (MenuBarItem) event.getSource();
            workspace.actions.perform(item.getAction());
        }
    };
}

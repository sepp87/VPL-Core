package vplcore;

import vplcore.editor.EditorController;
import vplcore.editor.EditorView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vplcore.editor.KeyboardController;
import vplcore.editor.MenuBarController;
import vplcore.editor.MenuBarView;
import vplcore.editor.PanController;
import vplcore.editor.SelectionRectangleController;
import vplcore.editor.SelectionRectangleView;
import vplcore.editor.radialmenu.RadialMenuController;
import vplcore.editor.radialmenu.RadialMenuView;
import vplcore.workspace.Workspace;
import vplcore.editor.ZoomModel;
import vplcore.editor.ZoomController;
import vplcore.editor.ZoomView;
import vplcore.workspace.Actions;

/**
 *
 * @author joostmeulenkamp
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        boolean addNewStuff = true;

        // Initialize models
        ZoomModel zoomModel = new ZoomModel();

        // Initialize views
        // WorkspaceView
        // BlockSearchView
        SelectionRectangleView selectionRectangleView = new SelectionRectangleView();
        ZoomView zoomView = new ZoomView(zoomModel);
        RadialMenuView radialMenuView = new RadialMenuView();
        Workspace workspace = new Workspace(zoomModel);
        MenuBarView menuBarView = new MenuBarView();
        EditorView editorView = new EditorView(radialMenuView, workspace, menuBarView, zoomView, selectionRectangleView);

        // Temporary stuff
        ZoomController zoomController = new ZoomController(zoomModel, workspace, zoomView);
        Actions actions = new Actions(workspace, zoomController, zoomModel);

        // Initialize controllers
        // WorkspaceController
        // BlockSearchController
        SelectionRectangleController selectionRectangleController = new SelectionRectangleController(selectionRectangleView, workspace);
        KeyboardController keyboardController = new KeyboardController(actions);
        PanController panController = new PanController(workspace, zoomModel);
        RadialMenuController radialMenuController = new RadialMenuController(radialMenuView, workspace, actions);
        MenuBarController menuBarController = new MenuBarController(menuBarView, workspace, actions);
        EditorController editorController = new EditorController(editorView, radialMenuController, workspace, zoomController, panController, keyboardController, selectionRectangleController);

        // Setup scene
        Scene scene = new Scene(editorView, 800, 800);
        stage.setScene(scene);
        stage.setTitle("Workspace");
        stage.show();
        stage.setFullScreen(false);

        editorView.test();
    }

}

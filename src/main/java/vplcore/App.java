package vplcore;

import java.io.File;
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
import vplcore.workspace.WorkspaceModel;
import vplcore.editor.ZoomController;
import vplcore.editor.ZoomView;
import vplcore.graph.io.GraphLoader;
import vplcore.editor.BlockSearchController;
import vplcore.editor.BlockSearchView;
import vplcore.editor.EditorModel;
import vplcore.workspace.ActionManager;

/**
 *
 * @author joostmeulenkamp
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        EventRouter eventRouter = new EventRouter();
        eventRouter.fireEvent(new FocusReleasedEvent());

        // Initialize models
        EditorModel editorModel = new EditorModel();
        WorkspaceModel workspaceModel = new WorkspaceModel();

        // Initialize views
        // WorkspaceView
        BlockSearchView blockSearchView = new BlockSearchView();
        SelectionRectangleView selectionRectangleView = new SelectionRectangleView();
        ZoomView zoomView = new ZoomView();
        RadialMenuView radialMenuView = new RadialMenuView();
        Workspace workspace = new Workspace(workspaceModel, editorModel);
        MenuBarView menuBarView = new MenuBarView();
        EditorView editorView = new EditorView(radialMenuView, workspace, menuBarView, zoomView, selectionRectangleView, blockSearchView);

        // Temporary stuff
        ActionManager actionManager = new ActionManager(workspace);
        ZoomController zoomController = new ZoomController(actionManager, editorModel, workspaceModel, zoomView);

        // Initialize controllers
        // WorkspaceController
        BlockSearchController blockSearchController = new BlockSearchController(editorModel, blockSearchView, actionManager);
        SelectionRectangleController selectionRectangleController = new SelectionRectangleController(actionManager, editorModel, selectionRectangleView);
        KeyboardController keyboardController = new KeyboardController(actionManager);
        PanController panController = new PanController(editorModel, workspaceModel);
        RadialMenuController radialMenuController = new RadialMenuController(actionManager, editorModel, radialMenuView);
        MenuBarController menuBarController = new MenuBarController(actionManager, menuBarView);
        EditorController editorController = new EditorController(editorView, radialMenuController, workspace, zoomController, panController, keyboardController, selectionRectangleController, blockSearchController);

        // Setup scene
        Scene scene = new Scene(editorView, 800, 800);
        stage.setScene(scene);
        stage.setTitle("Workspace");
        stage.show();
        stage.setFullScreen(false);

        GraphLoader.deserialize(new File("build/vplxml/string-to-text.vplxml"), workspace, workspaceModel);
        editorView.test();
    }

}

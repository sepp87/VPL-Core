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
import vplcore.workspace.WorkspaceController;
import vplcore.workspace.WorkspaceModel;
import vplcore.editor.ZoomController;
import vplcore.editor.ZoomView;
import vplcore.graph.io.GraphLoader;
import vplcore.editor.BlockSearchController;
import vplcore.editor.BlockSearchView;
import vplcore.editor.EditorModel;
import vplcore.workspace.ActionManager;
import vplcore.workspace.WorkspaceView;

/**
 *
 * @author joostmeulenkamp
 */
public class App extends Application {

    private static Stage stage;
    
    @Override
    public void start(Stage stage) throws Exception {
        
        this.stage = stage;

        EventRouter eventRouter = new EventRouter();
        eventRouter.fireEvent(new FocusReleasedEvent());

        // Initialize models
        EditorModel editorModel = new EditorModel();
        WorkspaceModel workspaceModel = new WorkspaceModel();

        // Initialize views
        // WorkspaceView
        WorkspaceView workspaceView = new WorkspaceView();
        BlockSearchView blockSearchView = new BlockSearchView();
        SelectionRectangleView selectionRectangleView = new SelectionRectangleView();
        ZoomView zoomView = new ZoomView();
        RadialMenuView radialMenuView = new RadialMenuView();
        MenuBarView menuBarView = new MenuBarView();
        EditorView editorView = new EditorView(radialMenuView, workspaceView, menuBarView, zoomView, selectionRectangleView, blockSearchView);

        // Initialize workspace controller and supporting managers
        WorkspaceController workspaceController = new WorkspaceController(editorModel, workspaceModel, workspaceView);
        ActionManager actionManager = new ActionManager(workspaceController);

        // Initialize controllers
        ZoomController zoomController = new ZoomController(actionManager, editorModel, workspaceModel, zoomView);
        BlockSearchController blockSearchController = new BlockSearchController(editorModel, blockSearchView, actionManager);
        SelectionRectangleController selectionRectangleController = new SelectionRectangleController(actionManager, editorModel, selectionRectangleView);
        KeyboardController keyboardController = new KeyboardController(actionManager);
        PanController panController = new PanController(editorModel, workspaceModel);
        RadialMenuController radialMenuController = new RadialMenuController(actionManager, editorModel, radialMenuView);
        MenuBarController menuBarController = new MenuBarController(actionManager, menuBarView);
        EditorController editorController = new EditorController(editorView, radialMenuController, workspaceController, zoomController, panController, keyboardController, selectionRectangleController, blockSearchController);

        // Setup scene
        Scene scene = new Scene(editorView, 800, 800);
        stage.setScene(scene);
        stage.setTitle("Workspace");
        stage.show();
        stage.setFullScreen(false);

        GraphLoader.deserialize(new File("build/vplxml/string-to-text.vplxml"), workspaceController, workspaceModel);
        editorView.test();
    }

    public static Stage getStage() {
        return stage;
    }
    

}

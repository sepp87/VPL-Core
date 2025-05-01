package btscore;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import btscore.editor.context.EditorContext;
import btscore.editor.context.EventRouter;
import btscore.editor.EditorController;
import btscore.editor.EditorView;
import btscore.editor.KeyboardController;
import btscore.editor.MenuBarController;
import btscore.editor.MenuBarView;
import btscore.editor.PanController;
import btscore.editor.SelectionRectangleController;
import btscore.editor.SelectionRectangleView;
import btscore.editor.radialmenu.RadialMenuController;
import btscore.editor.radialmenu.RadialMenuView;
import btscore.workspace.WorkspaceController;
import btscore.workspace.WorkspaceModel;
import btscore.editor.ZoomController;
import btscore.editor.ZoomView;
import btscore.editor.BlockSearchController;
import btscore.editor.BlockSearchView;
import btscore.editor.context.ActionManager;
import btscore.workspace.WorkspaceView;
import btscore.graph.io.GraphLoader;

/**
 *
 * @author joostmeulenkamp
 */
public class App extends Application {

    public static final boolean LOG_POTENTIAL_BUGS = true;
    public static final boolean LOG_METHOD_CALLS = false;
    public static final boolean LOG_EDITOR_STATE = false;

    public static final boolean TYPE_SENSITIVE = true;
    public static final boolean CONNECTION_REFACTOR = false;

    private static final Map<String, EditorContext> CONTEXTS = new HashMap<>();

    private static final double APP_WIDTH = 800;
    private static final double APP_HEIGHT = 800;
    private static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {

        this.stage = stage;
        stage.setTitle("BlockSmith: Blocks to Script");

        // Initialize models
        WorkspaceModel workspaceModel = new WorkspaceModel();

        // Initialize views
        WorkspaceView workspaceView = new WorkspaceView();
        BlockSearchView blockSearchView = new BlockSearchView();
        SelectionRectangleView selectionRectangleView = new SelectionRectangleView();
        ZoomView zoomView = new ZoomView();
        RadialMenuView radialMenuView = new RadialMenuView();
        MenuBarView menuBarView = new MenuBarView();
        EditorView editorView = new EditorView(radialMenuView, workspaceView, menuBarView, zoomView, selectionRectangleView, blockSearchView);

        // initialize context
        EditorContext context = new EditorContext(editorView, workspaceView);
        String contextId = context.getId();
        CONTEXTS.put(contextId, context);

        // initialize EventRouter for Context
        EventRouter eventRouter = new EventRouter();
        context.initializeEventRouter(eventRouter);

        // initialize ActionManager for Context
        WorkspaceController workspaceController = new WorkspaceController(contextId, workspaceModel, workspaceView);
        ActionManager actionManager = new ActionManager(workspaceModel, workspaceController);
        context.initializeActionManager(actionManager);

        // Initialize controllers
        new ZoomController(contextId, workspaceModel, zoomView);
        new BlockSearchController(contextId, blockSearchView);
        new SelectionRectangleController(contextId, selectionRectangleView);
        new PanController(contextId, workspaceModel);
        new RadialMenuController(contextId, radialMenuView);
        new MenuBarController(contextId, menuBarView);
        new EditorController(contextId, editorView);

        // Setup scene
        Scene scene = new Scene(editorView, APP_WIDTH, APP_HEIGHT);
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(false);

//        GraphLoader.deserialize(new File("btsxml/method-block.btsxml"), workspaceModel);
//        GraphLoader.deserialize(new File("btsxml/aslist.btsxml"), workspaceModel);
//        GraphLoader.deserialize(new File("btsxml/addition.btsxml"), workspaceModel);
//        GraphLoader.deserialize(new File("btsxml/file.btsxml"), workspaceModel);
        GraphLoader.deserialize(new File("btsxml/string-to-text.btsxml"), workspaceModel);
        editorView.printMenuBarHeight();

        scene.getStylesheets().add(Config.get().stylesheets());
        Config.setStylesheets(scene);
        stage.setOnCloseRequest(event -> {
            System.out.println("Closing application...");
            System.exit(0);  // Force JVM shutdown, triggering the shutdown hook
        });

        scene.setOnKeyPressed(KeyboardController::handleShortcutTriggered);

//        WelcomeDialog.show(stage);
       
    }

    public static Stage getStage() {
        return stage;
    }

    public static EditorContext getContext(String contextId) {
        return CONTEXTS.get(contextId);
    }

    public static EditorContext getCurrentContext() {
        return CONTEXTS.values().iterator().next();
    }

}


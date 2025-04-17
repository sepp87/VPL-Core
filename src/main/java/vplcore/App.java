package vplcore;

import vplcore.context.event.FocusNotRequiredEvent;
import vplcore.context.EditorContext;
import vplcore.context.EventRouter;
import java.util.HashMap;
import java.util.Map;
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
import vplcore.editor.BlockSearchController;
import vplcore.editor.BlockSearchView;
import vplcore.context.ActionManager;
import vplcore.workspace.WorkspaceView;

/**
 *
 * @author joostmeulenkamp
 */
public class App extends Application {

    public static final boolean LOG_POTENTIAL_BUGS = true;
    public static final boolean LOG_METHOD_CALLS = false;
    public static final boolean LOG_EDITOR_STATE = false;

    public static final boolean TYPE_SENSITIVE = true;
    public static final boolean BLOCK_MVC = true;

    private static final Map<String, EditorContext> CONTEXTS = new HashMap<>();

    private static final double APP_WIDTH = 800;
    private static final double APP_HEIGHT = 800;
    private static Stage stage;

    @Override
    public void start(Stage stage) throws Exception {

        if (vplcore.App.BLOCK_MVC) {

        } else {

        }

        this.stage = stage;
        stage.setTitle("Workspace");

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
        eventRouter.fireEvent(new FocusNotRequiredEvent());
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
        EditorController editorController = new EditorController(contextId, editorView);
        new KeyboardController(contextId);

        // Setup scene
        Scene scene = new Scene(editorView, APP_WIDTH, APP_HEIGHT);
        stage.setScene(scene);
        stage.show();
        stage.setFullScreen(false);

//        GraphLoader.deserialize(new File("vplxml/method-block.vplxml"), workspaceModel);
//        GraphLoader.deserialize(new File("vplxml/aslist.vplxml"), workspaceModel);
//        GraphLoader.deserialize(new File("vplxml/addition.vplxml"), workspaceModel);
//        GraphLoader.deserialize(new File("vplxml/file.vplxml"), workspaceModel);
//        GraphLoader.deserialize(new File("vplxml/string-to-text.vplxml"), workspaceModel);
        editorView.printMenuBarHeight();

        scene.getStylesheets().add(Config.get().stylesheets());
        Config.setStylesheets(scene);
        stage.setOnCloseRequest(event -> {
            System.out.println("Closing application...");
            System.exit(0);  // Force JVM shutdown, triggering the shutdown hook
        });

        editorController.initialize(scene);
    }

    public static Stage getStage() {
        return stage;
    }

    public static EditorContext getContext(String contextId) {
        return CONTEXTS.get(contextId);
    }

}

/**
 *
 * Context - id - Action Manager - State Manager - Event Router
 *
 * ClipBoard - copied Connections - copied Blocks
 *
 */

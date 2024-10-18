package vplcore;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import vplcore.workspace.RadialMenuController;
import vplcore.workspace.RadialMenuView;
import vplcore.workspace.Workspace;

/**
 *
 * @author joostmeulenkamp
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        // Initialize views
        // WorkspaceView
        // ZoomControlsView
        // BlockSearchView
        Workspace workspace = new Workspace();
        RadialMenuView radialMenuView = new RadialMenuView();
        EditorView editorView = new EditorView(radialMenuView, workspace);

        // Initialize controllers
        // WorkspaceController
        // ZoomController
        // BlockSearchController
        RadialMenuController radialMenuController = new RadialMenuController(radialMenuView, workspace);
        EditorController editorController = new EditorController(editorView, radialMenuController, workspace);

        // Setup scene
        Scene scene = new Scene(editorView, 800, 800);
        stage.setScene(scene);
        stage.setTitle("Workspace");
        stage.show();
        stage.setFullScreen(false);

        editorView.test();
    }

}

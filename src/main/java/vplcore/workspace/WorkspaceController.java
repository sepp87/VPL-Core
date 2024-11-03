package vplcore.workspace;

import java.util.Collection;
import vplcore.MousePositionHandler;
import vplcore.graph.model.Connection;
import vplcore.graph.model.Block;
import vplcore.graph.model.BlockGroup;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Point2D;
import vplcore.editor.EditorMode;
import vplcore.editor.EditorModel;
import vplcore.graph.model.BlockInfoPanel;
import vplcore.graph.model.Port;
import vplcore.graph.util.PreConnection;

/**
 *
 * @author JoostMeulenkamp
 */
public class WorkspaceController {

    //isSaved
    public BlockInfoPanel activeBlockInfoPanel;

    public ObservableSet<Block> blocksOnWorkspace;
    public ObservableSet<Block> blocksCopied;
    public ObservableSet<Block> blocksSelectedOnWorkspace;
    public ObservableSet<Connection> connectionsOnWorkspace;
    public ObservableSet<BlockGroup> groupsOfBlocks;

    //Create connection members
    public boolean typeSensitive = true;

    private final EditorModel editorModel;
    private final WorkspaceModel model;
    private final WorkspaceView view;
    private final WorkspaceZoomHelper zoomHelper;

    //Radial menu
    public WorkspaceController(EditorModel editorModel, WorkspaceModel workspaceModel, WorkspaceView workspaceView) {
        this.editorModel = editorModel;
        this.model = workspaceModel;
        this.view = workspaceView;
        this.zoomHelper = new WorkspaceZoomHelper(model, view);

        //Initialize members
        connectionsOnWorkspace = FXCollections.observableSet();
        blocksOnWorkspace = FXCollections.observableSet();
        blocksSelectedOnWorkspace = FXCollections.observableSet();
        groupsOfBlocks = FXCollections.observableSet();

        view.sceneProperty().addListener(initializationHandler);

    }

    private PreConnection preConnection = null;

    // rename to initiateConnection and when PreConnection != null, then turn PreConnection into a real connection
    public void createConnection(Port port) {
        if (preConnection == null) {
            preConnection = new PreConnection(WorkspaceController.this, port);
            view.getChildren().add(0, preConnection);
        }
    }

    // method is unneeded if createConnection catches the second click
    public void removeChild(PreConnection preConnection) {
        view.getChildren().remove(preConnection);
        this.preConnection = null;
    }

    //Initial modi members
    public MousePositionHandler mouse;

    private final ChangeListener<Object> initializationHandler = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends Object> observableValue, Object oldObject, Object newObject) {

            mouse = new MousePositionHandler(view);

        }
    };

    public WorkspaceModel getModel() {
        return model;
    }

    public void reset() {
        model.resetZoomFactor();
        model.translateXProperty().set(0.);
        model.translateYProperty().set(0.);
        blocksOnWorkspace.clear();
        connectionsOnWorkspace.clear();
        view.getChildren().clear();
    }

    public void setEditorMode(EditorMode mode) {
        editorModel.modeProperty().set(mode);
    }

    public double getZoomFactor() {
        return model.zoomFactorProperty().get();
    }

    public void zoomIn() {
        zoomHelper.zoomIn();
    }

    public void zoomOut() {
        zoomHelper.zoomOut();
    }

    public void applyZoom(double newScale, Point2D pivotPoint) {
        zoomHelper.applyZoom(newScale, pivotPoint);
    }

    public void zoomToFit() {
        Collection<Block> blocks = !blocksSelectedOnWorkspace.isEmpty() ? blocksSelectedOnWorkspace : blocksOnWorkspace;
        zoomHelper.zoomToFit(blocks);
    }

    public void deselectAllBlocks() {
        for (Block block : this.blocksSelectedOnWorkspace) {
            block.setSelected(false);
        }
        this.blocksSelectedOnWorkspace.clear();
    }

    public void rectangleSelect(Point2D selectionMin, Point2D selectionMax) {
        for (Block block : this.blocksOnWorkspace) {
            if (true // unnecessary statement for readability
                    && block.getLayoutX() >= selectionMin.getX()
                    && block.getLayoutX() + block.getWidth() <= selectionMax.getX()
                    && block.getLayoutY() >= selectionMin.getY()
                    && block.getLayoutY() + block.getHeight() <= selectionMax.getY()) {

                this.blocksSelectedOnWorkspace.add(block);
                block.setSelected(true);

            } else {
                this.blocksSelectedOnWorkspace.remove(block);
                block.setSelected(false);
            }
        }
    }

    public WorkspaceView getView() {
        return view;
    }

    public void addBlock(Block block) {
        blocksOnWorkspace.add(block);
        view.getChildren().add(block);
    }

    public void removeChild(Block block) {
        blocksOnWorkspace.remove(block);
        blocksSelectedOnWorkspace.remove(block);
        view.getChildren().remove(block);
    }

    public void addConnection(Connection connection) {
        connectionsOnWorkspace.add(connection);
//        view.getChildren().add(connection);
    }

}

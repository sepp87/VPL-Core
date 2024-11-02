package vplcore.workspace;

import vplcore.MousePositionHandler;
import vplcore.graph.model.Connection;
import vplcore.graph.model.Block;
import vplcore.graph.model.BlockGroup;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import vplcore.editor.EditorMode;
import vplcore.editor.EditorModel;
import vplcore.graph.model.BlockInfoPanel;
import vplcore.graph.model.Port;
import vplcore.graph.util.PreConnection;

/**
 *
 * @author JoostMeulenkamp
 */
public class Workspace extends AnchorPane {

    //isSaved
    public BlockInfoPanel activeBlockInfoPanel;

    public ObservableSet<Connection> connectionSet;
    public ObservableSet<Block> blockSet;
    public ObservableSet<Block> tempBlockSet;
    public ObservableSet<Block> selectedBlockSet;
    public ObservableSet<BlockGroup> blockGroupSet;

    //Create connection members
    public boolean typeSensitive = true;

    private final WorkspaceModel zoomModel;
    private final EditorModel editorModel;

    //Radial menu
    public Workspace(WorkspaceModel workspaceModel, EditorModel editorModel) {
        this.zoomModel = workspaceModel;
        this.editorModel = editorModel;

        this.scaleXProperty().bind(workspaceModel.zoomFactorProperty());
        this.scaleYProperty().bind(workspaceModel.zoomFactorProperty());
        this.translateXProperty().bind(workspaceModel.translateXProperty());
        this.translateYProperty().bind(workspaceModel.translateYProperty());

        //Must set to (0,0) due to funky resize, otherwise messes up zoom in and out
        setMinSize(0, 0);
        setMaxSize(0, 0);

        //Initialize members
        connectionSet = FXCollections.observableSet();
        blockSet = FXCollections.observableSet();
        selectedBlockSet = FXCollections.observableSet();
        blockGroupSet = FXCollections.observableSet();

        this.setStyle("-fx-background-color: green;");

        this.sceneProperty().addListener(initializationHandler);

    }

    private PreConnection preConnection = null;

    // rename to initiateConnection and when PreConnection != null, then turn PreConnection into a real connection
    public void createConnection(Port port) {
        if (preConnection == null) {
            preConnection = new PreConnection(Workspace.this, port);
            Workspace.this.getChildren().add(0, preConnection);
        } 
    }
    
    // method is unneeded if createConnection catches the second click
    public void removeChild(PreConnection preConnection) {
        getChildren().remove(preConnection);
        this.preConnection = null;
    }

    public void removeChild(Block block) {

    }

    //Initial modi members
    public MousePositionHandler mouse;

    private final ChangeListener<Object> initializationHandler = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends Object> observableValue, Object oldObject, Object newObject) {

            mouse = new MousePositionHandler(Workspace.this);

        }
    };

    public WorkspaceModel getZoomModel() {
        return zoomModel;
    }

    public void reset() {
        zoomModel.resetZoomFactor();
        zoomModel.translateXProperty().set(0.);
        zoomModel.translateYProperty().set(0.);
        blockSet.clear();
        connectionSet.clear();
        getChildren().clear();
    }

    public void setEditorMode(EditorMode mode) {
        editorModel.modeProperty().set(mode);
    }

    public double getZoomFactor() {
        return zoomModel.zoomFactorProperty().get();
    }

    public void zoomIn() {
        WorkspaceModel model = zoomModel;
        double newScale = model.getIncrementedZoomFactor();
        applyZoom(newScale); // Zoom is not from scrolling; no scroll event needed
    }

    public void zoomOut() {
        WorkspaceModel model = zoomModel;
        double newScale = model.getDecrementedZoomFactor();
        applyZoom(newScale); // Zoom is not from scrolling; no scroll event needed
    }

    private void applyZoom(double newScale) {
        applyZoom(newScale, null);
    }

    public void applyZoom(double newScale, Point2D pivotPoint) {
        WorkspaceModel model = zoomModel;
        Workspace view = this;

        double oldScale = model.zoomFactorProperty().get();
        double scaleChange = (newScale / oldScale) - 1;

        // Get the bounds of the workspace
        Bounds workspaceBounds = view.getBoundsInParent();

        double dx, dy;

        if (pivotPoint != null) {
            // Calculate the distance from the zoom point (mouse cursor/graph center) to the workspace origin
            dx = pivotPoint.getX() - workspaceBounds.getMinX();
            dy = pivotPoint.getY() - workspaceBounds.getMinY();
        } else {
            // Calculate the center of the scene (visible area)
            double sceneCenterX = view.getScene().getWidth() / 2;
            double sceneCenterY = view.getScene().getHeight() / 2;

            // Calculate the distance from the workspace's center to the scene's center
            dx = sceneCenterX - workspaceBounds.getMinX();
            dy = sceneCenterY - workspaceBounds.getMinY();
        }

        // Calculate the new translation needed to zoom to the center or to the mouse position
        double dX = scaleChange * dx;
        double dY = scaleChange * dy;

        double newTranslateX = model.translateXProperty().get() - dX;
        double newTranslateY = model.translateYProperty().get() - dY;

        model.translateXProperty().set(newTranslateX);
        model.translateYProperty().set(newTranslateY);
        model.zoomFactorProperty().set(newScale);
    }

    public void zoomToFit() {
        WorkspaceModel model = zoomModel;
        Workspace view = this;

        Scene scene = view.getScene();
        if (this.blockSet.isEmpty()) {
            return;
        }

        //Zoom to fit        
        Bounds boundingBox = view.localToParent(Block.getBoundingBoxOfBlocks(this.blockSet));
        double ratioX = boundingBox.getWidth() / scene.getWidth();
        double ratioY = boundingBox.getHeight() / scene.getHeight();
        double ratio = Math.max(ratioX, ratioY);
        // multiply, round and divide by 10 to reach zoom step of 0.1 and substract by 1 to zoom a bit more out so the blocks don't touch the border
        double scale = Math.ceil((model.zoomFactorProperty().get() / ratio) * 10 - 1) / 10;
        scale = scale < WorkspaceModel.MIN_ZOOM ? WorkspaceModel.MIN_ZOOM : scale;
        scale = scale > WorkspaceModel.MAX_ZOOM ? WorkspaceModel.MAX_ZOOM : scale;
        model.zoomFactorProperty().set(scale);

        //Pan to fit
        boundingBox = view.localToParent(Block.getBoundingBoxOfBlocks(this.blockSet));
        double dx = (boundingBox.getMinX() + boundingBox.getWidth() / 2) - scene.getWidth() / 2;
        double dy = (boundingBox.getMinY() + boundingBox.getHeight() / 2) - scene.getHeight() / 2;
        double newTranslateX = model.translateXProperty().get() - dx;
        double newTranslateY = model.translateYProperty().get() - dy;

        model.translateXProperty().set(newTranslateX);
        model.translateYProperty().set(newTranslateY);
    }

    public void deselectAllBlocks() {
        for (Block block : this.selectedBlockSet) {
            block.setSelected(false);
        }
        this.selectedBlockSet.clear();
    }

    public void rectangleSelect(Point2D selectionMin, Point2D selectionMax) {
        for (Block block : this.blockSet) {
            if (true // unnecessary statement for readability
                    && block.getLayoutX() >= selectionMin.getX()
                    && block.getLayoutX() + block.getWidth() <= selectionMax.getX()
                    && block.getLayoutY() >= selectionMin.getY()
                    && block.getLayoutY() + block.getHeight() <= selectionMax.getY()) {

                this.selectedBlockSet.add(block);
                block.setSelected(true);

            } else {
                this.selectedBlockSet.remove(block);
                block.setSelected(false);
            }
        }
    }

}

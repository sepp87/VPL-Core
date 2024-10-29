package vplcore.workspace;

import vplcore.graph.model.Connection;
import vplcore.graph.model.Block;
import vplcore.graph.model.BlockGroup;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Point2D;
import javafx.scene.layout.*;
import vplcore.editor.EditorMode;
import vplcore.editor.EditorModel;
import vplcore.editor.ZoomModel;
import vplcore.graph.model.BlockInfoPanel;
import vplcore.graph.util.ConnectionCreator;
import vplcore.graph.util.ConnectionRemover;

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

    //Selection rectangle members
    public Point2D startSelectionPoint;
    public Region selectionRectangle;

    private final ZoomModel zoomModel;
    private final EditorModel editorModel;

    //Radial menu
    public Workspace(ZoomModel zoomModel, EditorModel editorModel) {
        this.zoomModel = zoomModel;
        this.editorModel = editorModel;
        
        this.scaleXProperty().bind(zoomModel.zoomFactorProperty());
        this.scaleYProperty().bind(zoomModel.zoomFactorProperty());
        this.translateXProperty().bind(zoomModel.translateXProperty());
        this.translateYProperty().bind(zoomModel.translateYProperty());

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

        this.translateXProperty().addListener(this::translateXChanged);

    }

    private void translateXChanged(Object b, Object o, Object n) {
//        System.out.println(n + " Workspace");
    }

    //Initial modi members
    public MousePositionHandler mouse;
    public ConnectionCreator portConnector;
    public ConnectionRemover portDisconnector;

    private final ChangeListener<Object> initializationHandler = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends Object> observableValue, Object oldObject, Object newObject) {

            mouse = new MousePositionHandler(Workspace.this);
            portConnector = new ConnectionCreator(Workspace.this);
            portDisconnector = new ConnectionRemover(Workspace.this);
            Workspace.this.requestFocus(); // Request focus, zoom to fit with SPACEBAR only works when workspace received focus
        }
    };

    private final SimpleObjectProperty<MouseMode> mouseModeProperty = new SimpleObjectProperty<>(MouseMode.MOUSE_IDLE);

    public double getZoomFactor() {
        return zoomModel.zoomFactorProperty().get();
    }

    public void reset() {
        zoomModel.resetZoomFactor();
        zoomModel.translateXProperty().set(0.);
        zoomModel.translateYProperty().set(0.);
        blockSet.clear();
        connectionSet.clear();
        getChildren().clear();
        getChildren().add(portDisconnector.getRemoveButton());
    }


    public void setEditorMode(EditorMode mode) {
        editorModel.modeProperty().set(mode);
    }

}

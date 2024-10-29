package vplcore.workspace;

import vplcore.graph.model.Connection;
import vplcore.workspace.input.MousePositionHandler;
import vplcore.graph.model.Block;
import vplcore.graph.model.BlockGroup;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import vplcore.editor.ZoomModel;
import vplcore.editor.ZoomView;
import vplcore.graph.model.BlockInfoPanel;
import vplcore.graph.util.ConnectionCreator;
import vplcore.graph.util.ConnectionRemover;
import vplcore.workspace.input.MouseMode;
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
    
    //Radial menu
    public Workspace(ZoomModel zoomModel) {
        this.zoomModel = zoomModel;
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

    public MouseMode getMouseMode() {
        return mouseModeProperty.get();
    }

    public void setMouseMode(MouseMode mode) {
        mouseModeProperty.set(mode);
    }

    public ObjectProperty<MouseMode> mouseModeProperty() {
        return mouseModeProperty;
    }

    public boolean onZoomView(MouseEvent event) {
        Node node = event.getPickResult().getIntersectedNode();
        return checkParents(node, ZoomView.class);
    }

    public boolean onMenuBar(MouseEvent event) {
        Node node = event.getPickResult().getIntersectedNode();
        return checkParents(node, Control.class);
    }

    public boolean onBlock(MouseEvent event) {
        Node node = event.getPickResult().getIntersectedNode();
        return checkParents(node, Block.class);
    }

    public boolean onBlockInfoPanel(MouseEvent event) {
        Node node = event.getPickResult().getIntersectedNode();
        return checkParents(node, BlockInfoPanel.class);
    }

    /**
     * Check if the node of the same type or if it is embedded in the type
     *
     * @param node the node to check
     * @param type the type of node to check against
     * @return
     */
    public static <T> boolean checkParents(Node node, Class<T> type) {
        if (node == null) {
            return false;
        }
//        System.out.println(node.getClass().getSimpleName()  + " "+ type.getSimpleName());

        if (type.isAssignableFrom(node.getClass())) {
            return true;
        } else {
            Node parent = node.getParent();
            return checkParents(parent, type);
        }
    }
}

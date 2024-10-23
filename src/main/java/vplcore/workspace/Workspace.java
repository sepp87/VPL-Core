package vplcore.workspace;

import vplcore.graph.model.Connection;
import vplcore.workspace.input.MousePositionHandler;
import vplcore.workspace.input.KeyboardInputHandler;
import vplcore.graph.model.Block;
import vplcore.graph.model.BlockGroup;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
import vplcore.graph.util.PortConnector;
import vplcore.graph.util.PortDisconnector;
import vplcore.workspace.input.MouseMode;
import vplcore.workspace.input.SelectionHandler;

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

    //Zoom members
    DoubleProperty scale = new SimpleDoubleProperty(1.0);

//    private ZoomModel zoomModel;
    
    //Radial menu
    public Workspace(ZoomModel zoomModel) {
//        this.zoomModel = zoomModel;
//        this.scale.bind(zoomModel.zoomFactorProperty());
//        this.translateXProperty().bind(zoomModel.translateXProperty());
//        this.translateYProperty().bind(zoomModel.translateYProperty());

        //Must set due to funky resize, which messes up zooming (must be the same as the zoompane)
        setMinSize(0, 0);
        setMaxSize(0, 0);

        //Initialize members
        connectionSet = FXCollections.observableSet();
        blockSet = FXCollections.observableSet();
        selectedBlockSet = FXCollections.observableSet();
        blockGroupSet = FXCollections.observableSet();

        //Zooming functionality
        scaleXProperty().bind(scale);
        scaleYProperty().bind(scale);

        this.setStyle("-fx-background-color: green;");

        this.sceneProperty().addListener(initializationHandler);

        this.translateXProperty().addListener(this::translateXChanged);

    }

    private void translateXChanged(Object b, Object o, Object n) {
//        System.out.println(n + " Workspace");
    }

    //Initial modi members
    public KeyboardInputHandler keyboard;
    public MousePositionHandler mouse;

    private SelectionHandler selectionHandler;
    public PortConnector portConnector;
    public PortDisconnector portDisconnector;

    private final ChangeListener<Object> initializationHandler = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends Object> observableValue, Object oldObject, Object newObject) {

            mouse = new MousePositionHandler(Workspace.this);
            keyboard = new KeyboardInputHandler(Workspace.this);
            selectionHandler = new SelectionHandler(Workspace.this);
            portConnector = new PortConnector(Workspace.this);
            portDisconnector = new PortDisconnector(Workspace.this);
            mouseModeProperty().addListener(mouseModeListener);
            Workspace.this.requestFocus(); // Request focus, zoom to fit with SPACEBAR only works when workspace received focus
        }
    };

    private final SimpleObjectProperty<MouseMode> mouseModeProperty = new SimpleObjectProperty<>(MouseMode.MOUSE_IDLE);

    private ChangeListener<Object> mouseModeListener = (b, o, n) -> {
//        System.out.println(n);
    };

    public void reset() {
        setScale(1);
        setTranslateX(0);
        setTranslateY(0);
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

    public double getScale() {
        return scale.get();
    }

    public void setScale(double value) {
        scale.set(value);
    }

    public void setPivot(double x, double y) {
        setTranslateX(getTranslateX() - x);
        setTranslateY(getTranslateY() - y);
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

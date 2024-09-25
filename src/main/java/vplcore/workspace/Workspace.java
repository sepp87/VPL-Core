package vplcore.workspace;

import vplcore.graph.model.Connection;
import vplcore.workspace.input.MousePositionHandler;
import vplcore.workspace.input.KeyboardInputHandler;
import vplcore.workspace.input.DragContext;
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
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import vplcore.graph.util.PortConnector;
import vplcore.graph.util.PortDisconnector;
import vplcore.workspace.input.MouseMode;
import vplcore.workspace.input.PanHandler;
import vplcore.workspace.input.SelectionHandler;
import vplcore.workspace.input.ZoomManager;
import vplcore.workspace.radialmenu.RadialMenu;

/**
 *
 * @author JoostMeulenkamp
 */
public class Workspace extends AnchorPane {

    //isSaved


    public ObservableSet<Connection> connectionSet;
    public ObservableSet<Block> blockSet;
    public ObservableSet<Block> tempBlockSet;
    public ObservableSet<Block> selectedBlockSet;
    public ObservableSet<BlockGroup> blockGroupSet;

    //Actions
    public Actions actions;

    //Create connection members
    public boolean typeSensitive = true;

    //Selection rectangle members
    public Point2D startSelectionPoint;
    public Region selectionRectangle;


    //Pan members
    public DragContext panContext;

    //Zoom members
    DoubleProperty scale = new SimpleDoubleProperty(1.0);
    public Pane zoomPane;

    //Radial menu
    public Group Go() {
        //Actions
        actions = new Actions(this);

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
        zoomPane = new Pane();
        zoomPane.setPrefSize(0, 0);
        zoomPane.setStyle("-fx-background-color: red;");
        zoomPane.relocate(0, 0);

        this.setStyle("-fx-background-color: green;");
        getChildren().add(zoomPane);

        //Create radial menu
        RadialMenu radialMenu = new RadialMenuConfigurator(this).getRadialMenu();

        //Create content group, elements within this group get added to the scene and are not by the zooming of the workspace (this)
        Group contentGroup = new Group();
        contentGroup.getChildren().addAll(this, radialMenu);

        this.sceneProperty().addListener(initializationHandler);

        return contentGroup;
    }
    //Initial modi members
    public KeyboardInputHandler keyboard;
    public MousePositionHandler mouse;

    private SelectionHandler selectionHandler;
    private PanHandler panHandler;
    public ZoomManager zoomManager;
    public PortConnector portConnector;
    public PortDisconnector portDisconnector;
    

    private final ChangeListener<Object> initializationHandler = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends Object> observableValue, Object oldObject, Object newObject) {

            mouse = new MousePositionHandler(Workspace.this);
            keyboard = new KeyboardInputHandler(Workspace.this);
            selectionHandler = new SelectionHandler(Workspace.this);
            panHandler = new PanHandler(Workspace.this);
            portConnector = new PortConnector(Workspace.this);
            portDisconnector = new PortDisconnector(Workspace.this);
            mouseModeProperty().addListener(mouseModeListener);
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

    public boolean onZoomControls(MouseEvent event) {
        Node node = event.getPickResult().getIntersectedNode();
        return checkParents(node, ZoomManager.class);
    }

    public boolean onMenuBar(MouseEvent event) {
        Node node = event.getPickResult().getIntersectedNode();
        return checkParents(node, Control.class);
    }

    public boolean onBlock(MouseEvent event) {
        Node node = event.getPickResult().getIntersectedNode();
        return checkParents(node, Block.class);
    }

    /**
     * Check if the node of the same type or if it is embedded in the type
     *
     * @param node the node to check
     * @param type the type of node to check against
     * @return
     */
    public boolean checkParents(Node node, Class<?> type) {
        if (node == null) {
            return false;
        }
        if (type.isAssignableFrom(node.getClass())) {
            return true;
        } else {
            Node parent = node.getParent();
            return checkParents(parent, type);
        }
    }
}

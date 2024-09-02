package vplcore.workspace;

import vplcore.graph.model.Port;
import vplcore.graph.model.Connection;
import vplcore.graph.util.SelectBlock;
import vplcore.workspace.input.MousePositionHandler;
import vplcore.workspace.input.KeyboardInputHandler;
import vplcore.workspace.input.DragContext;
import vplcore.EventBlaster;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import vplcore.workspace.input.ConnectionHandler;
import vplcore.workspace.input.MouseMode;
import vplcore.workspace.input.PanHandler;
import vplcore.workspace.input.SelectBlockHandler;
import vplcore.workspace.input.SelectionHandler;
import vplcore.workspace.input.SplineMode;
import vplcore.workspace.input.ZoomHandler;
import vplcore.workspace.radialmenu.RadialMenu;

/**
 *
 * @author JoostMeulenkamp
 */
public class Workspace extends AnchorPane {

    //STATIC VARIABLES
    //isSaved
    //styleFile
    public static boolean isCSS = true;
    public static final double MAX_SCALE = 1.5d;
    public static final double MIN_SCALE = .25d;

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

    //EventBlaster for changes in view
    EventBlaster controlBlaster = new EventBlaster(this);

    //Pan members
    public DragContext panContext;

    //Zoom members
    DoubleProperty scale = new SimpleDoubleProperty(1.0);
    public Pane zoomPane;

    //Radial menu
    public Group Go() {
        //Actions
        actions = new Actions(this);

        //Must set due to funky resize, which messes up zooming (must be the same as the zoompane
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

        //Initialize eventblaster
        controlBlaster.set("scale", scale);
        controlBlaster.set("translateX", translateXProperty());
        controlBlaster.set("translateY", translateYProperty());

        //Create radial menu
        RadialMenu radialMenu = new RadialMenuConfigurator(this).getRadialMenu();

        //Create content group, elements within this group get added to the scene and are not by the zooming of the workspace (this)
        Group contentGroup = new Group();
        contentGroup.getChildren().addAll(this, radialMenu);

        //Testing
//        deserialize(new File("src/main/resources/SampleParseObj.vplxml"));
//        Block add = new DoubleSlider(this);
//        add.relocate(100, 100);
//        getChildren().add(add);
//        blockSet.add(add);
//        System.out.println(add.getClass().isAnnotationPresent(BlockInfo.class));
//        Button button = new Button();
//        ObjectProperty<Color> value = new SimpleObjectProperty();
//        BlockStyle.bindBackgroundColor(value);
//        button.setBackground(new Background(new BackgroundFill(value.getValue(), CornerRadii.EMPTY, Insets.EMPTY)));
//        button.setOnMouseClicked(e -> {
//            value.setValue(Color.AQUA);
//        });
//        contentGroup.getChildren().addAll(button);
        //TODO improve listener removal because it doesn't work
        this.sceneProperty().addListener(initializationHandler);

        return contentGroup;
    }
    //Initial modi members
    public SplineMode splineMode = SplineMode.NOTHING;
    public KeyboardInputHandler keyboard;
    public MousePositionHandler mouse;

    private ZoomHandler zoomHandler;
    private SelectionHandler selectHandler;
    private PanHandler panHandler;
    public ConnectionHandler connectionHandler;
    private SelectBlockHandler selectBlockHandler;

    private final ChangeListener<Object> initializationHandler = new ChangeListener<>() {
        @Override
        public void changed(ObservableValue<? extends Object> observableValue, Object oldObject, Object newObject) {

            mouse = new MousePositionHandler(Workspace.this);
            keyboard = new KeyboardInputHandler(Workspace.this);

            zoomHandler = new ZoomHandler(Workspace.this);
            selectHandler = new SelectionHandler(Workspace.this);
            panHandler = new PanHandler(Workspace.this);
            connectionHandler = new ConnectionHandler(Workspace.this);
            selectBlockHandler = new SelectBlockHandler(Workspace.this);
        }
    };

    private final SimpleObjectProperty<MouseMode> mouseModeProperty = new SimpleObjectProperty<>(MouseMode.MOUSE_IDLE);

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


    public static double clamp(double value, double min, double max) {

        if (Double.compare(value, min) < 0) {
            return min;
        }

        if (Double.compare(value, max) > 0) {
            return max;
        }

        return value;
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

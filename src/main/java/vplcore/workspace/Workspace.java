package vplcore.workspace;

import vplcore.workspace.radialmenu.RadialMenuConfigurator;
import vplcore.graph.model.Port;
import vplcore.graph.model.Connection;
import vplcore.graph.util.SelectBlock;
import vplcore.workspace.input.MouseInputHandler;
import vplcore.workspace.input.KeyboardInputHandler;
import vplcore.workspace.input.DragContext;
import vplcore.EventBlaster;
import vplcore.graph.model.Block;
import vplcore.graph.model.BlockGroup;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import vplcore.workspace.input.MouseMode;
import vplcore.workspace.input.SplineMode;
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

    //Menu members
    public SelectBlock selectBlock;
    public RadialMenu radialMenu;

    //Actions
    public Actions actions;

    //Create connection members
    public Port tempStartPort;
    public Line tempLine;
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

    //Initial modi members
    public SplineMode splineMode = SplineMode.NOTHING;
    public MouseMode mouseMode = MouseMode.NOTHING;

    public KeyboardInputHandler keyboard;
    public MouseInputHandler mouse;

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

        //Create spiffy menu
        radialMenu = new RadialMenuConfigurator(this).configure();
        radialMenu.setVisible(false);

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
        keyboard = new KeyboardInputHandler(this);
        mouse = new MouseInputHandler(this);
        return contentGroup;
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

    public void clearTempLine() {
        this.getChildren().remove(tempLine);
        tempLine = null;
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

    /**
     * Check if the node of the same type or if it is embedded in the type
     *
     * @param node the node to check
     * @param type the type of node to check against
     * @return
     */
    public boolean checkParent(Node node, Class<?> type) {

        if (node == null) {
            return false;
        }
        if (type.isAssignableFrom(node.getClass())) {
            return true;
        } else {
            Node parent = node.getParent();
            return checkParent(parent, type);
        }
    }

    /**
     * Check if the node is embedded in this object
     *
     * @param node the node to check
     * @param checkNode the object to check against
     * @return
     */
    public boolean checkParent(Node node, Node checkNode) {

        if (node == null) {
            return false;
        }
        Node parent = node.getParent();
        if (parent != null && parent == checkNode) {
            return true;
        } else {
            return checkParent(parent, checkNode);
        }
    }
}

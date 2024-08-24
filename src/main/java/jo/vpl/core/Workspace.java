package jo.vpl.core;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.control.Control;
import javafx.scene.input.*;
import static javafx.scene.input.KeyCode.SPACE;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import jo.vpl.radialmenu.RadialMenu;

/**
 *
 * @author JoostMeulenkamp
 */
public class Workspace extends AnchorPane {

    //STATIC VARIABLES
    //isSaved
    //styleFile
    public static boolean isCSS = true;

    ObservableSet<Connection> connectionSet;
    public ObservableSet<Block> blockSet;
    ObservableSet<Block> tempBlockSet;
    ObservableSet<Block> selectedBlockSet;
    ObservableSet<BlockGroup> blockGroupSet;

    //Menu members
    private SelectBlock selectBlock;
    private RadialMenu radialMenu;

    //Create connection members
    Port tempStartPort;
    Line tempLine;
    boolean typeSensitive = true;

    //Selection rectangle members
    private Point2D startSelectionPoint;
    Region selectionRectangle;

    //EventBlaster for changes in view
    EventBlaster controlBlaster = new EventBlaster(this);

    //Pan members
    private DragContext panContext = new DragContext();

    //Zoom members
    DoubleProperty scale = new SimpleDoubleProperty(1.0);
    Pane zoomPane;
    private static final double MAX_SCALE = 5.0d;
    private static final double MIN_SCALE = .25d;

    //Initial modi members
    SplineMode splineMode = SplineMode.NOTHING;
    MouseMode mouseMode = MouseMode.NOTHING;

    //Radial menu
    public Group Go() {
        //Must set due to funky resize, which messes up zooming
        setMinSize(600, 600);
        setMaxSize(600, 600);

        //Initialize members
        connectionSet = FXCollections.observableSet();
        blockSet = FXCollections.observableSet();
        selectedBlockSet = FXCollections.observableSet();
        blockGroupSet = FXCollections.observableSet();

        //Zooming functionality
        scaleXProperty().bind(scale);
        scaleYProperty().bind(scale);
        zoomPane = new Pane();
        zoomPane.setPrefSize(600, 600);
        zoomPane.relocate(0, 0);
        getChildren().add(zoomPane);

        //Initialize eventblaster
        controlBlaster.set("scale", scale);
        controlBlaster.set("translateX", translateXProperty());
        controlBlaster.set("translateY", translateYProperty());

        //Create spiffy menu
        radialMenu = RadialMenu.get();
        radialMenu.setVisible(false);

        //Create content group
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
        layoutBoundsProperty().addListener(this::init);
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

    /**
     * Set key event handlers because a pane is not focusable and thus will not
     * register a key press. Call this method after creating the scene,
     * otherwise keyboard functionality such as copy-paste is not possible.
     */
    public void init(Object obj, Object oldVal, Object newVal) {
        layoutBoundsProperty().removeListener(this::init);

        //Event handlers
        getScene().setOnKeyPressed(this::handle_KeyPress);
        getScene().setOnKeyReleased(this::handle_KeyRelease);
        getScene().setOnMouseReleased(this::handle_MouseRelease);
        getScene().setOnMousePressed(this::handle_MousePress);
        getScene().setOnMouseDragged(this::handle_MouseDrag);
        getScene().setOnMouseMoved(this::handle_MouseMove);

        getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
        getScene().addEventFilter(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
        getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, onMouseClickedEventHandler);
        getScene().addEventFilter(ScrollEvent.SCROLL, onScrollEventHandler);
        getScene().addEventFilter(ScrollEvent.SCROLL_STARTED, onScrollEventHandler);
        getScene().addEventFilter(ScrollEvent.SCROLL_FINISHED, onScrollEventHandler);
    }

    private EventHandler<MouseEvent> onMouseClickedEventHandler = new EventHandler<MouseEvent>() {

        public void handle(MouseEvent e) {

            // Check if mouse was on Controls
            Node node = e.getPickResult().getIntersectedNode();
            boolean onControl = checkParent(node, Control.class);
            boolean onViewer = checkParent(node, Shape3D.class);
            boolean onRadialMenu = checkParent(node, RadialMenu.class);

            if (onViewer || onControl) {
                return;
            }

            if (radialMenu.isVisible() && !onRadialMenu && e.getButton() == MouseButton.PRIMARY) {
                radialMenu.hide();
                return;
            }

            // right mouse button => open menu
            if (e.getButton() != MouseButton.SECONDARY) {
                return;
            }

            if (!e.isStillSincePress()) {
                return;
            }

            radialMenu.show(e.getSceneX(), e.getSceneY());
        }
    };

    private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

        public void handle(MouseEvent e) {

            // right mouse button => panning
            if (!e.isSecondaryButtonDown()) {
                return;
            }
            panContext.setX(e.getSceneX());
            panContext.setY(e.getSceneY());
            panContext.setTranslateX(getTranslateX());
            panContext.setTranslateY(getTranslateY());
        }
    };

    private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent e) {
//            System.out.println("fire Go");
            // right mouse button => panning
            if (!e.isSecondaryButtonDown()) {
                return;
            }

            // Check if mouse was on Controls
            Node node = e.getPickResult().getIntersectedNode();
            boolean onControl = checkParent(node, Control.class);
            boolean onViewer = checkParent(node, SubScene.class);
            boolean onModel = checkParent(node, Shape3D.class);
            if (onViewer || onControl || onModel) {
                return;
            }

            setTranslateX(panContext.getTranslateX() + e.getSceneX() - panContext.getX());
            setTranslateY(panContext.getTranslateY() + e.getSceneY() - panContext.getY());
            e.consume();
        }
    };

    private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {
        boolean doScroll = false;

        @Override
        public void handle(ScrollEvent e) {

            EventType<ScrollEvent> type = e.getEventType();

            if (type == ScrollEvent.SCROLL) {
                doScroll = true;
            } else if (type == ScrollEvent.SCROLL_STARTED) {
                doScroll = true;
            } else if (type == ScrollEvent.SCROLL_FINISHED) {
                doScroll = false;
            }

            if (!doScroll) {
                return;
            }

            // Check if mouse was on Controls
            Node node = e.getPickResult().getIntersectedNode();
            boolean onControl = checkParent(node, Control.class);
            boolean onViewer = checkParent(node, SubScene.class);
            boolean onModel = checkParent(node, Shape3D.class);
            if (onViewer || onControl || onModel) {
                return;
            }

//            double delta = 1.2;
            double delta = 1.05;

            double scale = getScale(); // currently we only use Y, same value is used for X
            double oldScale = scale;

            if (e.getDeltaY() < 0) {
                scale /= delta;
            } else {
                scale *= delta;
            }

            scale = clamp(scale, MIN_SCALE, MAX_SCALE);

            double f = (scale / oldScale) - 1;

            Bounds hack = localToParent(zoomPane.getBoundsInParent());

            double dx = (e.getSceneX() - (hack.getWidth() / 2 + hack.getMinX()));
            double dy = (e.getSceneY() - (hack.getHeight() / 2 + hack.getMinY()));

            setScale(scale);

            // note: pivot value must be untransformed, i. e. without scaling
            setPivot(f * dx, f * dy);

            e.consume();
        }
    };

    private void handle_MousePress(MouseEvent e) {

//        System.out.println(e.getPickResult().getIntersectedNode().getClass());
        switch (mouseMode) {
            case NOTHING:
                if (e.isPrimaryButtonDown()) {

                    // Check if mouse click was on a block
                    Node node = e.getPickResult().getIntersectedNode();
                    boolean mouseUpOnBlock = checkParent(node, Block.class);

                    if (!mouseUpOnBlock) {

                        startSelectionPoint = sceneToLocal(e.getSceneX(), e.getSceneY());

                        mouseMode = MouseMode.SELECT;

                        splineMode = SplineMode.NOTHING;

                    }
                } else if (e.isSecondaryButtonDown()) {

                }

                break;
        }
    }

    Point2D mousePosition = new Point2D(0, 0);

    private void handle_MouseMove(MouseEvent e) {

        mousePosition = sceneToLocal(e.getSceneX(), e.getSceneY());

        switch (splineMode) {
            case NOTHING:
                clearTempLine();
                break;

            case FIRST:
                break;

            case SECOND:
                if (tempLine == null) {
                    tempLine = new Line();
                    tempLine.getStyleClass().add("temp-line");

                    getChildren().add(0, tempLine);
                }

                tempLine.startXProperty().bind(tempStartPort.centerXProperty);
                tempLine.startYProperty().bind(tempStartPort.centerYProperty);
                tempLine.setEndX(sceneToLocal(e.getSceneX(), e.getSceneY()).getX());
                tempLine.setEndY(sceneToLocal(e.getSceneX(), e.getSceneY()).getY());

                break;

            default:
                throw new IndexOutOfBoundsException("Argument out of range.");

        }
    }

    private void handle_MouseDrag(MouseEvent e) {

        // click and drag mouse button => selection
        if (mouseMode == MouseMode.SELECT) {

            if (e.isPrimaryButtonDown()) {
                if (selectionRectangle == null) {

                    selectionRectangle = new Region();
                    selectionRectangle.setLayoutX(startSelectionPoint.getX());
                    selectionRectangle.setLayoutY(startSelectionPoint.getY());
                    selectionRectangle.setMinSize(
                            sceneToLocal(e.getSceneX(), e.getSceneY()).getX(),
                            sceneToLocal(e.getSceneX(), e.getSceneY()).getY());

                    selectionRectangle.getStyleClass().add("selection-rectangle");

                    getChildren().add(selectionRectangle);
                }

                Point2D currentPosition = sceneToLocal(e.getSceneX(), e.getSceneY());
                Point2D delta = currentPosition.subtract(startSelectionPoint);

                if (delta.getX() < 0) {
                    selectionRectangle.setLayoutX(currentPosition.getX());
                }

                if (delta.getY() < 0) {
                    selectionRectangle.setLayoutY(currentPosition.getY());
                }

                selectionRectangle.setMinSize(Math.abs(delta.getX()), Math.abs(delta.getY()));

                for (Block block : blockSet) {
                    selectedBlockSet.remove(block);
                    block.setSelected(false);

                    if ((block.getLayoutX() >= selectionRectangle.getLayoutX())
                            && block.getLayoutX() + block.getWidth() <= selectionRectangle.getLayoutX() + selectionRectangle.getWidth()
                            && (block.getLayoutY() >= selectionRectangle.getLayoutY()
                            && block.getLayoutY() + block.getHeight() <= selectionRectangle.getLayoutY() + selectionRectangle.getHeight())) {
                        selectedBlockSet.add(block);
                        block.setSelected(true);
                    }
                }
            }
        }
    }

    private void handle_MouseRelease(MouseEvent e) {

//        System.out.println(mouseMode);
        //if mouse was not dragged, mouseMode was actually nothing instead of selection
        //if mouse was on a group, then selection should not be canceled
        if (e.isDragDetect()) {
            mouseMode = MouseMode.NOTHING;
        }

        // Check if mouse click was on a block
        Node node = e.getPickResult().getIntersectedNode();
        boolean mouseUpOnBlock = checkParent(node, Block.class);
//        boolean mouseUpOnMenu = checkParent(node, SpiffyMenu.class);
        boolean mouseUpOnMenu = checkParent(node, RadialMenu.class);
        /**
         * @TODO CHANGE FROM ORIGINAL CODE If there is already a select block,
         * then remove that one first
         * @TODO BUG when user clicks for the second time, but it happens to be
         * a drag. Then the select block stays open, although mouse is not on the
         * block itself.
         */
        if (e.getClickCount() == 2 && e.isDragDetect() && !mouseUpOnBlock) {
            if (selectBlock != null) {
                getChildren().remove(selectBlock);
            }
            selectBlock = new SelectBlock(this);
            selectBlock.setLayoutX(sceneToLocal(e.getX(), e.getY()).getX() - 20);
            selectBlock.setLayoutY(sceneToLocal(e.getX(), e.getY()).getY() - 20);
            getChildren().add(selectBlock);
        }

        switch (mouseMode) {

            case NOTHING:

                // if mouse up in empty space unselect all blocks
                if (!mouseUpOnBlock && !mouseUpOnMenu && e.getButton() != MouseButton.SECONDARY) {
                    for (Block block : selectedBlockSet) {
                        block.setSelected(false);
                    }
                    selectedBlockSet.clear();
                }
                break;

            case SELECT:
                //Get mouse mode out of selection rectangle so UI can deselect nodes
                getChildren().remove(selectionRectangle);
                selectionRectangle = null;
                mouseMode = MouseMode.NOTHING;
                break;

        }
    }

    void clearTempLine() {
        this.getChildren().remove(tempLine);
        tempLine = null;
    }

    public void handle_KeyRelease(KeyEvent e) {
        switch (e.getCode()) {
            case DELETE:

                for (Block block : selectedBlockSet) {
                    block.delete();
                }

                selectedBlockSet.clear();
                break;
            case C:
                if (e.isControlDown()) {
                    Actions.copyBlocks(Workspace.this);
                }
                break;

            case V:
                if (e.isControlDown()) {
                    if (tempBlockSet == null) {
                        return;
                    }
                    if (tempBlockSet.isEmpty()) {
                        return;
                    }
                    Actions.pasteBlocks(Workspace.this);
                }
                break;

            case G:
                if (e.isControlDown()) {
                    Actions.groupBlocks(Workspace.this);
                }
                break;

            case N:
                if (e.isControlDown()) {
                    Actions.newFile(Workspace.this);
                }
                break;

            case S:

                if (e.isControlDown()) {
                    Actions.saveFile(Workspace.this);
                }
                break;

            case O:
                if (e.isControlDown()) {
                    Actions.openFile(Workspace.this);
                }
                break;

            case A: {
                if (e.isControlDown()) {
                    selectedBlockSet.clear();

                    for (Block block : blockSet) {
                        block.setSelected(true);
                        selectedBlockSet.add(block);
                    }
                }
            }
            break;
        }
    }

    /**
     * Move all the with a press on the arrow keys. A form of panning.
     *
     * @param e
     */
    public void handle_KeyPress(KeyEvent e) {

        switch (e.getCode()) {
            case SPACE:
                Actions.zoomToFit(Workspace.this);
                break;
        }
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

enum MouseMode {

    NOTHING,
    PANNING,
    SELECT,
    GROUP_SELECT
}

enum SplineMode {

    NOTHING,
    FIRST,
    SECOND
}

enum AlignType {

    LEFT,
    RIGHT,
    TOP,
    BOTTOM,
    V_CENTER,
    H_CENTER
}

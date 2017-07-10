package jo.vpl.core;

import java.awt.MouseInfo;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Control;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.shape.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import jo.vpl.xml.*;

/**
 *
 * @author JoostMeulenkamp
 */
public class VplControl extends AnchorPane {

    //STATIC VARIABLES
    //isSaved
    //styleFile
    public static boolean isCSS = true;

    ObservableSet<Connection> connectionSet;
    public ObservableSet<Hub> hubSet;
    ObservableSet<Hub> tempHubSet;
    ObservableSet<Hub> selectedHubSet;
    ObservableSet<HubGroup> hubGroupSet;

    //Menu members
    private SpiffyMenu radialMenu;
    private SelectHub selectHub;

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
        hubSet = FXCollections.observableSet();
        selectedHubSet = FXCollections.observableSet();
        hubGroupSet = FXCollections.observableSet();

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
        radialMenu = new SpiffyMenu(this);
        radialMenu.setVisible(false);

        //Create content group
        Group contentGroup = new Group();
        contentGroup.getChildren().addAll(this, radialMenu);

        //Testing
//        deserialize(new File("src/main/resources/SampleParseObj.vplxml"));
//        Hub add = new DoubleSlider(this);
//        add.relocate(100, 100);
//        getChildren().add(add);
//        hubSet.add(add);
//        System.out.println(add.getClass().isAnnotationPresent(HubInfo.class));
//        Button button = new Button();
//        ObjectProperty<Color> value = new SimpleObjectProperty();
//        HubStyle.bindBackgroundColor(value);
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
            if (onViewer || onControl) {
                return;
            }

            // right mouse button => open menu
            if (e.getButton() != MouseButton.SECONDARY) {
                return;
            }

            if (radialMenu.isVisible()) {
                return;
            }

            if (!e.isStillSincePress()) {
                return;
            }

            radialMenu.toggleMenu(e.getSceneX(), e.getSceneY());
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

            double delta = 1.2;

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

    public void zoomToFit() {

//        Bounds bLayout = getParent().getParent().getLayoutBounds();
        Scene bScene = getScene();
        Bounds localBBox = Hub.getBoundingBoxOfHubs(hubSet);
        if (localBBox == null) {
            return;
        }

        //Zoom to fit        
        Bounds bBox = localToParent(localBBox);
        double ratioX = bBox.getWidth() / bScene.getWidth();
        double ratioY = bBox.getHeight() / bScene.getHeight();
        double ratio = Math.max(ratioX, ratioY);
        setScale((getScale() / ratio) - 0.03); //little extra zoom out, not to touch the borders

        //Pan to fit
        bBox = localToParent(Hub.getBoundingBoxOfHubs(hubSet));
        double deltaX = (bBox.getMinX() + bBox.getWidth() / 2) - bScene.getWidth() / 2;
        double deltaY = (bBox.getMinY() + bBox.getHeight() / 2) - bScene.getHeight() / 2;
        setTranslateX(getTranslateX() - deltaX);
        setTranslateY(getTranslateY() - deltaY);
    }

    public void align(AlignType type) {
        Bounds bBox = Hub.getBoundingBoxOfHubs(selectedHubSet);
        switch (type) {
            case LEFT:
                for (Hub hub : selectedHubSet) {
                    hub.setLayoutX(bBox.getMinX());
                }
                break;
            case RIGHT:
                for (Hub hub : selectedHubSet) {
                    hub.setLayoutX(bBox.getMaxX() - hub.getWidth());
                }
                break;
            case TOP:
                for (Hub hub : selectedHubSet) {
                    hub.setLayoutY(bBox.getMinY());
                }
                break;
            case BOTTOM:
                for (Hub hub : selectedHubSet) {
                    hub.setLayoutY(bBox.getMaxY() - hub.getHeight());
                }
                break;
            case V_CENTER:
                for (Hub hub : selectedHubSet) {
                    hub.setLayoutX(bBox.getMaxX() - bBox.getWidth() / 2 - hub.getWidth());
                }
                break;
            case H_CENTER:
                for (Hub hub : selectedHubSet) {
                    hub.setLayoutY(bBox.getMaxY() - bBox.getHeight() / 2 - hub.getHeight());
                }
                break;
        }
    }

    private void handle_MousePress(MouseEvent e) {

//        System.out.println(e.getPickResult().getIntersectedNode().getClass());
        switch (mouseMode) {
            case NOTHING:
                if (e.isPrimaryButtonDown()) {

                    // Check if mouse click was on a hub
                    Node node = e.getPickResult().getIntersectedNode();
                    boolean mouseUpOnHub = checkParent(node, Hub.class);

                    if (!mouseUpOnHub) {

                        startSelectionPoint = sceneToLocal(e.getSceneX(), e.getSceneY());

                        mouseMode = MouseMode.SELECT;

                        splineMode = SplineMode.NOTHING;

//                          if (radialMenu != null){
//                              radialMenu.IsOpen = false;
//                              radialMenu.delete();
//                              radialMenu = null;
//                          }
                    }
                } else if (e.isSecondaryButtonDown()) {
//            if (radialMenu == null) {
//                radialMenu = new RadialContentMenu(this);
//                getChildren().add(radialMenu);
//            }
//
//            if (radialMenu.isOpen) {
//                radialMenu.isOpen = false;
//                await Task
//                .Delay(400);
//            }
//
//            radialMenu.SetValue(LeftProperty, Mouse.GetPosition(this).x - 150);
//            radialMenu.SetValue(TopProperty, Mouse.GetPosition(this).Y - 150);
//
//            radialMenu.isOpen = true;
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

                tempLine.startXProperty().bind(tempStartPort.origin.x());
                tempLine.startYProperty().bind(tempStartPort.origin.y());
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

                for (Hub hub : hubSet) {
                    selectedHubSet.remove(hub);
                    hub.setSelected(false);

                    if ((hub.getLayoutX() >= selectionRectangle.getLayoutX())
                            && hub.getLayoutX() + hub.getWidth() <= selectionRectangle.getLayoutX() + selectionRectangle.getWidth()
                            && (hub.getLayoutY() >= selectionRectangle.getLayoutY()
                            && hub.getLayoutY() + hub.getHeight() <= selectionRectangle.getLayoutY() + selectionRectangle.getHeight())) {
                        selectedHubSet.add(hub);
                        hub.setSelected(true);
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

        // Check if mouse click was on a hub
        Node node = e.getPickResult().getIntersectedNode();
        boolean mouseUpOnHub = checkParent(node, Hub.class);
        boolean mouseUpOnMenu = checkParent(node, SpiffyMenu.class);
        /**
         * @TODO CHANGE FROM ORIGINAL CODE If there is already a select hub,
         * then remove that one first
         * @TODO BUG when user clicks for the second time, but it happens to be
         * a drag. Then the select hub stays open, although mouse is not on the
         * hub itself.
         */
        if (e.getClickCount() == 2 && e.isDragDetect() && !mouseUpOnHub) {
            if (selectHub != null) {
                getChildren().remove(selectHub);
            }
            selectHub = new SelectHub(this);
            selectHub.setLayoutX(sceneToLocal(e.getX(), e.getY()).getX() - 20);
            selectHub.setLayoutY(sceneToLocal(e.getX(), e.getY()).getY() - 20);
            getChildren().add(selectHub);
        }

        switch (mouseMode) {

            case NOTHING:

                // if mouse up in empty space unselect all hubs
                if (!mouseUpOnHub && !mouseUpOnMenu && e.getButton() != MouseButton.SECONDARY) {
                    for (Hub hub : selectedHubSet) {
                        hub.setSelected(false);
                    }
                    selectedHubSet.clear();
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

                for (Hub hub : selectedHubSet) {
                    hub.delete();
                }

                selectedHubSet.clear();
                break;
            case C:
                if (e.isControlDown()) {
                    copyHubs();
                }
                break;

            case V:
                if (e.isControlDown()) {
                    if (tempHubSet == null) {
                        return;
                    }
                    if (tempHubSet.isEmpty()) {
                        return;
                    }
                    pasteHubs();
                }
                break;

            case G:
                if (e.isControlDown()) {
                    groupHubs();
                }
                break;

            case N:
                if (e.isControlDown()) {
                    newFile();
                }
                break;

            case S:

                if (e.isControlDown()) {
                    saveFile();
                }
                break;

            case O:
                if (e.isControlDown()) {
                    openFile();
                }
                break;

            case A: {
                if (e.isControlDown()) {
                    selectedHubSet.clear();

                    for (Hub hub : hubSet) {
                        hub.setSelected(true);
                        selectedHubSet.add(hub);
                    }
                }
            }
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
     * Move all the with a press on the arrow keys. A form of panning.
     *
     * @param e
     */
    public void handle_KeyPress(KeyEvent e) {

        switch (e.getCode()) {
            case SPACE:
                zoomToFit();
                break;
        }
    }

    public void newFile() {
        hubSet.clear();
        connectionSet.clear();
        getChildren().clear();
//            if (radialMenu != null) radialMenu.Dispose();
//            radialMenu = null;
    }

    public void openFile() {
        //Clear Layout
        hubSet.clear();
        connectionSet.clear();
        getChildren().clear();

        //Open File
        Stage stage = (Stage) getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open a vplXML...");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("vplXML", "*.vplxml"));
        File file = chooser.showOpenDialog(stage);

        if (file != null) {
            deserialize(file);
        }
    }

    public void saveFile() {
        Stage stage = (Stage) getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Save as vplXML...");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("vplXML", "*.vplxml"));
        File file = chooser.showSaveDialog(stage);

        if (file != null) {
            serialize(file);
        }
    }

    public void groupHubs() {
        if (selectedHubSet.size() <= 1) {
            return;
        }

        HubGroup hubGroup = new HubGroup(this);
        hubGroup.setChildHubs(selectedHubSet);
    }

    public void copyHubs() {
        tempHubSet = FXCollections.observableSet();

        for (Hub hub : selectedHubSet) {
            tempHubSet.add(hub);
        }
    }

    public void pasteHubs() {
        Bounds bBox = Hub.getBoundingBoxOfHubs(tempHubSet);

        if (bBox == null) {
            return;
        }

        Point2D copyPoint = new Point2D(bBox.getMinX() + bBox.getWidth() / 2, bBox.getMinY() + bBox.getHeight() / 2);
        double pastePointX = MouseInfo.getPointerInfo().getLocation().x;
        double pastePointY = MouseInfo.getPointerInfo().getLocation().y;
        Point2D pastePoint = screenToLocal(pastePointX, pastePointY);

        pastePoint = mousePosition;
        
        Point2D delta = pastePoint.subtract(copyPoint);

        //First deselect selected hubs. Simply said, deselect copied hubs.
        for (Hub hub : selectedHubSet) {
            hub.setSelected(false);
        }
        selectedHubSet.clear();

        List<Connection> alreadyClonedConnectors = new ArrayList<>();
        List<CopyConnection> copyConnections = new ArrayList<>();

        // copy hub from clipboard to canvas
        for (Hub hub : tempHubSet) {
            Hub newHub = hub.clone();

            newHub.setLayoutX(hub.getLayoutX() + delta.getX());
            newHub.setLayoutY(hub.getLayoutY() + delta.getY());

            getChildren().add(newHub);
            hubSet.add(newHub);

            //Set pasted hub(s) as selected
            selectedHubSet.add(newHub);
            newHub.setSelected(true);

            copyConnections.add(new CopyConnection(hub, newHub));
        }

        for (CopyConnection cc : copyConnections) {
            int counter = 0;

            for (Port port : cc.oldHub.inPorts) {
                for (Connection connection : port.connectedConnections) {
                    if (!alreadyClonedConnectors.contains(connection)) {
                        Connection newConnection = null;

                        // start and end hub are contained in selection
                        if (tempHubSet.contains(connection.startPort.parentHub)) {
                            CopyConnection cc2 = copyConnections
                                    .stream()
                                    .filter(i -> i.oldHub == connection.startPort.parentHub)
                                    .findFirst()
                                    .orElse(null);

                            if (cc2 != null) {
                                newConnection = new Connection(this, cc2.newHub.outPorts.get(0), cc.newHub.inPorts.get(counter));
                            }
                        } else {
                            // only end hub is contained in selection
                            newConnection = new Connection(this, connection.startPort, cc.newHub.inPorts.get(counter));
                        }

                        if (newConnection != null) {
                            alreadyClonedConnectors.add(connection);
                            connectionSet.add(newConnection);
                        }
                    }
                }
                counter++;
            }
        }
    }

    /**
     * Check if the node of the same type or if it is embedded in the type
     *
     * @param node the node to check
     * @param type the type of node to check against
     * @return
     */
    public boolean checkParent(Node node, Class type) {

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

    public void serialize(File file) {
        try {

            ObjectFactory factory = new ObjectFactory();

            HubsTag hubsTag = factory.createHubsTag();

            for (Hub hub : hubSet) {
                HubTag hubTag = factory.createHubTag();
                hub.serialize(hubTag);
                hubsTag.getHub().add(hubTag);
            }

            ConnectionsTag connectionsTag = factory.createConnectionsTag();

            for (Connection connection : connectionSet) {
                ConnectionTag connectionTag = factory.createConnectionTag();
                connection.serialize(connectionTag);
                connectionsTag.getConnection().add(connectionTag);
            }

            DocumentTag documentTag = factory.createDocumentTag();
            documentTag.setScale(getScale());
            documentTag.setTranslateX(getTranslateX());
            documentTag.setTranslateY(getTranslateY());

            documentTag.setHubs(hubsTag);
            documentTag.setConnections(connectionsTag);

            JAXBElement<DocumentTag> document = factory.createDocument(documentTag);

            JAXBContext context = JAXBContext.newInstance("jo.vpl.xml");
            Marshaller marshaller = context.createMarshaller();

            //Pretty output
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(document, file);

        } catch (JAXBException ex) {
            Logger.getLogger(VplControl.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void deserialize(File file) {
        
        String errorMessage = "";
        
        try {
            JAXBContext context = JAXBContext.newInstance(ObjectFactory.class
            );
            Unmarshaller unmarshaller = context.createUnmarshaller();

//            JAXBElement<DocumentTag> document
//                    = (JAXBElement<DocumentTag>) unmarshaller.
//                    unmarshal(ClassLoader.getSystemResourceAsStream("problem/expense.xml"));
            JAXBElement<DocumentTag> document = (JAXBElement<DocumentTag>) unmarshaller.unmarshal(file);
            DocumentTag documentTag = document.getValue();

            setScale(documentTag.getScale());
            setTranslateX(documentTag.getTranslateX());
            setTranslateY(documentTag.getTranslateY());

            HubsTag hubsTag = documentTag.getHubs();
            List<HubTag> hubTagList = hubsTag.getHub();
            if (hubTagList
                    != null) {
                for (HubTag hubTag : hubTagList) {
                    errorMessage = "Hub type " + hubTag.getType() + " not found.";
                    Class type = VplGlobal.HUB_TYPE_MAP.get(hubTag.getType());
//                    Class type = Class.forName(hubTag.getType());
                    Hub hub = (Hub) type.getConstructor(VplControl.class).newInstance(this);
                    hub.deserialize(hubTag);
                    hubSet.add(hub);
                    getChildren().add(hub);
                }
            }

            ConnectionsTag connectionsTag = documentTag.getConnections();
            List<ConnectionTag> connectionTagList = connectionsTag.getConnection();
            if (connectionTagList
                    != null) {
                for (ConnectionTag connectionTag : connectionTagList) {

                    UUID startHubUUID = UUID.fromString(connectionTag.getStartHub());
                    int startPortIndex = connectionTag.getStartIndex();
                    UUID endHubUUID = UUID.fromString(connectionTag.getEndHub());
                    int endPortIndex = connectionTag.getEndIndex();

                    Hub startHub = null;
                    Hub endHub = null;
                    for (Hub hub : hubSet) {
                        if (hub.uuid.compareTo(startHubUUID) == 0) {
                            startHub = hub;
                        } else if (hub.uuid.compareTo(endHubUUID) == 0) {
                            endHub = hub;
                        }
                    }

                    if (startHub != null && endHub != null) {
                        Port startPort = startHub.outPorts.get(startPortIndex);
                        Port endPort = endHub.inPorts.get(endPortIndex);
                        Connection connection = new Connection(this, startPort, endPort);
                        connectionSet.add(connection);
                    }
                }
            }
        } catch (JAXBException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(VplControl.class
                    .getName()).log(Level.SEVERE, errorMessage, ex);
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

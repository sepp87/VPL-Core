package vplcore.graph.model;

import java.util.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.input.*;
import vplcore.workspace.Workspace;
import jo.vpl.xml.BlockTag;
import vplcore.IconType;
import vplcore.workspace.Workspace;

/**
 *
 * @author JoostMeulenkamp
 */
public abstract class Block extends VplElement {

    public UUID uuid;
    public Pane inPortBox;
    public Pane outPortBox;
    public List<Port> inPorts;
    public List<Port> outPorts;
    public List<Region> controls;
    public GridPane contentGrid;
    public GridPane mainContentGrid;
    private Boolean resizable = false;

    public Point2D oldMousePosition;

    public Block(Workspace workspace) {
        super(workspace);
        uuid = UUID.randomUUID();

        inPorts = new ArrayList<>();
        outPorts = new ArrayList<>();
        controls = new ArrayList<>();

        //Content Grid is the actual block box without the buttons on top etc.
        contentGrid = new GridPane();
        contentGrid.setAlignment(Pos.CENTER);
        contentGrid.addEventFilter(MouseEvent.MOUSE_ENTERED, onMouseEnterEventHandler);
        contentGrid.addEventFilter(MouseEvent.MOUSE_EXITED, onMouseExitEventHandler);
        contentGrid.setOnMousePressed(this::handle_MousePress);
        selected.addListener(selectChangeListener);

        if (true) {
            VBox in = new VBox();
            VBox out = new VBox();

            in.setAlignment(Pos.CENTER);
            out.setAlignment(Pos.CENTER);

            inPortBox = in;
            outPortBox = out;

            contentGrid.add(inPortBox, 0, 1);
            contentGrid.add(outPortBox, 2, 1);

            ColumnConstraints column1 = new ColumnConstraints();
            ColumnConstraints column2 = new ColumnConstraints();
            ColumnConstraints column3 = new ColumnConstraints();

            column1.setHgrow(Priority.NEVER);
            column2.setHgrow(Priority.ALWAYS);
            column3.setHgrow(Priority.NEVER);
            column3.setHalignment(HPos.RIGHT);

            contentGrid.getColumnConstraints().addAll(column1, column2, column3);
        } else {
            HBox in = new HBox();
            HBox out = new HBox();

            in.setAlignment(Pos.CENTER);
            out.setAlignment(Pos.CENTER);

            inPortBox = in;
            outPortBox = out;

            contentGrid.add(inPortBox, 1, 0);
            contentGrid.add(outPortBox, 1, 3);
        }

        contentGrid.getStyleClass().add("block");
        inPortBox.getStyleClass().add("in-port-box");
        outPortBox.getStyleClass().add("out-port-box");

        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();
        RowConstraints row3 = new RowConstraints();
        RowConstraints row4 = new RowConstraints();
        row1.setVgrow(Priority.NEVER);
        row2.setVgrow(Priority.ALWAYS);
        row3.setVgrow(Priority.NEVER);
        row4.setVgrow(Priority.NEVER);

        contentGrid.getRowConstraints().addAll(row1, row2, row3, row4);

        //Main content grid is -> the center for controls
        mainContentGrid = new GridPane();
        contentGrid.add(mainContentGrid, 1, 1);

        //Main content grid constraints to make content grow
        ColumnConstraints column = new ColumnConstraints();
        column.setHgrow(Priority.ALWAYS);
        column.setHalignment(HPos.CENTER);
        mainContentGrid.getColumnConstraints().addAll(column);

        super.add(contentGrid, 1, 1);
    }

    public void setResizable(boolean resizable) {
        if (resizable) {
            resizeButton = new BlockButton(IconType.FA_PLUS_SQUARE_O);
            contentGrid.add(resizeButton, 2, 3);

            resizeButton.setOnMousePressed(this::resizeButton_MousePress);
            resizeButton.setOnMouseDragged(this::resizeButton_MouseDrag);
        }
    }

    private void resizeButton_MousePress(MouseEvent e) {
        oldMousePosition = new Point2D(e.getSceneX(), e.getSceneY());
    }

    private void resizeButton_MouseDrag(MouseEvent e) {
        double scale = workspace.getScale();

        double deltaX = (e.getSceneX() - oldMousePosition.getX()) / scale;
        double deltaY = (e.getSceneY() - oldMousePosition.getY()) / scale;

        contentGrid.setMinWidth(contentGrid.getWidth() + deltaX);
        contentGrid.setMinHeight(contentGrid.getHeight() + deltaY);

        oldMousePosition = new Point2D(e.getSceneX(), e.getSceneY());
    }

    /**
     * Event handler for selection of blocks and possible followed up dragging of
     * them by the user.
     *
     * @param e
     */
    private void handle_MousePress(MouseEvent e) {

        if (workspace.selectedBlockSet.contains(this)) {
            if (e.isControlDown()) {
                // Remove this node from selection
                workspace.selectedBlockSet.remove(this);
                setSelected(false);
            } else {
                // Subscribe multiselection to MouseMove event
                for (Block block : workspace.selectedBlockSet) {
                    block.setOnMouseDragged(block::handle_MouseDrag);

                    block.oldMousePosition = new Point2D(e.getSceneX(), e.getSceneY());
                }
            }
        } else {
            if (e.isControlDown()) {
                // add this node to selection
                workspace.selectedBlockSet.add(this);

                setSelected(true);
            } else {
                // Deselect all blocks that are selected
                for (Block block : workspace.selectedBlockSet) {
                    block.setSelected(false);
                }

                workspace.selectedBlockSet.clear();
                workspace.selectedBlockSet.add(this);
                // Select this block as selected
                setSelected(true);
                for (Block block : workspace.selectedBlockSet) {
                    //Add mouse dragged event handler so the block will move
                    //when the user starts dragging it
                    this.setOnMouseDragged(block::handle_MouseDrag);

                    //Get mouse position so there is a value to calculate 
                    //in the mouse dragged event
                    block.oldMousePosition = new Point2D(e.getSceneX(), e.getSceneY());
                }
            }
        }
        e.consume();
    }

    public void handle_MouseDrag(MouseEvent e) {

        double scale = workspace.getScale();

        double deltaX = (e.getSceneX() - oldMousePosition.getX()) / scale;
        double deltaY = (e.getSceneY() - oldMousePosition.getY()) / scale;

        for (Block block : workspace.selectedBlockSet) {

            block.setLayoutX(block.getLayoutX() + deltaX);
            block.setLayoutY(block.getLayoutY() + deltaY);
        }

        oldMousePosition = new Point2D(e.getSceneX(), e.getSceneY());
    }

    /**
     * Add a port to the block, multiple connections are not allowed by default
     *
     * @param name the string that shows up as comment
     * @param type the dataProperty type it will be handling
     * @return the Port
     */
    public Port addInPortToBlock(String name, Class type) {
        return Block.this.addInPortToBlock(name, type, false);
    }

    /**
     * Add a port to the block
     *
     * @param name the string that shows up as comment
     * @param type the dataProperty type it will be handling
     * @param multiDockAllowed if multiple connections are allowed
     * @return the Port
     */
    public Port addInPortToBlock(String name, Class type, boolean multiDockAllowed) {
        Port port = new Port(name, this, PortTypes.IN, type);
        port.multiDockAllowed = multiDockAllowed;
        inPortBox.getChildren().add(port);
        port.dataProperty().addListener(port_DataChangeListener);
        inPorts.add(port);
        return port;
    }

    /**
     * Add a port to the block
     *
     * @param port the port to add
     * @return the Port
     */
    public Port addInPortToBlock(Port port) {
        inPortBox.getChildren().add(port.index, port);
        port.dataProperty().addListener(port_DataChangeListener);
        inPorts.add(port.index, port);
        return port;
    }

    /**
     * Remove a port from the block
     *
     * @param port the port to remove
     */
    public void removeInPortFromBlock(Port port) {
        for (Connection connector : port.connectedConnections) {
            connector.removeFromCanvas();
        }
        inPortBox.getChildren().remove(port);
        port.dataProperty().removeListener(port_DataChangeListener);
        inPorts.remove(port);
    }

    //Double point operators do NOT work when trying to remove listeners
    //USE THIS OTHERWISE THERE WILL BE MEMORY LEAKING
    ChangeListener port_DataChangeListener = new ChangeListener() {

        @Override
        public void changed(ObservableValue obj, Object oldVal, Object newVal) {
            //        try {
//            if (AutoCheckBox.IsChecked != null && (bool) AutoCheckBox.IsChecked) {

            calculate();
            //            }
//            HasError = false;
//            TopComment.Visibility = Visibility.Hidden;
//        } catch (Exception ex) {
//            HasError = true;
//            TopComment.Text = ex.ToString();
//            TopComment.Visibility = Visibility.Visible;
//        }
        }
    };

    /**
     * Add a port to the block, multiple outgoing connections are allowed
     *
     * @param name the string that shows up as comment
     * @param type the dataProperty type it will be handling
     * @return the Port
     */
    public Port addOutPortToBlock(String name, Class type) {
        Port port = new Port(name, this, PortTypes.OUT, type);
        port.multiDockAllowed = true;
        outPortBox.getChildren().add(port);
        outPorts.add(port);
        return port;
    }

    /**
     * Add control to the block. A control extends region so it can be a layout,
     * but also a simple control like a button.
     *
     * @param control the control to add
     */
    public void addControlToBlock(Region control) {
        mainContentGrid.add(control, 0, mainContentGrid.getChildren().size());
        RowConstraints row = new RowConstraints();
        row.setVgrow(Priority.ALWAYS);
        mainContentGrid.getRowConstraints().add(row);
        controls.add(control);
    }

    @Override
    public void binButton_MouseClick(MouseEvent e) {
        delete();
    }

    /**
     * Remove this block from the host canvas
     */
    public void delete() {
        workspace.blockSet.remove(this);
        super.delete();
    }

    /**
     * Called when a new connection is incoming. Ideal for forwarding a data
     * type to an out port e.g. blocks operating on collections. Its removed
     * counterpart is used to set the data type of the out port back to its
     * initial state. Only called when multi dock is not allowed!
     *
     * @param source port the connection was added to
     * @param incoming port which sends the data
     */
    protected void handle_IncomingConnectionAdded(Port source, Port incoming) {

    }

    /**
     * Called when an incoming connection is removed. Ideal for forwarding a
     * data type to an out port e.g. blocks operating on collections. Its removed
     * counterpart is used to set the data type of the out port back to its
     * initial state. Only called when multi dock is not allowed!
     *
     * @param source port the connection was removed from
     */
    protected void handle_IncomingConnectionRemoved(Port source) {

    }

    public abstract void calculate();

    public abstract Block clone();

    public void serialize(BlockTag xmlTag) {
        xmlTag.setType(this.getClass().getAnnotation(BlockInfo.class).identifier());
        xmlTag.setUUID(uuid.toString());
        xmlTag.setX(getLayoutX());
        xmlTag.setY(getLayoutY());
    }

    public void deserialize(BlockTag xmlTag) {
        uuid = UUID.fromString(xmlTag.getUUID());
        setLayoutX(xmlTag.getX());
        setLayoutY(xmlTag.getY());
        List<Block> blocks = new ArrayList<>();
    }

    public <type extends Block> void testList(type... blocks) {

    }

    public static Bounds getBoundingBoxOfBlocks(Collection<? extends Block> blocks) {
        if (blocks == null || blocks.isEmpty()) {
            return null;
        }
        double minLeft = Double.MAX_VALUE;
        double minTop = Double.MAX_VALUE;
        double maxLeft = Double.MIN_VALUE;
        double maxTop = Double.MIN_VALUE;

        for (Block block : blocks) {
            if (block.getLayoutX() < minLeft) {
                minLeft = block.getLayoutX();
            }
            if (block.getLayoutY() < minTop) {
                minTop = block.getLayoutY();
            }

            if ((block.getLayoutX() + block.getWidth()) > maxLeft) {
                maxLeft = block.getLayoutX() + block.getWidth();
            }
            if ((block.getLayoutY() + block.getHeight()) > maxTop) {
                maxTop = block.getLayoutY() + block.getHeight();
            }
        }

        return new BoundingBox(minLeft, minTop, maxLeft - minLeft, maxTop - minTop);
    }

    private final EventHandler<MouseEvent> onMouseEnterEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent e) {
            Block.this.setActive(true);
            Block.this.updateStyle();
        }
    };
    private final EventHandler<MouseEvent> onMouseExitEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent e) {
            //Change focus on exit to host canvas so controls do not interrupt key events
            Block.this.workspace.requestFocus();
            Block.this.setActive(false);
            Block.this.updateStyle();
        }
    };
    private final EventHandler<MouseEvent> onMousePressEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent e) {

        }
    };

    private final ChangeListener<Boolean> selectChangeListener = new ChangeListener<Boolean>() {

        @Override
        public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
            updateStyle();
        }
    };

    public void updateStyle() {
//        System.out.println(this.isPressed());
//        System.out.println(this.isSelected());
        if (isSelected()) {
//          contentGrid.getStyleClass().clear();
            contentGrid.getStyleClass().add("block-selected");
        } else {
            contentGrid.getStyleClass().clear();
            contentGrid.getStyleClass().add("block");
        }
    }

    /**
     * Pick yourself a wonderfully awesome icon
     *
     * @param type
     * @return
     */
    public Label getAwesomeIcon(IconType type) {
        Label label = new Label(type.getUnicode() + "");
        label.getStyleClass().add("block-awesome-icon");
        return label;
    }
}

enum PortTypes {

    IN,
    OUT
}

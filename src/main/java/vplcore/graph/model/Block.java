package vplcore.graph.model;

import java.util.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.input.*;
import jo.vpl.xml.BlockTag;
import vplcore.FontAwesomeIcon;
import vplcore.IconType;
import vplcore.Util;
import vplcore.graph.model.BlockExceptionPanel.BlockException;
import vplcore.graph.util.SelectBlock;
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

    BlockInfoPanel infoPanel;
    BlockExceptionPanel exceptionPanel;

    public ResizeButton resizeButton;
    public VplButton infoButton = new VplButton(IconType.FA_INFO_CIRCLE);
    public VplButton exceptionButton = new VplButton(IconType.FA_WARNING);

    private final EventHandler<MouseEvent> blockEnteredHandler = this::handleBlockEntered;
    private final EventHandler<MouseEvent> blockExitedHandler = this::handleBlockExited;
    private final EventHandler<MouseEvent> blockPressedHandler = this::handleBlockPressed;
    private final EventHandler<MouseEvent> blockDraggedHandler = this::handleBlockDragged;
    private final EventHandler<MouseEvent> resizeButtonPressedHandler = this::handleResizeButtonPressed;
    private final EventHandler<MouseEvent> resizeButtonDraggedHandler = this::handleResizeButtonDragged;
    private final ChangeListener<Object> portDataChangedListener = this::handlePortDataChanged;
    private final ChangeListener<Boolean> selectionChangedListener = this::handleSelectionChanged;
    private final ChangeListener<Number> blockWidthChangedListener = this::handleBlockWidthChanged;
    private final EventHandler<ActionEvent> infoButtonClickedHandler = this::handleInfoButtonClicked;
    private final EventHandler<ActionEvent> exceptionButtonClickedHandler = this::handleExceptionButtonClicked;

    public Block(Workspace workspace) {
        super(workspace);
        uuid = UUID.randomUUID();

        inPorts = new ArrayList<>();
        outPorts = new ArrayList<>();
        controls = new ArrayList<>();

        //Content Grid is the actual block box without the buttons on top etc.
        contentGrid = new GridPane();
        contentGrid.setAlignment(Pos.CENTER);
        contentGrid.addEventHandler(MouseEvent.MOUSE_ENTERED, blockEnteredHandler);
        contentGrid.addEventHandler(MouseEvent.MOUSE_EXITED, blockExitedHandler);
        contentGrid.addEventHandler(MouseEvent.MOUSE_PRESSED, blockPressedHandler);
        selected.addListener(selectionChangedListener);

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

        this.add(contentGrid, 1, 1);

        if (this instanceof SelectBlock) {
            return;
        }

        //Open block info panel on clicking question button
        infoButton.setOnAction(infoButtonClickedHandler);
        infoButton.setVisible(false);
        exceptionButton.setOnAction(exceptionButtonClickedHandler);
        exceptionButton.setVisible(true);
        menuBox.getChildren().addAll(exceptionButton, infoButton);

        this.widthProperty().addListener(blockWidthChangedListener);
    }

    private void handleBlockWidthChanged(Object b, Number o, Number n) {
        if (infoPanel != null) {
            double dX = n.doubleValue() - o.doubleValue();
            infoPanel.move(dX, 0);
        }
    }

    public void setResizable(boolean resizable) {
        if (resizable) {
            resizeButton = new ResizeButton();
            resizeButton.setVisible(false);
            contentGrid.setStyle("-fx-padding: 10 0 0 0");
            contentGrid.add(resizeButton, 2, 3);
            resizeButton.addEventHandler(MouseEvent.MOUSE_PRESSED, resizeButtonPressedHandler);
            resizeButton.addEventHandler(MouseEvent.MOUSE_DRAGGED, resizeButtonDraggedHandler);
            this.resizable = true;
        }
    }

    private void handleInfoButtonClicked(ActionEvent event) {
        if (workspace.activeBlockInfoPanel != null) {
            workspace.activeBlockInfoPanel.delete();
        }
        BlockInfoPanel info = new BlockInfoPanel(this);
        workspace.activeBlockInfoPanel = info;
        infoPanel = info;
        infoButton.setVisible(false);
    }

    private void handleExceptionButtonClicked(ActionEvent event) {
        if (workspace.activeBlockInfoPanel != null) {
            workspace.activeBlockInfoPanel.delete();
        }
        BlockExceptionPanel exception = new BlockExceptionPanel(this);
        Exception e1 = new Exception("Short message! 🧐");
        Exception e2 = new Exception("""
                                     Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus eget odio vel purus sodales ullamcorper. Sed id suscipit ante, vitae molestie quam. Donec turpis nulla, rhoncus ac fermentum sit amet, tempus non justo. Proin mattis fringilla dui. Curabitur elementum, odio ut porta rhoncus, quam sapien fermentum augue, vitae mattis risus velit quis mauris. Nam eleifend tortor ac dignissim aliquam. In bibendum magna sed erat ultricies, id imperdiet odio ultrices. Etiam in euismod nunc. Nullam varius lacus eu est aliquet tempus. Fusce suscipit, enim vel maximus tristique, erat mauris hendrerit quam, ac convallis augue dui id nulla. Praesent convallis diam non nunc cursus feugiat. Nullam gravida, tortor a bibendum iaculis, erat mauris dapibus lacus, eu lobortis turpis enim luctus quam. Morbi sed lectus suscipit nibh lacinia viverra. Fusce laoreet tortor at risus molestie ultrices.
                                                                           
                                     Vivamus pellentesque eros mi, nec commodo leo sagittis mollis. Suspendisse ultricies ac nisi id facilisis. Sed ac nisl quis neque blandit vestibulum. Nunc ullamcorper odio at ante tincidunt ultrices. Aliquam nec varius sem. Donec sed convallis nibh. Donec nec ultricies tellus, at pulvinar tortor. Nullam enim dolor, malesuada sit amet libero euismod, imperdiet faucibus elit. Ut ligula dui, luctus vel venenatis at, vehicula in metus. Nunc ultricies id nunc sit amet dignissim. Maecenas et nunc lacus. Donec sit amet sapien hendrerit turpis interdum vulputate a vitae metus.
                                                                      
                                     Praesent non tincidunt orci. Morbi egestas ex velit, eget laoreet ipsum posuere et. Morbi tempor lacinia tincidunt. Mauris vitae arcu sed neque aliquam malesuada. Suspendisse a efficitur mi, ac vestibulum elit. Donec luctus gravida dui vel mollis. Ut gravida urna lorem, sed tincidunt elit pellentesque sed. Mauris viverra pharetra purus, nec ultricies enim rhoncus dictum. Ut odio purus, scelerisque quis arcu sed, ullamcorper tincidunt risus. Praesent ac velit ut nibh rutrum malesuada id non nulla.
                                     """);
        Exception e3 = new Exception("This is a mad exception that was thrown off the block! Not sure how long this message should be, but let us find out if it just grows and grows and grows.");
        Exception e4 = new Exception("This is the second mad exception that was thrown off the block! Not sure how long this message should be, but let us find out if it just grows and grows and grows.");
        List<BlockExceptionPanel.BlockException> list = new ArrayList<>();
        list.add(new BlockException("[0]", BlockExceptionPanel.Severity.ERROR, e1));
        list.add(new BlockException("[1]", BlockExceptionPanel.Severity.ERROR, e2));
        list.add(new BlockException("[2]", BlockExceptionPanel.Severity.ERROR, e3));
        list.add(new BlockException("[3]", BlockExceptionPanel.Severity.ERROR, e4));
        exception.setExceptions(list);
        workspace.activeBlockInfoPanel = exception;
        exceptionPanel = exception;
        exceptionButton.setVisible(false);
        infoButton.setVisible(true);
    }

    @Override
    public void handleVplElementEntered(MouseEvent event) {
        super.handleVplElementEntered(event);
        if (Block.this instanceof SelectBlock) {
            return;
        }
        boolean infoPanelIsActive = infoPanel != null;
        if (!infoPanelIsActive) {
            infoButton.setVisible(true);
        }
        if (resizable) {
            resizeButton.setVisible(true);
        }
    }

    @Override
    public void handleVplElementExited(MouseEvent event) {
        super.handleVplElementEntered(event);
        if (Block.this instanceof SelectBlock) {
            return;
        }
        infoButton.setVisible(false);
        if (resizable) {
            resizeButton.setVisible(false);
        }
    }

    private void handleResizeButtonPressed(MouseEvent event) {
        oldMousePosition = new Point2D(event.getSceneX(), event.getSceneY());
    }

    private void handleResizeButtonDragged(MouseEvent event) {
        resizeBlock(event);
    }

    private void resizeBlock(MouseEvent event) {
        double scale = workspace.getZoomFactor();
        double deltaX = (event.getSceneX() - oldMousePosition.getX()) / scale;
        double deltaY = (event.getSceneY() - oldMousePosition.getY()) / scale;
        double oldWidth = contentGrid.getPrefWidth();
        double oldHeight = contentGrid.getPrefHeight();
        double newWidth = Math.max(oldWidth + deltaX, contentGrid.getMinWidth());
        double newHeight = Math.max(oldHeight + deltaY, contentGrid.getMinHeight());
        contentGrid.setPrefWidth(newWidth);
        contentGrid.setPrefHeight(newHeight);
        oldMousePosition = new Point2D(event.getSceneX(), event.getSceneY());
        contentGrid.layout();
    }

    public void handleBlockPressed(MouseEvent event) {
        updateSelection(event);
    }

    /**
     * Event handler for selection of blocks and possible followed up dragging
     * of them by the user.
     *
     * @param event
     */
    private void updateSelection(MouseEvent event) {

        if (workspace.selectedBlockSet.contains(this)) {
            if (Util.isModifierDown(event)) {
                // Remove this node from selection
                workspace.selectedBlockSet.remove(this);
                setSelected(false);
            } else {
                // Subscribe multiselection to MouseMove event
                for (Block block : workspace.selectedBlockSet) {
                    block.addEventHandler(MouseEvent.MOUSE_DRAGGED, blockDraggedHandler);
                    block.oldMousePosition = new Point2D(event.getSceneX(), event.getSceneY());
                }
            }
        } else {
            if (Util.isModifierDown(event)) {
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
                    this.addEventHandler(MouseEvent.MOUSE_DRAGGED, blockDraggedHandler);

                    //Get mouse position so there is a value to calculate 
                    //in the mouse dragged event
                    block.oldMousePosition = new Point2D(event.getSceneX(), event.getSceneY());
                }
            }
        }
        event.consume();
    }

    public void handleBlockDragged(MouseEvent event) {
        move(event);
    }

    public void move(MouseEvent event) {
        double scale = workspace.getZoomFactor();
        double deltaX = (event.getSceneX() - oldMousePosition.getX()) / scale;
        double deltaY = (event.getSceneY() - oldMousePosition.getY()) / scale;
        for (Block block : workspace.selectedBlockSet) {
            block.setLayoutX(block.getLayoutX() + deltaX);
            block.setLayoutY(block.getLayoutY() + deltaY);
        }
        if (infoPanel != null) {
            infoPanel.move(deltaX, deltaY);
        }
        oldMousePosition = new Point2D(event.getSceneX(), event.getSceneY());
    }

    /**
     * Add a port to the block, multiple connections are not allowed by default
     *
     * @param name the string that shows up as comment
     * @param type the dataProperty type it will be handling
     * @return the Port
     */
    public Port addInPortToBlock(String name, Class<?> type) {
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
    public Port addInPortToBlock(String name, Class<?> type, boolean multiDockAllowed) {
        Port port = new Port(name, this, Port.Type.IN, type);
        port.multiDockAllowed = multiDockAllowed;
        inPortBox.getChildren().add(port);
        port.dataProperty().addListener(portDataChangedListener);
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
        port.dataProperty().addListener(portDataChangedListener);
        inPorts.add(port.index, port);
        return port;
    }

    /**
     * Remove a port from the block
     *
     * @param port the port to remove
     */
    public void removeInPortFromBlock(Port port) {
        for (Connection connection : port.connectedConnections) {
            connection.removeFromCanvas();
        }
        inPortBox.getChildren().remove(port);
        port.dataProperty().removeListener(portDataChangedListener);
        inPorts.remove(port);
    }

    public void handlePortDataChanged(ObservableValue obj, Object oldVal, Object newVal) {
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

    /**
     * Add a port to the block, multiple outgoing connections are allowed
     *
     * @param name the string that shows up as comment
     * @param type the dataProperty type it will be handling
     * @return the Port
     */
    public Port addOutPortToBlock(String name, Class type) {
        Port port = new Port(name, this, Port.Type.OUT, type);
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

    /**
     * Remove this block from the host canvas
     */
    @Override
    public void delete() {
        super.delete();
        if (resizable) {
            resizeButton.removeEventHandler(MouseEvent.MOUSE_PRESSED, resizeButtonPressedHandler);
            resizeButton.removeEventHandler(MouseEvent.MOUSE_DRAGGED, resizeButtonDraggedHandler);
        }
        contentGrid.removeEventHandler(MouseEvent.MOUSE_ENTERED, blockEnteredHandler);
        contentGrid.removeEventHandler(MouseEvent.MOUSE_EXITED, blockExitedHandler);
        contentGrid.removeEventHandler(MouseEvent.MOUSE_PRESSED, blockPressedHandler);
        selected.addListener(selectionChangedListener);
        this.removeEventHandler(MouseEvent.MOUSE_DRAGGED, blockDraggedHandler);
        for (Port port : inPorts) {
            port.dataProperty().removeListener(portDataChangedListener);
            port.delete();
        }
        inPorts.clear();
        outPorts.clear();
        controls.clear();
        workspace.blockSet.remove(this);
        if (infoPanel != null) {
            infoPanel.delete();
        }
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
    protected void handleIncomingConnectionAdded(Port source, Port incoming) {
        calculate();
    }

    /**
     * Called when an incoming connection is removed. Ideal for forwarding a
     * data type to an out port e.g. blocks operating on collections. Its
     * removed counterpart is used to set the data type of the out port back to
     * its initial state. Only called when multi dock is not allowed!
     *
     * @param source port the connection was removed from
     */
    protected void handleIncomingConnectionRemoved(Port source) {
        calculate();
    }

    public abstract void calculate();

    @Override
    public abstract Block clone();

    public void serialize(BlockTag xmlTag) {
        xmlTag.setType(this.getClass().getAnnotation(BlockInfo.class).identifier());
        xmlTag.setUUID(uuid.toString());
        xmlTag.setX(getLayoutX());
        xmlTag.setY(getLayoutY());
        if (resizable) {
            xmlTag.setWidth(getPrefWidth());
            xmlTag.setHeight(getPrefHeight());
        }
    }

    public void deserialize(BlockTag xmlTag) {
        uuid = UUID.fromString(xmlTag.getUUID());
        setLayoutX(xmlTag.getX());
        setLayoutY(xmlTag.getY());
        if (resizable) {
            contentGrid.setPrefWidth(xmlTag.getWidth());
            contentGrid.setPrefHeight(xmlTag.getHeight());
        }
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

    protected void handleBlockEntered(MouseEvent event) {
        workspace.portDisconnector.hideRemoveButton();
        Block.this.setActive(true);
        Block.this.updateStyle();
    }

    protected void handleBlockExited(MouseEvent event) {
        //Change focus on exit to workspace so controls do not interrupt key events
        Block.this.workspace.requestFocus();
        Block.this.setActive(false);
        Block.this.updateStyle();
    }

    public void handleSelectionChanged(ObservableValue<? extends Boolean> arg0, Boolean oldVal, Boolean newVal) {
        updateStyle();
    }

    public void updateStyle() {
        if (isSelected()) {
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

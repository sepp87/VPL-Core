package vplcore.workspace.input;

import javafx.event.Event;
import vplcore.graph.model.Block;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.control.Control;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape3D;
import vplcore.graph.util.SelectBlock;
import vplcore.workspace.Actions;
import vplcore.workspace.Workspace;
import static vplcore.workspace.Workspace.clamp;
import vplcore.workspace.radialmenu.RadialMenu;

/**
 *
 * @author joostmeulenkamp
 */
public class MouseInputHandler {

    // mouse: right click > radial menu
    // mouse: left double cick > select block
    // mouse: left press, drag and release > selection rectangle
    // mouse: right press, drag and release > pan
    private final Workspace workspace;

    public MouseInputHandler(Workspace workspace) {
        this.workspace = workspace;

        //TODO this method listener should be removed, use real listeners instead of method references
        this.workspace.sceneProperty().addListener(this::addInputHandlers);
    }

    private void addInputHandlers(Object obj, Object oldVal, Object newVal) {

        workspace.getScene().setOnMouseMoved(this::handle_MouseMove);

//        workspace.getScene().setOnMouseReleased(this::handle_MouseRelease);
//        workspace.getScene().setOnMousePressed(this::handle_MousePress);
//        workspace.getScene().setOnMouseDragged(this::handle_MouseDrag);
//        workspace.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
//        workspace.getScene().addEventFilter(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
//
        workspace.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, onMouseClickedEventHandler);
        workspace.getScene().addEventFilter(ScrollEvent.SCROLL, onScrollEventHandler);
        workspace.getScene().addEventFilter(ScrollEvent.SCROLL_STARTED, onScrollEventHandler);
        workspace.getScene().addEventFilter(ScrollEvent.SCROLL_FINISHED, onScrollEventHandler);

        workspace.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, mousePressedHandler);
        workspace.getScene().addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDraggedHandler);
        workspace.getScene().addEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);

//        workspace.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, mouseClickedHandler);
//        workspace.getScene().addEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
//        workspace.getScene().addEventFilter(ScrollEvent.SCROLL, mouseScrollHandler);
//        workspace.getScene().addEventFilter(ScrollEvent.SCROLL_STARTED, mouseScrollStartedHandler);
//        workspace.getScene().addEventFilter(ScrollEvent.SCROLL_FINISHED, mouseScrollFinishedHandler);
    }
    private EventHandler<MouseEvent> mouseClickedHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {

        }
    };
    private EventHandler<MouseEvent> mousePressedHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {

            // register mouse position
            mousePosition = workspace.sceneToLocal(event.getSceneX(), event.getSceneY());
            
            if (event.isPrimaryButtonDown() && !onBlock(event)) {
                prepareSelectionRectangle(event);

            } else if (event.isSecondaryButtonDown() && !onBlock(event)) {
                preparePan(event);

            }
        }
    };

    private EventHandler<MouseEvent> mouseDraggedHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {

            boolean isSelecting = workspace.startSelectionPoint != null;
            if (event.isPrimaryButtonDown() && isSelecting) {
                startSelectionRectangleIfNull();
                updateSelectionRectangle(event);
                updateSelection();

            } else if (event.isSecondaryButtonDown()) {
                pan(event);
            }
        }
    };

    private EventHandler<MouseEvent> mouseReleasedHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            workspace.startSelectionPoint = null;

            if (event.getButton() == MouseButton.PRIMARY) {
                
                boolean wasSelecting = workspace.selectionRectangle != null;
                if (wasSelecting) {
                    removeSelectionRectangle();

                } else {
                    Actions.deselectAllBlocks(workspace);
                }

                if (event.getClickCount() == 2 && !onBlock(event) && event.isDragDetect()) {
                    showSelectBlock(event);
                }
            } else if (event.getButton() == MouseButton.SECONDARY) {
                boolean wasPanning = workspace.panContext != null;
                if (wasPanning) {
                    removePan();
                }

            }
        }
    };

    private boolean onBlock(MouseEvent event) {
        Node node = event.getPickResult().getIntersectedNode();
        return workspace.checkParent(node, Block.class);
    }

    private void prepareSelectionRectangle(MouseEvent event) {
        workspace.startSelectionPoint = workspace.sceneToLocal(event.getSceneX(), event.getSceneY());
    }

    private void startSelectionRectangleIfNull() {
        if (workspace.selectionRectangle != null) {
            return;
        }
        workspace.selectionRectangle = new Region();
        workspace.selectionRectangle.setLayoutX(workspace.startSelectionPoint.getX());
        workspace.selectionRectangle.setLayoutY(workspace.startSelectionPoint.getY());
        workspace.selectionRectangle.setMinSize(0, 0);

        workspace.selectionRectangle.getStyleClass().add("selection-rectangle");
        workspace.getChildren().add(workspace.selectionRectangle);
    }

    private void updateSelectionRectangle(MouseEvent event) {

        Point2D currentPosition = workspace.sceneToLocal(event.getSceneX(), event.getSceneY());
        Point2D delta = currentPosition.subtract(workspace.startSelectionPoint);

        if (delta.getX() < 0) {
            workspace.selectionRectangle.setLayoutX(currentPosition.getX());
        }

        if (delta.getY() < 0) {
            workspace.selectionRectangle.setLayoutY(currentPosition.getY());
        }

        workspace.selectionRectangle.setMinSize(Math.abs(delta.getX()), Math.abs(delta.getY()));

    }

    private void updateSelection() {
        for (Block block : workspace.blockSet) {
            if ((block.getLayoutX() >= workspace.selectionRectangle.getLayoutX())
                    && block.getLayoutX() + block.getWidth() <= workspace.selectionRectangle.getLayoutX() + workspace.selectionRectangle.getWidth()
                    && (block.getLayoutY() >= workspace.selectionRectangle.getLayoutY()
                    && block.getLayoutY() + block.getHeight() <= workspace.selectionRectangle.getLayoutY() + workspace.selectionRectangle.getHeight())) {
                workspace.selectedBlockSet.add(block);
                block.setSelected(true);
            } else {
                workspace.selectedBlockSet.remove(block);
                block.setSelected(false);
            }
        }
    }

    private void removeSelectionRectangle() {
        workspace.getChildren().remove(workspace.selectionRectangle);
        workspace.selectionRectangle = null;
        workspace.startSelectionPoint = null;
    }

    private void preparePan(MouseEvent event) {
        workspace.panContext = new DragContext();
        workspace.panContext.setX(event.getSceneX());
        workspace.panContext.setY(event.getSceneY());
        workspace.panContext.setTranslateX(workspace.getTranslateX());
        workspace.panContext.setTranslateY(workspace.getTranslateY());
    }

    private void pan(MouseEvent event) {
        workspace.setTranslateX(workspace.panContext.getTranslateX() + event.getSceneX() - workspace.panContext.getX());
        workspace.setTranslateY(workspace.panContext.getTranslateY() + event.getSceneY() - workspace.panContext.getY());
    }

    private void removePan() {
        workspace.panContext = null;
    }

    private void showSelectBlock(MouseEvent event) {
        if (workspace.selectBlock != null) {
            workspace.getChildren().remove(workspace.selectBlock);
        }
        workspace.selectBlock = new SelectBlock(workspace);
        workspace.selectBlock.setLayoutX(workspace.sceneToLocal(event.getX(), event.getY()).getX() - 20);
        workspace.selectBlock.setLayoutY(workspace.sceneToLocal(event.getX(), event.getY()).getY() - 20);
        workspace.getChildren().add(workspace.selectBlock);
    }

    private EventHandler<MouseEvent> mouseMovedHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {

        }
    };

    private EventHandler<ScrollEvent> mouseScrollHandler = new EventHandler<>() {
        @Override
        public void handle(ScrollEvent event) {

        }
    };
    private EventHandler<ScrollEvent> mouseScrollStartedHandler = new EventHandler<>() {
        @Override
        public void handle(ScrollEvent event) {

        }
    };
    private EventHandler<ScrollEvent> mouseScrollFinishedHandler = new EventHandler<>() {
        @Override
        public void handle(ScrollEvent event) {

        }
    };

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    private EventHandler<MouseEvent> onMouseClickedEventHandler = new EventHandler<MouseEvent>() {

        @Override
        public void handle(MouseEvent event) {

            Node node = event.getPickResult().getIntersectedNode();
            boolean onBlock = workspace.checkParent(node, Block.class);
            boolean onRadialMenu = workspace.checkParent(node, RadialMenu.class);
            boolean isSecondary = event.getButton() == MouseButton.SECONDARY;
            boolean isNotDragged = event.isStillSincePress();

//            System.out.println(node.getClass().getSimpleName());
//            boolean onControl = workspace.checkParent(node, Control.class);
//            boolean onViewer = workspace.checkParent(node, Shape3D.class);
            if (isSecondary && isNotDragged && !onRadialMenu && !onBlock) {
                workspace.radialMenu.show(event.getSceneX(), event.getSceneY());
            } else if (onRadialMenu) {
                // keep radial menu shown if it is clicked on
            } else {
                workspace.radialMenu.hide();
            }
        }
    };


    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////

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
            boolean onControl = workspace.checkParent(node, Control.class);
            boolean onViewer = workspace.checkParent(node, SubScene.class);
            boolean onModel = workspace.checkParent(node, Shape3D.class);
            if (onViewer || onControl || onModel) {
                return;
            }

//            double delta = 1.2;
            double delta = 1.05;

            double scale = workspace.getScale(); // currently we only use Y, same value is used for X
            double oldScale = scale;

            if (e.getDeltaY() < 0) {
                scale /= delta;
            } else {
                scale *= delta;
            }

            scale = clamp(scale, workspace.MIN_SCALE, workspace.MAX_SCALE);

            double f = (scale / oldScale) - 1;

            Bounds hack = workspace.localToParent(workspace.zoomPane.getBoundsInParent());

            double dx = (e.getSceneX() - (hack.getWidth() / 2 + hack.getMinX()));
            double dy = (e.getSceneY() - (hack.getHeight() / 2 + hack.getMinY()));

            workspace.setScale(scale);

            // note: pivot value must be untransformed, i. e. without scaling
            workspace.setPivot(f * dx, f * dy);

            e.consume();
        }
    };

    public Point2D mousePosition = new Point2D(0, 0);

    private void handle_MouseMove(MouseEvent e) {

        mousePosition = workspace.sceneToLocal(e.getSceneX(), e.getSceneY());

        switch (workspace.splineMode) {
            case SplineMode.NOTHING:
                workspace.clearTempLine();
                break;

            case SplineMode.FIRST:
                break;

            case SplineMode.SECOND:
                if (workspace.tempLine == null) {
                    workspace.tempLine = new Line();
                    workspace.tempLine.getStyleClass().add("temp-line");
                    workspace.getChildren().add(0, workspace.tempLine);
                }

                workspace.tempLine.startXProperty().bind(workspace.tempStartPort.centerXProperty);
                workspace.tempLine.startYProperty().bind(workspace.tempStartPort.centerYProperty);
                workspace.tempLine.setEndX(workspace.sceneToLocal(e.getSceneX(), e.getSceneY()).getX());
                workspace.tempLine.setEndY(workspace.sceneToLocal(e.getSceneX(), e.getSceneY()).getY());

                break;

            default:
                throw new IndexOutOfBoundsException("Argument out of range.");

        }
    }

}

package vplcore.workspace.input;

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
import vplcore.workspace.Workspace;
import static vplcore.workspace.Workspace.clamp;
import vplcore.workspace.radialmenu.RadialMenu;

/**
 *
 * @author joostmeulenkamp
 */
public class MouseInputHandler {

    private final Workspace workspace;

    public MouseInputHandler(Workspace workspace) {
        this.workspace = workspace;

        //TODO this method listener should be removed, use real listeners instead of method references
        this.workspace.sceneProperty().addListener(this::addInputHandlers);
    }

    private void addInputHandlers(Object obj, Object oldVal, Object newVal) {

        workspace.getScene().setOnMouseReleased(this::handle_MouseRelease);
        workspace.getScene().setOnMousePressed(this::handle_MousePress);
        workspace.getScene().setOnMouseDragged(this::handle_MouseDrag);
        workspace.getScene().setOnMouseMoved(this::handle_MouseMove);

        workspace.getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
        workspace.getScene().addEventFilter(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
        workspace.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, onMouseClickedEventHandler);
        workspace.getScene().addEventFilter(ScrollEvent.SCROLL, onScrollEventHandler);
        workspace.getScene().addEventFilter(ScrollEvent.SCROLL_STARTED, onScrollEventHandler);
        workspace.getScene().addEventFilter(ScrollEvent.SCROLL_FINISHED, onScrollEventHandler);
    }

    private EventHandler<MouseEvent> onMouseClickedEventHandler = new EventHandler<MouseEvent>() {

        public void handle(MouseEvent e) {

            // Check if mouse was on Controls
            Node node = e.getPickResult().getIntersectedNode();
            boolean onControl = workspace.checkParent(node, Control.class);
            boolean onViewer = workspace.checkParent(node, Shape3D.class);
            boolean onRadialMenu = workspace.checkParent(node, RadialMenu.class);

            if (onViewer || onControl) {
                return;
            }

            if (workspace.radialMenu.isVisible() && !onRadialMenu && e.getButton() == MouseButton.PRIMARY) {
                workspace.radialMenu.hide();
                return;
            }

            // right mouse button => open menu
            if (e.getButton() != MouseButton.SECONDARY) {
                return;
            }

            if (!e.isStillSincePress()) {
                return;
            }

            workspace.radialMenu.show(e.getSceneX(), e.getSceneY());
        }
    };

    private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

        public void handle(MouseEvent e) {

            // right mouse button => panning
            if (!e.isSecondaryButtonDown()) {
                return;
            }
            workspace.panContext.setX(e.getSceneX());
            workspace.panContext.setY(e.getSceneY());
            workspace.panContext.setTranslateX(workspace.getTranslateX());
            workspace.panContext.setTranslateY(workspace.getTranslateY());
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
            boolean onControl = workspace.checkParent(node, Control.class);
            boolean onViewer = workspace.checkParent(node, SubScene.class);
            boolean onModel = workspace.checkParent(node, Shape3D.class);
            if (onViewer || onControl || onModel) {
                return;
            }

            workspace.setTranslateX(workspace.panContext.getTranslateX() + e.getSceneX() - workspace.panContext.getX());
            workspace.setTranslateY(workspace.panContext.getTranslateY() + e.getSceneY() - workspace.panContext.getY());
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

    private void handle_MousePress(MouseEvent e) {

//        System.out.println(e.getPickResult().getIntersectedNode().getClass());
        switch (workspace.mouseMode) {
            case MouseMode.NOTHING:
                if (e.isPrimaryButtonDown()) {

                    // Check if mouse click was on a block
                    Node node = e.getPickResult().getIntersectedNode();
                    boolean mouseUpOnBlock = workspace.checkParent(node, Block.class);

                    if (!mouseUpOnBlock) {

                        workspace.startSelectionPoint = workspace.sceneToLocal(e.getSceneX(), e.getSceneY());

                        workspace.mouseMode = MouseMode.SELECT;

                        workspace.splineMode = SplineMode.NOTHING;

                    }
                } else if (e.isSecondaryButtonDown()) {

                }

                break;
        }
    }

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

    private void handle_MouseDrag(MouseEvent e) {

        // click and drag mouse button => selection
        if (workspace.mouseMode == MouseMode.SELECT) {

            if (e.isPrimaryButtonDown()) {
                if (workspace.selectionRectangle == null) {

                    workspace.selectionRectangle = new Region();
                    workspace.selectionRectangle.setLayoutX(workspace.startSelectionPoint.getX());
                    workspace.selectionRectangle.setLayoutY(workspace.startSelectionPoint.getY());
                    workspace.selectionRectangle.setMinSize(
                            workspace.sceneToLocal(e.getSceneX(), e.getSceneY()).getX(),
                            workspace.sceneToLocal(e.getSceneX(), e.getSceneY()).getY());

                    workspace.selectionRectangle.getStyleClass().add("selection-rectangle");

                    workspace.getChildren().add(workspace.selectionRectangle);
                }

                Point2D currentPosition = workspace.sceneToLocal(e.getSceneX(), e.getSceneY());
                Point2D delta = currentPosition.subtract(workspace.startSelectionPoint);

                if (delta.getX() < 0) {
                    workspace.selectionRectangle.setLayoutX(currentPosition.getX());
                }

                if (delta.getY() < 0) {
                    workspace.selectionRectangle.setLayoutY(currentPosition.getY());
                }

                workspace.selectionRectangle.setMinSize(Math.abs(delta.getX()), Math.abs(delta.getY()));

                for (Block block : workspace.blockSet) {
                    workspace.selectedBlockSet.remove(block);
                    block.setSelected(false);

                    if ((block.getLayoutX() >= workspace.selectionRectangle.getLayoutX())
                            && block.getLayoutX() + block.getWidth() <= workspace.selectionRectangle.getLayoutX() + workspace.selectionRectangle.getWidth()
                            && (block.getLayoutY() >= workspace.selectionRectangle.getLayoutY()
                            && block.getLayoutY() + block.getHeight() <= workspace.selectionRectangle.getLayoutY() + workspace.selectionRectangle.getHeight())) {
                        workspace.selectedBlockSet.add(block);
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
            workspace.mouseMode = MouseMode.NOTHING;
        }

        // Check if mouse click was on a block
        Node node = e.getPickResult().getIntersectedNode();
        boolean mouseUpOnBlock = workspace.checkParent(node, Block.class);
        boolean mouseUpOnMenu = workspace.checkParent(node, RadialMenu.class);

        if (e.getClickCount() == 2 && e.isDragDetect() && !mouseUpOnBlock) {
            if (workspace.selectBlock != null) {
                workspace.getChildren().remove(workspace.selectBlock);
            }
            workspace.selectBlock = new SelectBlock(workspace);
            workspace.selectBlock.setLayoutX(workspace.sceneToLocal(e.getX(), e.getY()).getX() - 20);
            workspace.selectBlock.setLayoutY(workspace.sceneToLocal(e.getX(), e.getY()).getY() - 20);
            workspace.getChildren().add(workspace.selectBlock);
        }

        switch (workspace.mouseMode) {

            case MouseMode.NOTHING:

                // if mouse up in empty space unselect all blocks
                if (!mouseUpOnBlock && !mouseUpOnMenu && e.getButton() != MouseButton.SECONDARY) {
                    for (Block block : workspace.selectedBlockSet) {
                        block.setSelected(false);
                    }
                    workspace.selectedBlockSet.clear();
                }
                break;

            case MouseMode.SELECT:
                //Get mouse mode out of selection rectangle so UI can deselect nodes
                workspace.getChildren().remove(workspace.selectionRectangle);
                workspace.selectionRectangle = null;
                workspace.mouseMode = MouseMode.NOTHING;
                break;

        }
    }

}

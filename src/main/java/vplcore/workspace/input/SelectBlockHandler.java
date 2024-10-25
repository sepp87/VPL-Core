package vplcore.workspace.input;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import vplcore.graph.util.SelectBlock;
import vplcore.workspace.Workspace;

/**
 *
 * @author joostmeulenkamp
 */
public class SelectBlockHandler {

    private final ChangeListener<Object> initializationHandler = this::handleInitialization;
    private final ChangeListener<Boolean> visibilityListener = this::handleVisibility;
    private final EventHandler<MouseEvent> mouseClickedHandler = this::handleMouseClicked;

    private Workspace workspace;
    private SelectBlock selectBlock;

    public SelectBlockHandler(Workspace workspace) {
        this.workspace = workspace;
        initializeSelectBlock();
        workspace.sceneProperty().addListener(initializationHandler);
    }

    public void handleInitialization(ObservableValue<? extends Object> observableValue, Object oldObject, Object newObject) {
        workspace.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, mouseClickedHandler);
    }

    private void initializeSelectBlock() {
        selectBlock = new SelectBlock(workspace);
        selectBlock.setVisible(false);
        selectBlock.visibleProperty().addListener(visibilityListener);
    }

    public void handleMouseClicked(MouseEvent event) {
        System.out.println(workspace.getMouseMode() + " " + event.getButton());
        if (workspace.getMouseMode() == MouseMode.MOUSE_IDLE && event.getButton() == MouseButton.PRIMARY) {
            if (event.getClickCount() == 2 && !workspace.onBlock(event) && event.isDragDetect() && !workspace.onZoomView(event) && !workspace.onMenuBar(event) && !workspace.onBlockInfoPanel(event)) {
                showSelectBlock(event);
            }
        }
    }

    private void showSelectBlock(MouseEvent event) {
        selectBlock.setLayoutX(event.getX() - 20);
        selectBlock.setLayoutY(event.getY() - 20);
        selectBlock.setVisible(true);
    }

    public void handleVisibility(ObservableValue<? extends Boolean> observableValue, Boolean oldBoolean, Boolean isVisble) {
        if (isVisble) {
            workspace.setMouseMode(MouseMode.AWAITING_SELECT_BLOCK);
        } else {
            workspace.setMouseMode(MouseMode.MOUSE_IDLE);
        }
    }

    public SelectBlock getSelectBlock() {
        return selectBlock;
    }

}

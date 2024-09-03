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

    private Workspace workspace;
    private SelectBlock selectBlock;

    public SelectBlockHandler(Workspace workspace) {
        this.workspace = workspace;
        initializeSelectBlock();
        addInputHandlers();
    }
    
    public SelectBlock getSelectBlock() {
        return selectBlock;
    }

    private void initializeSelectBlock() {
        selectBlock = new SelectBlock(workspace);
        selectBlock.setVisible(false);
        selectBlock.visibleProperty().addListener(visibilityHandler);
        workspace.getChildren().add(selectBlock);
    }

    private void addInputHandlers() {
        workspace.getScene().addEventFilter(MouseEvent.MOUSE_RELEASED, mouseReleasedHandler);
    }

    private final EventHandler<MouseEvent> mouseReleasedHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {
            if (workspace.getMouseMode() == MouseMode.MOUSE_IDLE && event.getButton() == MouseButton.PRIMARY) {
                if (event.getClickCount() == 2 && !workspace.onBlock(event) && event.isDragDetect()) {
                    showSelectBlock(event);
                }
            }
        }
    };

    private void showSelectBlock(MouseEvent event) {
        selectBlock.setLayoutX(workspace.sceneToLocal(event.getX(), event.getY()).getX() - 20);
        selectBlock.setLayoutY(workspace.sceneToLocal(event.getX(), event.getY()).getY() - 20);
        selectBlock.setVisible(true);
    }

    private final ChangeListener<Boolean> visibilityHandler = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldBoolean, Boolean isVisble) {
            if (isVisble) {
                workspace.setMouseMode(MouseMode.AWAITING_SELECT_BLOCK);
            } else {
                workspace.setMouseMode(MouseMode.MOUSE_IDLE);
            }
        }
    };

}

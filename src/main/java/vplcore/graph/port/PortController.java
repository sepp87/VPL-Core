package vplcore.graph.port;

import javafx.beans.value.ChangeListener;
import javafx.scene.input.MouseEvent;
import vplcore.graph.block.BlockController;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class PortController {

    private final BlockController blockController;

    private final PortModel model;
    private final PortView view;

    public PortController(BlockController blockController, PortModel model, PortView view) {
        this.blockController = blockController;
        this.model = model;
        this.view = view;

        model.activeProperty().addListener(activeListener);

        view.getTooltip().textProperty().bind(model.nameProperty());
        view.setOnMouseClicked(this::handlePortClicked);
        view.setOnMousePressed(this::handlePortPressed);
        view.setOnMouseDragged(this::handlePortDragged);
    }

    private void handlePortClicked(MouseEvent event) {
        if (event.isStillSincePress()) {
            blockController.initiateConnection(model);
        }
        event.consume();
    }

    private void handlePortPressed(MouseEvent event) {
        event.consume();
    }

    private void handlePortDragged(MouseEvent event) {
        event.consume();
    }

    private final ChangeListener<Boolean> activeListener = this::onActiveChanged;

    private void onActiveChanged(Object b, boolean o, boolean n) {
        view.setActive(n);
    }

    public void remove() {
        model.activeProperty().removeListener(activeListener);
        view.getTooltip().textProperty().unbind();
        view.setOnMouseClicked(null);
        view.setOnMousePressed(null);
        view.setOnMouseDragged(null);
    }
}

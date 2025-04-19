package btscore.graph.port;

import javafx.beans.value.ChangeListener;
import javafx.collections.SetChangeListener.Change;
import javafx.collections.SetChangeListener;
import javafx.scene.input.MouseEvent;
import btscore.graph.block.BlockController;
import btscore.graph.connection.ConnectionModel;

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
        
        view.idProperty().bind(model.idProperty());
        view.getTooltip().textProperty().bind(model.nameProperty());
        view.setOnMouseClicked(this::handlePortClicked);
        view.setOnMousePressed(this::ignoreDrag);
        view.setOnMouseDragged(this::ignoreDrag);
    }



    private void handlePortClicked(MouseEvent event) {
        if (event.isStillSincePress()) {
            blockController.initiateConnection(this);
        }
        event.consume();
    }

    private void ignoreDrag(MouseEvent event) {
        event.consume();
    }

    private final ChangeListener<Boolean> activeListener = this::onActiveChanged;

    private void onActiveChanged(Object b, boolean o, boolean n) {
        view.setActive(n);
    }

    public PortModel getModel() {
        return model;
    }
    
    public PortView getView() {
        return view;
    }

    public void remove() {
        model.activeProperty().removeListener(activeListener);
        view.getTooltip().textProperty().unbind();
        view.setOnMouseClicked(null);
        view.setOnMousePressed(null);
        view.setOnMouseDragged(null);
    }
}

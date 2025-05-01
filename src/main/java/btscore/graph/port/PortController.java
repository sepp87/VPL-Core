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
        model.nameProperty().addListener(toolTipSourceListener);
        model.dataTypeProperty().addListener(toolTipSourceListener);

        view.idProperty().bind(model.idProperty());
        view.setOnMouseClicked(this::handlePortClicked);
        view.setOnMousePressed(this::ignoreDrag);
        view.setOnMouseDragged(this::ignoreDrag);
        view.setActive(model.isActive());
        
        setToolTip();
    }

    private final ChangeListener<Object> toolTipSourceListener = this::onToolTipSourceChanged;

    private void onToolTipSourceChanged(Object b, Object o, Object n) {
        setToolTip();
    }

    private void setToolTip() {
        view.getTooltip().setText(model.nameProperty().get() + " : " + model.dataTypeProperty().get().getSimpleName());
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
        model.nameProperty().removeListener(toolTipSourceListener);
        model.dataTypeProperty().removeListener(toolTipSourceListener);

        view.setOnMouseClicked(null);
        view.setOnMousePressed(null);
        view.setOnMouseDragged(null);

    }
}

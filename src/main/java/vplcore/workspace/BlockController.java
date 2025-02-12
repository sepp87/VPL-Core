package vplcore.workspace;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author Joost
 */
public class BlockController {

    private final WorkspaceController workspaceController;
    private final BlockModel model;
    private final BlockView view;

    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    public BlockController(WorkspaceController workspaceController, BlockModel blockModel, BlockView blockView) {
        this.workspaceController = workspaceController;
        this.model = blockModel;
        this.view = blockView;

        view.setOnMouseEntered(model.onMouseEntered());

        view.idProperty().bind(model.idProperty());
        view.layoutXProperty().bind(model.layoutXProperty());
        view.layoutYProperty().bind(model.layoutYProperty());

        view.addControlToBlock(model.getCustomization());
        view.addInputPorts(model.getInputPorts());
        view.addOutputPorts(model.getOutputPorts());

    }

    public WorkspaceController getWorkspaceController() {
        return workspaceController;
    }

    public BlockView getView() {
        return view;
    }

    public BlockModel getModel() {
        return model;
    }

    public void remove() {
        view.setOnMouseEntered(null);

        view.idProperty().unbind();
        view.layoutXProperty().unbind();
        view.layoutYProperty().unbind();

    }

    public BooleanProperty selectedProperty() {
        return selected;
    }
}

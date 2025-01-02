package vplcore.workspace;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 *
 * @author Joost
 */
public class BlockController {

    private final BlockModel model;
    private final BlockView view;

    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    public BlockController(BlockModel blockModel, BlockView blockView) {
        this.model = blockModel;
        this.view = blockView;

        view.idProperty().bind(model.idProperty());
        view.layoutXProperty().bind(model.layoutXProperty());
        view.layoutYProperty().bind(model.layoutYProperty());
    }

    public BlockView getView() {
        return view;
    }

    public void remove() {
        view.idProperty().unbind();
        view.layoutXProperty().unbind();
        view.layoutYProperty().unbind();
    }
    
    
}

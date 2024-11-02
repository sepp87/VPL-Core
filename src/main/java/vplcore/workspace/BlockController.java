package vplcore.workspace;

/**
 *
 * @author Joost
 */
public class BlockController {

    private final BlockModel model;
    private final BlockView view;
    
    public BlockController(BlockModel blockModel, BlockView blockView) {
        this.model = blockModel;
        this.view = blockView;
        
        view.idProperty().bind(model.idProperty());
    }
}

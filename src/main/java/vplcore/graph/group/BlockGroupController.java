package vplcore.graph.group;

import java.util.Collection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import vplcore.context.ActionManager;
import vplcore.context.StateManager;
import vplcore.context.command.RemoveGroupCommand;
import vplcore.editor.BaseController;
import vplcore.graph.block.BlockController;
import vplcore.graph.block.BlockModel;
import vplcore.graph.block.BlockView;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author JoostMeulenkamp
 */
public class BlockGroupController extends BaseController {

    private final ActionManager actionManager;
    private final StateManager state;

    private final WorkspaceController workspaceController;

    private final BlockGroupModel model;
    private final BlockGroupView view;

    private final ObservableMap<BlockModel, BlockController> children;

    public BlockGroupController(WorkspaceController workspaceController, BlockGroupModel blockGroupModel, BlockGroupView blockGroupView) {
        super(workspaceController);
        this.actionManager = this.getEditorContext().getActionManager();
        this.state = this.getEditorContext().getStateManager();
        this.workspaceController = workspaceController;
        this.model = blockGroupModel;
        this.view = blockGroupView;
        this.children = FXCollections.observableHashMap();

        // Handlers
        view.setOnMouseEntered(this::handleMouseEntered);
        view.setOnMouseExited(this::handleMouseExited);
        view.setOnMousePressed(this::handleGroupPressed);
        view.setOnMouseReleased(this::handleGroupReleased);
        view.getBinButton().setOnAction(this::handleBinButtonClicked);

        // Listeners
        model.getBlocks().addListener(blocksListener);

        // Bindings
        view.getLabel().textProperty().bindBidirectional(model.nameProperty());
    }

    public void setBlocks(Collection<BlockController> blocks) {
        for (BlockController blockController : blocks) {
            children.put(blockController.getModel(), blockController);
            addListeners(blockController);
        }
        calculateSize();
    }

    public void remove() {
        // Handlers
        view.setOnMouseEntered(null);
        view.setOnMouseExited(null);
        view.setOnMousePressed(null);
        view.setOnMouseReleased(null);
        view.getBinButton().setOnAction(null);

        // Listeners
        model.getBlocks().removeListener(blocksListener);

        // Bindings
        view.getLabel().textProperty().unbindBidirectional(model.nameProperty());


        for (BlockController blockController : children.values()) {
            removeListeners(blockController);
        }
    }

    public BlockGroupView getView() {
        return view;
    }

    private final SetChangeListener<BlockModel> blocksListener = this::onBlocksChanged;

    private void onBlocksChanged(SetChangeListener.Change<? extends BlockModel> change) {
        System.out.println("BlockGroupController.onBlocksChanged()");
        if (change.wasAdded()) {
            BlockModel blockModel = change.getElementAdded();
            BlockController blockController = workspaceController.getBlockController(blockModel);
            addListeners(blockController);
            children.put(blockModel, blockController);
        } else {
            BlockModel blockModel = change.getElementRemoved();
            BlockController blockController = children.get(blockModel);
            removeListeners(blockController);
            children.remove(blockModel);
        }
        calculateSize();
    }

    private void handleBinButtonClicked(ActionEvent event) {
        RemoveGroupCommand command = new RemoveGroupCommand(actionManager.getWorkspaceModel(), model);
        actionManager.executeCommand(command);
    }

    private void handleMouseEntered(MouseEvent event) {
        view.getLabel().setVisible(true);
        view.getBinButton().setVisible(true);
    }

    private void handleMouseExited(MouseEvent event) {
        view.getLabel().setVisible(false);
        view.getBinButton().setVisible(false);
    }

    private void handleGroupPressed(MouseEvent event) {
        for (BlockController blockController : children.values()) {
            blockController.startPoint = new Point2D(event.getSceneX(), event.getSceneY());
            workspaceController.selectBlock(blockController);
        }
        state.setSelectingBlockGroup(); // prevent group from being deselected
    }

    private void handleGroupReleased(MouseEvent event) {
        state.setIdle();
//        event.consume();
    }

    private void addListeners(BlockController blockController) {
        BlockView blockView = blockController.getView();
        blockView.layoutXProperty().addListener(blockTransformedListener);
        blockView.layoutYProperty().addListener(blockTransformedListener);
        blockView.widthProperty().addListener(blockTransformedListener);
        blockView.heightProperty().addListener(blockTransformedListener);
    }

    private void removeListeners(BlockController blockController) {
        BlockView blockView = blockController.getView();
        blockView.layoutXProperty().removeListener(blockTransformedListener);
        blockView.layoutYProperty().removeListener(blockTransformedListener);
        blockView.widthProperty().removeListener(blockTransformedListener);
        blockView.heightProperty().removeListener(blockTransformedListener);
    }

    private final ChangeListener<Object> blockTransformedListener = this::onBlockTransformed; // is this listening to transforms e.g. move and resize? otherwise groupBlockTransformedListener

    private void onBlockTransformed(ObservableValue b, Object o, Object n) {
        // TODO optimize here so only the changed block model is used the re-calculate the size
        calculateSize();
    }

    private void calculateSize() {
        if (children.isEmpty()) {
            return;
        }

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for (BlockController blockController : children.values()) {
            BlockView blockView = blockController.getView();

            if (blockView.layoutXProperty().get() < minX) {
                minX = blockView.layoutXProperty().get();
            }
            if (blockView.layoutYProperty().get() < minY) {
                minY = blockView.layoutYProperty().get();
            }
            if ((blockView.layoutXProperty().get() + blockView.widthProperty().get()) > maxX) {
                maxX = blockView.layoutXProperty().get() + blockView.widthProperty().get();
            }
            if ((blockView.layoutYProperty().get() + blockView.heightProperty().get()) > maxY) {
                maxY = blockView.layoutYProperty().get() + blockView.heightProperty().get();
            }

//            System.out.println("x:" + blockView.layoutXProperty().get() + " y:" + blockView.layoutYProperty().get() + " w" + blockView.widthProperty().get() + " h" + blockView.heightProperty().get());
        }

        view.setPrefSize(maxX - minX, maxY - minY);
        view.relocate(minX, minY);
    }

}

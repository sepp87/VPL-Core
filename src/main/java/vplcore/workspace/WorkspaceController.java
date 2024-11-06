package vplcore.workspace;

import java.util.Collection;
import vplcore.graph.model.Connection;
import vplcore.graph.model.Block;
import vplcore.graph.model.BlockGroup;
import javafx.geometry.Point2D;
import vplcore.App;
import vplcore.context.StateManager;
import vplcore.editor.BaseController;
import vplcore.graph.model.BlockInfoPanel;
import vplcore.graph.model.Port;
import vplcore.graph.model.VplElement;
import vplcore.graph.util.PreConnection;

/**
 *
 * @author JoostMeulenkamp
 */
public class WorkspaceController extends BaseController {

    private final StateManager state;
    private final WorkspaceModel model;
    private final WorkspaceView view;
    private final WorkspaceZoomHelper zoomHelper;
    private final WorkspaceSelectionHelper selectionHelper;

    public BlockInfoPanel activeBlockInfoPanel;
    public boolean typeSensitive = true;

    //Radial menu
    public WorkspaceController(String contextId, WorkspaceModel workspaceModel, WorkspaceView workspaceView) {
        super(contextId);
        this.state = App.getContext(contextId).getStateManager();
        this.model = workspaceModel;
        this.view = workspaceView;
        this.zoomHelper = new WorkspaceZoomHelper(model, view);
        this.selectionHelper = new WorkspaceSelectionHelper(model, view);
    }

    private PreConnection preConnection = null;

    // rename to initiateConnection and when PreConnection != null, then turn PreConnection into a real connection
    public void initiateConnection(Port port) {
        if (preConnection == null) {
            preConnection = new PreConnection(WorkspaceController.this, port);
            view.getChildren().add(0, preConnection);
        }
    }

    // method is unneeded if createConnection catches the second click
    public void removeChild(PreConnection preConnection) {
        view.getChildren().remove(preConnection);
        this.preConnection = null;
    }

    public WorkspaceModel getModel() {
        return model;
    }

    public void reset() {
        model.reset();
        view.getChildren().clear();
    }

    public void setIdle() {
        state.setIdle();
    }

    public void setSelectingBlockGroup() {
        state.setSelectingBlockGroup();
    }

    public double getZoomFactor() {
        return model.zoomFactorProperty().get();
    }

    public void zoomIn() {
        zoomHelper.zoomIn();
    }

    public void zoomOut() {
        zoomHelper.zoomOut();
    }

    public void applyZoom(double newScale, Point2D pivotPoint) {
        zoomHelper.applyZoom(newScale, pivotPoint);
    }

    public void zoomToFit() {
        Collection<Block> blocks = !getSelectedBlocks().isEmpty() ? getSelectedBlocks() : model.getBlocks();
        zoomHelper.zoomToFit(blocks);
    }

    public void selectAllBlocks() {
        selectionHelper.selectAllBlocks();
    }

    public void deselectAllBlocks() {
        selectionHelper.deselectAllBlocks();
    }

    public void rectangleSelect(Point2D selectionMin, Point2D selectionMax) {
        selectionHelper.rectangleSelect(selectionMin, selectionMax);
    }

    public WorkspaceView getView() {
        return view;
    }

    public void addBlock(Block block) {
        model.addBlock(block);
        if (block.isSelected()) {
            selectionHelper.selectBlock(block);
        }
        view.getChildren().add(block);
    }

    public void selectBlock(Block block) {
        selectionHelper.selectBlock(block);
    }

    public void deselectBlock(Block block) {
        selectionHelper.deselectBlock(block);
    }

    public Collection<Block> getBlocks() {
        return model.getBlocks();
    }

    public Collection<Block> getSelectedBlocks() {
        return selectionHelper.getSelectedBlocks();
    }

    public Collection<Connection> getConnections() {
        return model.getConnections();
    }

    public Collection<BlockGroup> getBlockGroups() {
        return model.getBlockGroups();
    }

    public <E extends VplElement> void removeChild(E element) {
        System.out.println("WorkspaceController REIMPLEMENT " + element.getClass().getSimpleName() + " removed");
    }

    public void removeChild(Connection connection) {
        System.out.println("WorkspaceController Connection removed");
        model.removeConnection(connection);
        view.getChildren().remove(connection);
    }

    public void removeChild(Block block) {
        System.out.println("WorkspaceController " + block.getClass().getSimpleName() + " removed");
        model.removeBlock(block);
//        blocksSelectedOnWorkspace.remove(block);
        view.getChildren().remove(block);
    }

    public void removeChild(BlockGroup blockGroup) {
        System.out.println("WorkspaceController " + blockGroup.getClass().getSimpleName() + " removed");
        model.removeBlockGroup(blockGroup);
        view.getChildren().remove(blockGroup);
    }

    public void addConnection(Port startPort, Port endPort) {
        Connection connection = new Connection(this, startPort, endPort);
        addConnection(connection);
    }

    public void addConnection(Connection connection) {
        model.addConnection(connection);
        view.getChildren().add(0, connection);
    }

    public void addBlockGroup() {
        if (getSelectedBlocks().size() <= 1) {
            return;
        }
        BlockGroup blockGroup = new BlockGroup(this);
        model.addBlockGroup(blockGroup);
        blockGroup.setChildBlocks(getSelectedBlocks());
        view.getChildren().add(0, blockGroup);
    }

}

package vplcore.workspace;

import java.util.Collection;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import vplcore.context.ActionManager;
import vplcore.context.command.MoveBlocksCommand;
import vplcore.context.command.UpdateSelectionCommand;
import vplcore.editor.BaseController;
import vplcore.util.EventUtils;

/**
 *
 * @author Joost
 */
public class BlockController extends BaseController {

    private final ActionManager actionManager;
    private final WorkspaceController workspaceController;
    private final BlockModel model;
    private final BlockView view;

    private final BooleanProperty selected = new SimpleBooleanProperty(false);

    public Point2D startPoint;
    public Point2D updatedPoint;

    public BlockController(WorkspaceController workspaceController, BlockModel blockModel, BlockView blockView) {
        super(workspaceController);
        this.actionManager = this.getEditorContext().getActionManager();
        this.workspaceController = workspaceController;
        this.model = blockModel;
        this.view = blockView;

        selected.addListener(selectionListener);

        view.getContentGrid().setOnMouseEntered(model.onMouseEntered());
        view.getContentGrid().setOnMousePressed(this::handleMoveStartedAndUpdateSelection);
        view.getContentGrid().setOnMouseDragged(this::handleMoveUpdated);
        view.getContentGrid().setOnMouseDragReleased(this::handleMoveFinished);
        view.getContentGrid().addEventHandler(MouseEvent.MOUSE_EXITED, blockExitedHandler);

        view.idProperty().bind(model.idProperty());
        view.layoutXProperty().bindBidirectional(model.layoutXProperty());
        view.layoutYProperty().bindBidirectional(model.layoutYProperty());

        view.addControlToBlock(model.getCustomization());
        view.addInputPorts(model.getInputPorts());
        view.addOutputPorts(model.getOutputPorts());

    }

    private final EventHandler<MouseEvent> blockExitedHandler = this::handleBlockExited;


    protected void handleBlockExited(MouseEvent event) {
        //Change focus on exit to workspace so controls do not interrupt key events
        this.getEditorContext().returnFocusToEditor();
    }

    ChangeListener<Boolean> selectionListener = this::onSelectionChanged;

    private void onSelectionChanged(Object b, Boolean o, Boolean n) {
        view.setSelected(n);
    }

    private void handleMoveStartedAndUpdateSelection(MouseEvent event) {
        startPoint = new Point2D(event.getSceneX(), event.getSceneY());
        updatedPoint = startPoint;
        UpdateSelectionCommand command = new UpdateSelectionCommand(actionManager.getWorkspaceController(), this, EventUtils.isModifierDown(event));
        actionManager.executeCommand(command);
        event.consume();
    }

    public void handleMoveUpdated(MouseEvent event) {
        double scale = workspaceController.getZoomFactor();
        double deltaX = (event.getSceneX() - updatedPoint.getX()) / scale;
        double deltaY = (event.getSceneY() - updatedPoint.getY()) / scale;
        for (BlockController block : workspaceController.getSelectedBlockControllers()) {
            BlockModel blockModel = block.getModel();
            double x = blockModel.layoutXProperty().get();
            double y = blockModel.layoutYProperty().get();
            blockModel.layoutXProperty().set(x + deltaX);
            blockModel.layoutYProperty().set(y + deltaY);
        }
        updatedPoint = new Point2D(event.getSceneX(), event.getSceneY());
    }

    public void handleMoveFinished(MouseEvent event) {
        Collection<BlockController> blockControllers = workspaceController.getSelectedBlockControllers();
        Point2D delta = startPoint.subtract(updatedPoint);
        MoveBlocksCommand command = new MoveBlocksCommand(blockControllers, delta);
        actionManager.executeCommand(command);
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

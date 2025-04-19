package btscore.graph.block;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import btscore.App;
import btscore.context.ActionManager;
import btscore.context.command.MoveBlocksCommand;
import btscore.context.command.ResizeBlockCommand;
import btscore.context.command.UpdateSelectionCommand;
import btscore.editor.BaseController;
import btscore.graph.block.ExceptionPanel.BlockException;
import btscore.graph.port.PortController;
import btscore.graph.port.PortModel;
import btscore.graph.port.PortType;
import btscore.graph.port.PortView;
import btscore.util.EventUtils;
import btscore.workspace.WorkspaceController;

/**
 *
 * @author Joost
 */
public class BlockController extends BaseController {

    private final ActionManager actionManager;
    private final WorkspaceController workspaceController;
    private final BlockModel model;
    private final BlockView view;

    private final ObservableMap<PortModel, PortController> ports = FXCollections.observableHashMap();

    private final BooleanProperty selected = new SimpleBooleanProperty(false);
    private boolean infoShown = false;
    private boolean exceptionShown = false;

    public Point2D startPoint;
    private Point2D updatedPoint;

    private double previousWidth = -1;
    private double previousHeight = -1;

    public BlockController(WorkspaceController workspaceController, BlockModel blockModel, BlockView blockView) {
        super(workspaceController);
        this.actionManager = this.getEditorContext().getActionManager();
        this.workspaceController = workspaceController;
        this.model = blockModel;
        this.view = blockView;

        this.model.getExceptions().addListener(exceptionsListener);
        showExceptionButton();

        selected.addListener(selectionListener);

        view.setOnMouseEntered(this::handleMouseEntered);
        view.setOnMouseExited(this::handleMouseExited);
        view.getContentGrid().setOnMouseEntered(model.onMouseEntered());
        view.getContentGrid().setOnMousePressed(this::handleMoveStartedAndUpdateSelection);
        view.getContentGrid().setOnMouseDragged(this::handleMoveUpdated);
        view.getContentGrid().setOnMouseReleased(this::handleMoveFinished);

        view.getInfoButton().setOnAction(this::handleInfoButtonClicked);
        view.getExceptionButton().setOnAction(this::handleExceptionButtonClicked);

        view.layoutXProperty().addListener(transformListener);
        view.layoutYProperty().addListener(transformListener);

        view.idProperty().bind(model.idProperty());
        view.layoutXProperty().bindBidirectional(model.layoutXProperty());
        view.layoutYProperty().bindBidirectional(model.layoutYProperty());
        view.getCaptionLabel().textProperty().bindBidirectional(model.nameProperty());

        view.addControlToBlock(model.getCustomization());

        addPorts(model.getInputPorts(), PortType.INPUT);
        addPorts(model.getOutputPorts(), PortType.OUTPUT);

        if (model.resizableProperty().get()) {
            view.getContentGrid().prefWidthProperty().bind(model.widthProperty());
            view.getContentGrid().prefHeightProperty().bind(model.heightProperty());
            ResizeButton resizeButton = view.getResizeButton();
            resizeButton.setOnMousePressed(this::handleResizeStarted);
            resizeButton.setOnMouseDragged(this::handleResizeUpdated);
            resizeButton.setOnMouseReleased(this::handleResizeFinished);
        }
    }

    private void addPorts(List<PortModel> portModels, PortType portType) {
        List<PortView> portViews = new ArrayList<>();
        for (PortModel portModel : portModels) {
            PortView portView = new PortView(portType);
            portView.boundsInParentProperty().addListener(transformListener);
            portViews.add(portView);
            PortController portController = new PortController(this, portModel, portView);
            ports.put(portModel, portController);
            workspaceController.registerPort(portController);
        }
        if (portType == PortType.INPUT) {
            view.addInputPorts(portViews);
        } else {
            view.addOutputPorts(portViews);
        }
    }

    private final ChangeListener<Object> transformListener = this::onTransformCalculatePortCenter;

    private void onTransformCalculatePortCenter(Object b, Object o, Object n) {
        for (PortController portController : ports.values()) {
            PortView portView = portController.getView();
            Point2D centerInScene = portView.localToScene(portView.getWidth() / 2, portView.getHeight() / 2);
            Point2D centerInLocal = workspaceController.getView().sceneToLocal(centerInScene);
            portView.centerXProperty().set(centerInLocal.getX());
            portView.centerYProperty().set(centerInLocal.getY());
        }
    }

    public void initiateConnection(PortController portController) {
        workspaceController.initiateConnection(portController);
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    ChangeListener<Boolean> selectionListener = this::onSelectionChanged;

    private void onSelectionChanged(Object b, Boolean o, Boolean n) {
        view.setSelected(n);
    }

    private void handleMouseEntered(MouseEvent event) {
        view.getCaptionLabel().setVisible(true);

        if (!infoShown) {
            view.getInfoButton().setVisible(true);
        }

        if (model.resizableProperty().get()) {
            view.getResizeButton().setVisible(true);
        }
    }

    private void handleMouseExited(MouseEvent event) {
        view.getCaptionLabel().setVisible(false);
        view.getInfoButton().setVisible(false);

        if (model.resizableProperty().get()) {
            view.getResizeButton().setVisible(false);
        }
    }

    private void handleMoveStartedAndUpdateSelection(MouseEvent event) {
        view.toFront();
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
        if (!event.isDragDetect()) {
            Collection<BlockController> blockControllers = workspaceController.getSelectedBlockControllers();
            Point2D delta = updatedPoint.subtract(startPoint);
            MoveBlocksCommand command = new MoveBlocksCommand(blockControllers, delta);
            actionManager.executeCommand(command);
        }
    }

    private final ListChangeListener<BlockException> exceptionsListener = this::onExceptionsChanged;

    private void onExceptionsChanged(Change<? extends BlockException> change) {
        if (App.LOG_METHOD_CALLS) {
            System.out.println("BlockController.onExceptionsChanged()");
        }
        showExceptionButton();
    }

    private void showExceptionButton() {
        if (App.LOG_METHOD_CALLS) {
            System.out.println("BlockController.showExceptionButton() exceptionShown " + exceptionShown);
        }

        if (exceptionShown) {
            return;
        }
        if (this.model.getExceptions().isEmpty()) {
            view.getExceptionButton().setVisible(false);
        } else {
            view.getExceptionButton().setVisible(true);
        }
    }

    public void onInfoPanelRemoved() {
        infoShown = false;
    }

    public void onExceptionPanelRemoved() {
        exceptionShown = false;
        showExceptionButton();
    }

    private void handleInfoButtonClicked(ActionEvent event) {
        workspaceController.showInfoPanel(this);
        view.getInfoButton().setVisible(false);
        infoShown = true;
    }

    private void handleExceptionButtonClicked(ActionEvent event) {
        workspaceController.showExceptionPanel(this);
        view.getExceptionButton().setVisible(false);
        exceptionShown = true;
    }

    private void handleResizeStarted(MouseEvent event) {
        startPoint = new Point2D(event.getSceneX(), event.getSceneY());
        updatedPoint = startPoint;
        GridPane contentGrid = view.getContentGrid();
        previousWidth = contentGrid.getWidth();
        previousHeight = contentGrid.getHeight();
        model.widthProperty().set(previousWidth);
        model.heightProperty().set(previousHeight);
    }

    private void handleResizeUpdated(MouseEvent event) {
        double scale = workspaceController.getZoomFactor();
        double deltaX = (event.getSceneX() - updatedPoint.getX()) / scale;
        double deltaY = (event.getSceneY() - updatedPoint.getY()) / scale;
        double newWidth = model.widthProperty().get() + deltaX;
        double newHeight = model.heightProperty().get() + deltaY;
        model.widthProperty().set(newWidth);
        model.heightProperty().set(newHeight);
        updatedPoint = new Point2D(event.getSceneX(), event.getSceneY());
    }

    private void handleResizeFinished(MouseEvent event) {
        if (!event.isDragDetect()) {
            double newWidth = model.widthProperty().get();
            double newHeight = model.heightProperty().get();
            ResizeBlockCommand command = new ResizeBlockCommand(this, newWidth, newHeight);
            actionManager.executeCommand(command);
        }
    }

    public double getPreviousWidth() {
        return previousWidth;
    }

    public double getPreviousHeight() {
        return previousHeight;
    }

    public void setSize(double width, double height) {
        GridPane contentGrid = view.getContentGrid();
        contentGrid.setPrefSize(width, height);
    }

    public BlockView getView() {
        return view;
    }

    public BlockModel getModel() {
        return model;
    }

    public void remove() {

        this.model.getExceptions().removeListener(exceptionsListener);
        selected.removeListener(selectionListener);

        view.setOnMouseEntered(null);
        view.setOnMouseExited(null);
        view.getContentGrid().setOnMouseEntered(null);
        view.getContentGrid().setOnMousePressed(null);
        view.getContentGrid().setOnMouseDragged(null);
        view.getContentGrid().setOnMouseReleased(null);

        view.getInfoButton().setOnAction(null);
        view.getExceptionButton().setOnAction(null);

        view.layoutXProperty().removeListener(transformListener);
        view.layoutYProperty().removeListener(transformListener);

        view.idProperty().unbind();
        view.layoutXProperty().unbindBidirectional(model.layoutXProperty());
        view.layoutYProperty().unbindBidirectional(model.layoutYProperty());
        view.getCaptionLabel().textProperty().unbindBidirectional(model.nameProperty());
        view.getCaptionLabel().remove();

        if (model.resizableProperty().get()) {
            view.getContentGrid().prefWidthProperty().unbind();
            view.getContentGrid().prefHeightProperty().unbind();
            ResizeButton resizeButton = view.getResizeButton();
            resizeButton.setOnMousePressed(null);
            resizeButton.setOnMouseDragged(null);
            resizeButton.setOnMouseReleased(null);
        }

        for (PortController portController : ports.values()) {
            portController.getView().boundsInParentProperty().removeListener(transformListener);
            workspaceController.unregisterPort(portController);
        }

    }

}

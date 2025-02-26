package vplcore.graph.block;

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
import vplcore.context.ActionManager;
import vplcore.context.command.MoveBlocksCommand;
import vplcore.context.command.ResizeBlockCommand;
import vplcore.context.command.UpdateSelectionCommand;
import vplcore.editor.BaseController;
import vplcore.graph.block.ExceptionPanel.BlockException;
import vplcore.graph.port.PortController;
import vplcore.graph.port.PortModel;
import vplcore.graph.port.PortType;
import vplcore.graph.port.PortView;
import vplcore.util.EventUtils;
import vplcore.workspace.WorkspaceController;

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

        view.widthProperty().addListener(layoutXListener);
        view.layoutXProperty().addListener(layoutXListener);
        view.layoutYProperty().addListener(layoutYListener);

        view.idProperty().bind(model.idProperty());
        view.layoutXProperty().bindBidirectional(model.layoutXProperty());
        view.layoutYProperty().bindBidirectional(model.layoutYProperty());
        view.getCaptionLabel().textProperty().bindBidirectional(model.nameProperty());

        view.addControlToBlock(model.getCustomization());
//        view.addInputPorts(model.getInputPorts());
//        view.addOutputPorts(model.getOutputPorts());

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

    private final ListChangeListener<BlockException> exceptionsListener = this::onExceptionsChanged;

    private void onExceptionsChanged(Change<? extends BlockException> change) {
        System.out.println("WorkspaceController.onExceptionsChanged()");
        if (this.model.getExceptions().isEmpty()) {
            // hide button and remove exception panel
            view.getExceptionButton().setVisible(false);

        } else {
            // show button and create exception panel
            view.getExceptionButton().setVisible(true);

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
        boolean infoPanelIsActive = view.getInfoPanel() != null;
        if (!infoPanelIsActive) {
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
        //Change focus on exit to workspace so controls do not interrupt key events
        this.getEditorContext().returnFocusToEditor();
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
            Point2D delta = startPoint.subtract(updatedPoint);
            MoveBlocksCommand command = new MoveBlocksCommand(blockControllers, delta);
            actionManager.executeCommand(command);
        }
    }

    private void handleInfoButtonClicked(ActionEvent event) {
        if (workspaceController.activeBlockModelInfoPanel != null) {
            workspaceController.activeBlockModelInfoPanel.remove();
        }
        InfoPanel infoPanel = new InfoPanel(workspaceController.getView(), this);
        int position = workspaceController.getBlockGroups().size() + 1;
//        workspaceController.getView().getChildren().add(position, infoPanel);
        workspaceController.getView().getInfoLayer().getChildren().add(infoPanel);
        workspaceController.activeBlockModelInfoPanel = infoPanel;
        view.setInfoPanel(infoPanel);
        view.getInfoButton().setVisible(false);
    }

    private void handleExceptionButtonClicked(ActionEvent event) {
        if (workspaceController.activeBlockModelInfoPanel != null) {
            workspaceController.activeBlockModelInfoPanel.remove();
        }
        ExceptionPanel exceptionPanel = new ExceptionPanel(workspaceController.getView(), this);
        int position = workspaceController.getBlockGroups().size() + 1;
//        workspaceController.getView().getChildren().add(position, exceptionPanel);
        workspaceController.getView().getInfoLayer().getChildren().add( exceptionPanel);

//        Exception e1 = new Exception("Short message! 🧐");
//        Exception e2 = new Exception("""
//                                     Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus eget odio vel purus sodales ullamcorper. Sed id suscipit ante, vitae molestie quam. Donec turpis nulla, rhoncus ac fermentum sit amet, tempus non justo. Proin mattis fringilla dui. Curabitur elementum, odio ut porta rhoncus, quam sapien fermentum augue, vitae mattis risus velit quis mauris. Nam eleifend tortor ac dignissim aliquam. In bibendum magna sed erat ultricies, id imperdiet odio ultrices. Etiam in euismod nunc. Nullam varius lacus eu est aliquet tempus. Fusce suscipit, enim vel maximus tristique, erat mauris hendrerit quam, ac convallis augue dui id nulla. Praesent convallis diam non nunc cursus feugiat. Nullam gravida, tortor a bibendum iaculis, erat mauris dapibus lacus, eu lobortis turpis enim luctus quam. Morbi sed lectus suscipit nibh lacinia viverra. Fusce laoreet tortor at risus molestie ultrices.
//                                                                           
//                                     Vivamus pellentesque eros mi, nec commodo leo sagittis mollis. Suspendisse ultricies ac nisi id facilisis. Sed ac nisl quis neque blandit vestibulum. Nunc ullamcorper odio at ante tincidunt ultrices. Aliquam nec varius sem. Donec sed convallis nibh. Donec nec ultricies tellus, at pulvinar tortor. Nullam enim dolor, malesuada sit amet libero euismod, imperdiet faucibus elit. Ut ligula dui, luctus vel venenatis at, vehicula in metus. Nunc ultricies id nunc sit amet dignissim. Maecenas et nunc lacus. Donec sit amet sapien hendrerit turpis interdum vulputate a vitae metus.
//                                                                      
//                                     Praesent non tincidunt orci. Morbi egestas ex velit, eget laoreet ipsum posuere et. Morbi tempor lacinia tincidunt. Mauris vitae arcu sed neque aliquam malesuada. Suspendisse a efficitur mi, ac vestibulum elit. Donec luctus gravida dui vel mollis. Ut gravida urna lorem, sed tincidunt elit pellentesque sed. Mauris viverra pharetra purus, nec ultricies enim rhoncus dictum. Ut odio purus, scelerisque quis arcu sed, ullamcorper tincidunt risus. Praesent ac velit ut nibh rutrum malesuada id non nulla.
//                                     """);
//        Exception e3 = new Exception("This is a mad exception that was thrown off the block! Not sure how long this message should be, but let us find out if it just grows and grows and grows.");
//        Exception e4 = new Exception("This is the second mad exception that was thrown off the block! Not sure how long this message should be, but let us find out if it just grows and grows and grows.");
//        List<ExceptionPanel.BlockException> list = new ArrayList<>();
//        list.add(new BlockException("[0]", ExceptionPanel.Severity.ERROR, e1));
//        list.add(new BlockException("[1]", ExceptionPanel.Severity.ERROR, e2));
//        list.add(new BlockException("[2]", ExceptionPanel.Severity.ERROR, e3));
//        list.add(new BlockException("[3]", ExceptionPanel.Severity.ERROR, e4));
        exceptionPanel.setExceptions(this.model.getExceptions());
        workspaceController.activeBlockModelInfoPanel = exceptionPanel;
        view.setExceptionPanel(exceptionPanel);
        view.getExceptionButton().setVisible(false);
        view.getInfoButton().setVisible(true);
    }

    private final ChangeListener<Number> layoutXListener = this::onLayoutXChanged;

    private void onLayoutXChanged(Object b, Number o, Number n) {
        InfoPanel infoPanel = view.getInfoPanel();
        infoPanel = infoPanel != null ? infoPanel : view.getExceptionPanel();

        if (infoPanel != null) {
            double dX = n.doubleValue() - o.doubleValue();
            infoPanel.move(dX, 0);
        }
    }

    private final ChangeListener<Number> layoutYListener = this::onLayoutYChanged;

    private void onLayoutYChanged(Object b, Number o, Number n) {
        InfoPanel infoPanel = view.getInfoPanel();
        infoPanel = infoPanel != null ? infoPanel : view.getExceptionPanel();

        if (infoPanel != null) {
            double dY = n.doubleValue() - o.doubleValue();
            infoPanel.move(0, dY);
        }
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

        selected.removeListener(selectionListener);

        view.setOnMouseEntered(null);
        view.setOnMouseExited(null);
        view.getContentGrid().setOnMouseEntered(null);
        view.getContentGrid().setOnMousePressed(null);
        view.getContentGrid().setOnMouseDragged(null);
        view.getContentGrid().setOnMouseReleased(null);

        view.getInfoButton().setOnAction(null);
        view.getExceptionButton().setOnAction(null);

        view.heightProperty().removeListener(transformListener);
        view.widthProperty().removeListener(transformListener);
        view.layoutXProperty().removeListener(transformListener);
        view.layoutYProperty().removeListener(transformListener);

        view.widthProperty().removeListener(layoutXListener);
        view.layoutXProperty().removeListener(layoutXListener);
        view.layoutYProperty().removeListener(layoutYListener);

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

        InfoPanel infoPanel = view.getInfoPanel();
        if (infoPanel != null) {
            infoPanel.remove();
        }

        infoPanel = view.getExceptionPanel();
        if (infoPanel != null) {
            infoPanel.remove();
        }

        for (PortController portController : ports.values()) {
            portController.getView().boundsInParentProperty().removeListener(transformListener);
            workspaceController.unregisterPort(portController);
        }

    }

}

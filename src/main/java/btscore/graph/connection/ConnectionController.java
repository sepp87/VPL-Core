package btscore.graph.connection;

import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.CubicCurve;
import btscore.context.ActionManager;
import btscore.context.command.RemoveConnectionCommand;
import btscore.editor.BaseController;
import btscore.graph.port.PortController;
import btscore.graph.port.PortType;
import btscore.graph.port.PortView;
import static btscore.util.EventUtils.isLeftClick;
import btscore.workspace.WorkspaceController;

/**
 *
 * @author JoostMeulenkamp
 */
public class ConnectionController extends BaseController {

    private final WorkspaceController workspaceController;

    private final ConnectionModel model;
    private final ConnectionView view;

    private final PortController startPortController;
    private final PortController endPortController;

    public ConnectionController(WorkspaceController workspaceController, ConnectionModel model, ConnectionView view) {
        super(workspaceController);
        this.workspaceController = workspaceController;
        this.model = model;
        this.view = view;

        this.startPortController = workspaceController.getPortController(model.getStartPort().getId());
        this.endPortController = workspaceController.getPortController(model.getEndPort().getId());

        bindCurves();
        addSnappingCurveListeners();
    }

    private void bindCurves() {
        bindCurve(view.getConnectionCurve());
        bindCurve(view.getSnappingCurve());
    }

    private void bindCurve(CubicCurve curve) {
        PortView startPortView = startPortController.getView();
        PortView endPortView = endPortController.getView();

        curve.controlX1Property().bind(Bindings.createDoubleBinding(() -> calculateControlX(startPortController), startPortView.centerXProperty(), endPortView.centerXProperty()));
        curve.controlY1Property().bind(startPortView.centerYProperty());
        curve.startXProperty().bind(startPortView.centerXProperty());
        curve.startYProperty().bind(startPortView.centerYProperty());

        curve.controlX2Property().bind(Bindings.createDoubleBinding(() -> calculateControlX(endPortController), startPortView.centerXProperty(), endPortView.centerXProperty()));
        curve.controlY2Property().bind(endPortView.centerYProperty());
        curve.endXProperty().bind(endPortView.centerXProperty());
        curve.endYProperty().bind(endPortView.centerYProperty());

    }

    private double calculateControlX(PortController portController) {
        PortView startPort = startPortController.getView();
        PortView endPort = endPortController.getView();
        Double dX = endPort.centerXProperty().get() - startPort.centerXProperty().get();
        Double dY = endPort.centerYProperty().get() - startPort.centerYProperty().get();
        Point2D vector = new Point2D(dX, dY);
        double distance = vector.magnitude() / 2;
        distance = (portController.getModel().getPortType() == PortType.OUTPUT) ? distance : -distance;
        return portController.getView().centerXProperty().get() + distance;
    }

    private void addSnappingCurveListeners() {
        view.getSnappingCurve().setOnMouseEntered(this::handleShowRemoveButton);
        view.getSnappingCurve().setOnMouseExited(this::handleHideRemoveButton);
        view.getSnappingCurve().setOnMouseMoved(this::handleMoveRemoveButton);
        view.getSnappingCurve().setOnMouseClicked(this::handleRemoveButtonClicked);
    }

    private void handleShowRemoveButton(MouseEvent event) {
        view.getRemoveButton().setVisible(true);
        positionRemoveButton(event);
    }

    private void handleMoveRemoveButton(MouseEvent event) {
        positionRemoveButton(event);
        event.consume();
    }

    private void handleHideRemoveButton(MouseEvent event) {
        view.getRemoveButton().setVisible(false);
    }

    private void positionRemoveButton(MouseEvent event) {
        Point2D mouse = view.sceneToLocal(event.getSceneX(), event.getSceneY());

        double closestDistance = Double.MAX_VALUE;
        double closestX = 0;
        double closestY = 0;

        CubicCurve connectionCurve = view.getConnectionCurve();

        for (double t = 0; t <= 1.0; t += 0.01) {
            double curveX = cubicCurvePoint(connectionCurve.getStartX(), connectionCurve.getControlX1(), connectionCurve.getControlX2(), connectionCurve.getEndX(), t);
            double curveY = cubicCurvePoint(connectionCurve.getStartY(), connectionCurve.getControlY1(), connectionCurve.getControlY2(), connectionCurve.getEndY(), t);

            double distance = Math.sqrt(Math.pow(mouse.getX() - curveX, 2) + Math.pow(mouse.getY() - curveY, 2));

            if (distance < closestDistance) {
                closestDistance = distance;
                closestX = curveX;
                closestY = curveY;
            }
        }

        view.getRemoveButton().setTranslateX(closestX);
        view.getRemoveButton().setTranslateY(closestY);
    }

    private double cubicCurvePoint(double start, double control1, double control2, double end, double t) {
        double u = 1 - t;
        return Math.pow(u, 3) * start + 3 * Math.pow(u, 2) * t * control1 + 3 * u * Math.pow(t, 2) * control2 + Math.pow(t, 3) * end;
    }

    private void handleRemoveButtonClicked(MouseEvent event) {
        if (!isLeftClick(event)) {
            return;
        }
        ActionManager actionManager = this.getEditorContext().getActionManager();
        RemoveConnectionCommand command = new RemoveConnectionCommand(actionManager.getWorkspaceModel(), model);
        actionManager.executeCommand(command);
    }

    public void remove() {
        unbindCurve(view.getConnectionCurve());
        unbindCurve(view.getSnappingCurve());
        removeSnappingCurveListeners();
    }

    private void unbindCurve(CubicCurve curve) {
        curve.controlX1Property().unbind();
        curve.controlY1Property().unbind();
        curve.startXProperty().unbind();
        curve.startYProperty().unbind();

        curve.controlX2Property().unbind();
        curve.controlY2Property().unbind();
        curve.endXProperty().unbind();
        curve.endYProperty().unbind();
    }

    private void removeSnappingCurveListeners() {
        view.getSnappingCurve().setOnMouseEntered(null);
        view.getSnappingCurve().setOnMouseExited(null);
        view.getSnappingCurve().setOnMouseMoved(null);
        view.getSnappingCurve().setOnMouseClicked(null);
    }

    public ConnectionView getView() {
        return view;
    }
}

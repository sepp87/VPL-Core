package vplcore.graph.connection;

import javafx.beans.binding.Bindings;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.CubicCurve;
import vplcore.context.ActionManager;
import vplcore.context.command.RemoveConnectionCommand;
import vplcore.editor.BaseController;
import vplcore.graph.port.PortModel;
import static vplcore.util.EventUtils.isLeftClick;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author JoostMeulenkamp
 */
public class ConnectionController extends BaseController {

    private final ConnectionModel model;
    private final ConnectionView view;

    public ConnectionController(WorkspaceController workspaceController, ConnectionModel model, ConnectionView view) {
        super(workspaceController);
        this.model = model;
        this.view = view;
        bindCurves();
        addSnappingCurveListeners();
    }

    private void bindCurves() {
        bindCurve(view.getConnectionCurve());
        bindCurve(view.getSnappingCurve());
    }

    private void bindCurve(CubicCurve curve) {
        PortModel startPort = model.getStartPort();
        PortModel endPort = model.getEndPort();

        curve.controlX1Property().bind(Bindings.createDoubleBinding(() -> calculateControlX(startPort), startPort.centerXProperty, endPort.centerXProperty));
        curve.startXProperty().bind(startPort.centerXProperty);
        curve.startYProperty().bind(startPort.centerYProperty);

        curve.controlX2Property().bind(Bindings.createDoubleBinding(() -> calculateControlX(startPort), startPort.centerXProperty, endPort.centerXProperty));
        curve.endXProperty().bind(endPort.centerXProperty);
        curve.endYProperty().bind(endPort.centerYProperty);
    }

    private double calculateControlX(PortModel port) {
        double dX = model.getStartPort().centerXProperty.get() - model.getEndPort().centerXProperty.get();
        double distance = Math.abs(dX) / 2;
        distance = port.portType == port.portType.OUT ? distance : -distance;
        return port.centerXProperty.get() + distance;
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

        // TODO move to dedicated remove method for ConnectionController
        unbindCurve(view.getConnectionCurve());
        unbindCurve(view.getSnappingCurve());

        removeSnappingCurveListeners();
    }

    private void unbindCurve(CubicCurve curve) {
        curve.controlX1Property().unbind();
        curve.startXProperty().unbind();
        curve.startYProperty().unbind();

        curve.controlX2Property().unbind();
        curve.endXProperty().unbind();
        curve.endYProperty().unbind();
    }

    private void removeSnappingCurveListeners() {
        view.getSnappingCurve().setOnMouseEntered(null);
        view.getSnappingCurve().setOnMouseExited(null);
        view.getSnappingCurve().setOnMouseMoved(null);
        view.getSnappingCurve().setOnMouseClicked(null);
    }
}

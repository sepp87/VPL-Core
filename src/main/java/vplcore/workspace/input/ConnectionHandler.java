package vplcore.workspace.input;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import vplcore.graph.model.Connection;
import vplcore.graph.model.Port;
import vplcore.graph.util.TypeExtensions;
import vplcore.workspace.Workspace;
import static vplcore.workspace.input.SplineMode.NOTHING;
import static vplcore.workspace.input.SplineMode.SECOND;

/**
 *
 * @author joostmeulenkamp
 */
public class ConnectionHandler {

    private Workspace workspace;
    public Line tempLine;
    public Port tempStartPort;

    public ConnectionHandler(Workspace workspace) {
        this.workspace = workspace;
        addInputHandlers();
    }

    private void addInputHandlers() {
        workspace.getScene().addEventFilter(MouseEvent.MOUSE_MOVED, mouseMovedHandler);
        workspace.getScene().addEventFilter(MouseEvent.MOUSE_CLICKED, mouseClickedHandler);
    }
    

    private final EventHandler<MouseEvent> mouseMovedHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {

            switch (workspace.splineMode) {
                case SplineMode.NOTHING:
                    clearTempLine();
                    break;

                case SplineMode.FIRST:
                    break;

                case SplineMode.SECOND:
                    if (tempLine == null) {
                        tempLine = new Line();
                        tempLine.getStyleClass().add("temp-line");
                        workspace.getChildren().add(0, tempLine);
                    }

                    tempLine.startXProperty().bind(tempStartPort.centerXProperty);
                    tempLine.startYProperty().bind(tempStartPort.centerYProperty);
                    tempLine.setEndX(workspace.sceneToLocal(event.getSceneX(), event.getSceneY()).getX());
                    tempLine.setEndY(workspace.sceneToLocal(event.getSceneX(), event.getSceneY()).getY());

                    break;

                default:
                    throw new IndexOutOfBoundsException("Argument out of range.");

            }
        }
    };

    public void startConnection(Port port) {
        
    }
    
    private final EventHandler<MouseEvent> mouseClickedHandler = new EventHandler<>() {
        @Override
        public void handle(MouseEvent event) {

        }
    };

    public void clearTempLine() {
        workspace.getChildren().remove(tempLine);
        tempLine = null;
    }
}

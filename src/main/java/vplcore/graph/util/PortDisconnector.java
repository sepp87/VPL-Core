package vplcore.graph.util;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import vplcore.graph.model.Connection;
import vplcore.workspace.Workspace;

/**
 *
 * @author joostmeulenkamp
 */
public class PortDisconnector {

    private Workspace workspace;
    private Group removeButton;

    public PortDisconnector(Workspace workspace) {
        this.workspace = workspace;
        initializeRemoveButton();
    }

    private void initializeRemoveButton() {
        Circle c = new Circle(0, 0, 10, Paint.valueOf("RED"));
        removeButton = new Group();
        removeButton.getChildren().add(c);
        removeButton.setVisible(false);
        workspace.getChildren().add(removeButton);
    }

    public void showRemoveButton(MouseEvent event) {
        Point2D coordinates = workspace.sceneToLocal(event.getSceneX(), event.getSceneY());

        removeButton.setTranslateX(event.getSceneX());
        removeButton.setTranslateY(event.getSceneY());
//System.out.println();
        removeButton.setVisible(true);
        System.out.println("ON LINE");
    }

    public void hideRemoveButton() {

    }

    public void removeConnection(Connection connection) {

    }
}

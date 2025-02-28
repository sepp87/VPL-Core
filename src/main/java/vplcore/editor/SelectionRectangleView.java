package vplcore.editor;

import javafx.scene.layout.Region;

/**
 *
 * @author joostmeulenkamp
 */
public class SelectionRectangleView extends Region {

    public SelectionRectangleView() {

        this.setVisible(false);
        this.getStyleClass().add("selection-rectangle");

        this.setLayoutX(0);
        this.setLayoutY(0);
        this.setMinSize(0, 0);
    }

}

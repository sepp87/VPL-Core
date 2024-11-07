package vplcore.workspace;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import vplcore.IconType;
import vplcore.graph.model.BlockExceptionPanel;
import vplcore.graph.model.BlockInfoPanel;
import vplcore.graph.model.BlockLabel;
import vplcore.graph.model.ResizeButton;
import vplcore.graph.model.VplButton;

/**
 *
 * @author Joost
 */
/**
 *
 * @author JoostMeulenkamp
 */
public class BlockView extends GridPane  {

    private final BlockLabel captionLabel;
    private final HBox menuBox;

    private final Pane inPortBox;
    private final Pane outPortBox;
    private final List<Region> controls;
    private final GridPane contentGrid;
    private final GridPane mainContentGrid;

    private BlockInfoPanel infoPanel;
    private BlockExceptionPanel exceptionPanel;

    private ResizeButton resizeButton;
    private final VplButton infoButton = new VplButton(IconType.FA_INFO_CIRCLE);
    private final VplButton exceptionButton = new VplButton(IconType.FA_WARNING);

    public BlockView() {

        menuBox = new HBox(5);
        captionLabel = new BlockLabel(menuBox);
        captionLabel.getStyleClass().add("vpl-tag");
        captionLabel.setVisible(false);

        menuBox.setAlignment(Pos.BOTTOM_LEFT);
        menuBox.getStyleClass().add("block-header");
        menuBox.getChildren().addAll(captionLabel);

        add(menuBox, 1, 0);

        controls = new ArrayList<>();

        //Content Grid is the actual block box without the buttons on top etc.
        contentGrid = new GridPane();
        contentGrid.setAlignment(Pos.CENTER);

        VBox in = new VBox();
        VBox out = new VBox();

        in.setAlignment(Pos.CENTER);
        out.setAlignment(Pos.CENTER);

        inPortBox = in;
        outPortBox = out;

        contentGrid.add(inPortBox, 0, 1);
        contentGrid.add(outPortBox, 2, 1);

        ColumnConstraints column1 = new ColumnConstraints();
        ColumnConstraints column2 = new ColumnConstraints();
        ColumnConstraints column3 = new ColumnConstraints();

        column1.setHgrow(Priority.NEVER);
        column2.setHgrow(Priority.ALWAYS);
        column3.setHgrow(Priority.NEVER);
        column3.setHalignment(HPos.RIGHT);

        contentGrid.getColumnConstraints().addAll(column1, column2, column3);

        contentGrid.getStyleClass().add("block");
        inPortBox.getStyleClass().add("in-port-box");
        outPortBox.getStyleClass().add("out-port-box");

        RowConstraints row1 = new RowConstraints();
        RowConstraints row2 = new RowConstraints();
        RowConstraints row3 = new RowConstraints();
        RowConstraints row4 = new RowConstraints();
        row1.setVgrow(Priority.NEVER);
        row2.setVgrow(Priority.ALWAYS);
        row3.setVgrow(Priority.NEVER);
        row4.setVgrow(Priority.NEVER);

        contentGrid.getRowConstraints().addAll(row1, row2, row3, row4);

        //Main content grid is -> the center for controls
        mainContentGrid = new GridPane();
        contentGrid.add(mainContentGrid, 1, 1);

        //Main content grid constraints to make content grow
        ColumnConstraints column = new ColumnConstraints();
        column.setHgrow(Priority.ALWAYS);
        column.setHalignment(HPos.CENTER);
        mainContentGrid.getColumnConstraints().addAll(column);

        this.add(contentGrid, 1, 1);

        //Open block info panel on clicking question button
        infoButton.setVisible(false);
        exceptionButton.setVisible(true);
        menuBox.getChildren().addAll(exceptionButton, infoButton);

    }

    /**
     * Add control to the block. A control extends region so it can be a layout,
     * but also a simple control like a button.
     *
     * @param control the control to add
     */
    public void addControlToBlock(Region control) {
        mainContentGrid.add(control, 0, mainContentGrid.getChildren().size());
        RowConstraints row = new RowConstraints();
        row.setVgrow(Priority.ALWAYS);
        mainContentGrid.getRowConstraints().add(row);
        controls.add(control);
    }

}

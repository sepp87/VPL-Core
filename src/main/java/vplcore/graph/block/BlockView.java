package vplcore.graph.block;

import vplcore.graph.base.BaseLabel;
import vplcore.graph.base.BaseButton;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import vplcore.IconType;
import vplcore.graph.port.PortView;

/**
 *
 * @author Joost
 */
/**
 *
 * @author JoostMeulenkamp
 */
public class BlockView extends GridPane {

    private final BaseLabel captionLabel;
    private final HBox menuBox;

    private final Pane inPortBox;
    private final Pane outPortBox;
    private final List<Region> controls;
    private final GridPane contentGrid;
    private final GridPane mainContentGrid;

    private final BaseButton infoButton = new BaseButton(IconType.FA_INFO_CIRCLE);
    private InfoPanel infoPanel;

    private final BaseButton exceptionButton = new BaseButton(IconType.FA_WARNING);
    private ExceptionPanel exceptionPanel;

    private ResizeButton resizeButton;

    public BlockView() {

        menuBox = new HBox(5);
        captionLabel = new BaseLabel(menuBox);
        captionLabel.getStyleClass().add("vpl-tag");
        captionLabel.setVisible(false);

        menuBox.setAlignment(Pos.BOTTOM_LEFT);
        menuBox.getStyleClass().add("block-header");
        menuBox.getChildren().addAll(captionLabel);

        this.add(menuBox, 1, 0);

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
        exceptionButton.setVisible(false);
        menuBox.getChildren().addAll(exceptionButton, infoButton);

    }

    public BaseLabel getCaptionLabel() {
        return captionLabel;
    }

    public GridPane getContentGrid() {
        return contentGrid;
    }

    public BaseButton getInfoButton() {
        return infoButton;
    }

    public void setInfoPanel(InfoPanel infoPanel) {
        this.infoPanel = infoPanel;
    }

    public InfoPanel getInfoPanel() {
        return infoPanel;
    }

    public void removeInfoPanel() {
        infoPanel = null;
    }

    public void setExceptionPanel(ExceptionPanel exceptionPanel) {
        this.exceptionPanel = exceptionPanel;
    }

    public ExceptionPanel getExceptionPanel() {
        return exceptionPanel;
    }

    public void removeExceptionPanel() {
        exceptionPanel = null;
        exceptionButton.setVisible(true);
    }

    public BaseButton getExceptionButton() {
        return exceptionButton;
    }

    ResizeButton getResizeButton() {
        if (resizeButton == null) {
            resizeButton = new ResizeButton();
            resizeButton.setVisible(false);
            contentGrid.setStyle("-fx-padding: 10 0 0 0");
            contentGrid.add(resizeButton, 2, 3);
        }
        return resizeButton;
    }

    public void addInputPorts(List<PortView> ports) {
        inPortBox.getChildren().addAll(ports);
    }

    public void addOutputPorts(List<PortView> ports) {
        outPortBox.getChildren().addAll(ports);
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

    public static Bounds getBoundingBoxOfBlocks(Collection<? extends BlockView> blocks) {
        System.out.println("getBoundingBoxOfBlocks");
        if (blocks == null || blocks.isEmpty()) {
            return null;
        }
        double minLeft = Double.MAX_VALUE;
        double minTop = Double.MAX_VALUE;
        double maxLeft = Double.MIN_VALUE;
        double maxTop = Double.MIN_VALUE;

        for (BlockView block : blocks) {
            if (block.getLayoutX() < minLeft) {
                minLeft = block.getLayoutX();
            }
            if (block.getLayoutY() < minTop) {
                minTop = block.getLayoutY();
            }

            if ((block.getLayoutX() + block.getWidth()) > maxLeft) {
                maxLeft = block.getLayoutX() + block.getWidth();
            }
            if ((block.getLayoutY() + block.getHeight()) > maxTop) {
                maxTop = block.getLayoutY() + block.getHeight();
            }
        }

        return new BoundingBox(minLeft, minTop, maxLeft - minLeft, maxTop - minTop);
    }

    public void setSelected(boolean isSelected) {
        if (isSelected) {
            contentGrid.getStyleClass().add("block-selected");
        } else {
            contentGrid.getStyleClass().clear();
            contentGrid.getStyleClass().add("block");
        }
    }

    public static Label getAwesomeIcon(IconType type) {
        Label label = new Label(type.getUnicode() + "");
        label.getStyleClass().add("block-awesome-icon");
        return label;
    }
}

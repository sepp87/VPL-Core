package vplcore.graph.model;

import java.beans.PropertyChangeEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import vplcore.workspace.input.MouseMode;
import vplcore.workspace.Workspace;

/**
 *
 * @author JoostMeulenkamp
 */
public class BlockGroup extends VplElement {

    public int id;

    private static int counter;
    public ObservableSet<Block> childBlocks;

    public BlockGroup(Workspace vplControl) {
        super(vplControl);

        getStyleClass().add("block-group");

        id = counter++;

        childBlocks = FXCollections.observableSet();
        setOnMousePressed(this::handle_MousePress);
        setOnMouseReleased(this::handle_MouseRelease);

        setName("Name group here...");

        workspace.blockGroupSet.add(this);
        workspace.getChildren().add(1, this);
    }

    public void setChildBlocks(ObservableSet<Block> blockSet) {
        childBlocks.addAll(blockSet);
        childBlocks.addListener(this::handle_CollectionChange);
        observeAllChildBlocks();
        calculateSize();
    }

    private void handle_MousePress(MouseEvent e) {
        for (Block block : childBlocks) {

            block.setOnMouseDragged(block::handle_MouseDrag);

            block.oldMousePosition = new Point2D(e.getSceneX(), e.getSceneY());

            block.setSelected(true);
            workspace.selectedBlockSet.add(block);
        }
        workspace.mouseMode = MouseMode.GROUP_SELECT;
    }

    private void handle_MouseRelease(MouseEvent e) {
        workspace.mouseMode = MouseMode.NOTHING;
        e.consume();
    }

    @Override
    public void binButton_MouseClick(MouseEvent e) {
        deleteGroup();
    }

    private void deleteGroup() {
        unObserveAllChildBlocks();
        workspace.blockGroupSet.remove(this);
        super.delete();
    }

    private void handle_CollectionChange(SetChangeListener.Change change) {

        if (change.wasAdded()) {
            Block block = (Block) change.getElementAdded();
            block.eventBlaster.add("deleted", this::block_DeletedInBlockSet);
            block.eventBlaster.add(this::block_PropertyChanged);
        } else {
            Block block = (Block) change.getElementRemoved();
            block.eventBlaster.remove("deleted", this::block_DeletedInBlockSet);
            block.eventBlaster.remove(this::block_PropertyChanged);
        }

        if (childBlocks.size() < 2) {
//            binButton_Click(null, null);
            deleteGroup();
        } else {
            calculateSize();
        }
    }

    private void observeAllChildBlocks() {
        for (Block block : childBlocks) {
            block.eventBlaster.add("deleted", this::block_DeletedInBlockSet);
            block.eventBlaster.add(this::block_PropertyChanged);
        }
    }

    private void unObserveAllChildBlocks() {
        for (Block block : childBlocks) {
            block.eventBlaster.remove("deleted", this::block_DeletedInBlockSet);
            block.eventBlaster.remove(this::block_PropertyChanged);
        }
    }

    private void block_DeletedInBlockSet(PropertyChangeEvent e) {
        Block block = (Block) e.getSource();
        if (block == null) {
            return;
        }
        childBlocks.remove(block);
    }

    private void block_PropertyChanged(PropertyChangeEvent e) {
        calculateSize();
    }

    private void calculateSize() {
        if (childBlocks.isEmpty()) {
            return;
        }

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for (Block block : childBlocks) {

            if (block.getLayoutX() < minX) {
                minX = block.getLayoutX();
            }
            if (block.getLayoutY() < minY) {
                minY = block.getLayoutY();
            }
            if ((block.getLayoutX() + block.getWidth()) > maxX) {
                maxX = block.getLayoutX() + block.getWidth();
            }
            if ((block.getLayoutY() + block.getHeight()) > maxY) {
                maxY = block.getLayoutY() + block.getHeight();
            }
        }

        relocate(minX, minY);
        setPrefSize(maxX - minX, maxY - minY);

//        OnPropertyChanged("BorderSize");
    }

    private void bindStyle() {
        //Block Passive Style
        Insets blockGroupBackgroundInsets = new Insets(4);
        CornerRadii blockGroupBackgroundRadius = new CornerRadii(8);
        Color blockGroupBackgroundColor = Color.web("#d35f5f");
        BackgroundFill blockGroupBackgroundFill = new BackgroundFill(
                blockGroupBackgroundColor,
                blockGroupBackgroundRadius,
                blockGroupBackgroundInsets);
        Background blockGroupBackground = new Background(blockGroupBackgroundFill);

        BorderWidths blockGroupBorderWidth = new BorderWidths(1);
        CornerRadii blockGroupBorderRadius = new CornerRadii(12);
        Color blockGroupBorderColor = Color.LIGHTGREY;
        BorderStroke blockBorderStroke = new BorderStroke(
                blockGroupBorderColor,
                BorderStrokeStyle.SOLID,
                blockGroupBorderRadius,
                blockGroupBorderWidth);
        Border blockBorder = new Border(blockBorderStroke);
        Insets blockPadding = new Insets(10);
    }
}

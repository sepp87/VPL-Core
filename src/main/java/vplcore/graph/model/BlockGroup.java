package vplcore.graph.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.SetChangeListener.Change;
import javafx.event.EventHandler;
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
import vplcore.workspace.Workspace;

/**
 *
 * @author JoostMeulenkamp
 */
public class BlockGroup extends VplElement {

    public int id;

    private static int counter;
    public ObservableSet<Block> childBlocks;

    private final EventHandler<MouseEvent> groupPressedHandler = this::handleGroupPressed;
    private final SetChangeListener<Block> groupSetChangedListener = this::handleGroupSetChanged;
    private final PropertyChangeListener groupBlockDeletedListener = this::handleGroupBlockDeleted;
    private final PropertyChangeListener groupBlockChangedListener = this::handleGroupBlockChanged; // is this listening to transforms e.g. move and resize? otherwise groupBlockTransformedListener

    public BlockGroup(Workspace vplControl) {
        super(vplControl);

        getStyleClass().add("block-group");

        id = counter++;

        childBlocks = FXCollections.observableSet();
        setOnMousePressed(groupPressedHandler);

        setName("Name group here...");

        workspace.blockGroupSet.add(this);
        workspace.getChildren().add(1, this);
    }

    public void setChildBlocks(ObservableSet<Block> blockSet) {
        childBlocks.addAll(blockSet);
        childBlocks.addListener(groupSetChangedListener);
        observeAllChildBlocks();
        calculateSize();
    }

    private void handleGroupPressed(MouseEvent event) {
        for (Block block : childBlocks) {

            block.setOnMouseDragged(block::moveBlock);

            block.oldMousePosition = new Point2D(event.getSceneX(), event.getSceneY());

            block.setSelected(true);
            workspace.selectedBlockSet.add(block);
        }
    }

    @Override
    public void delete() {
        unObserveAllChildBlocks();
        workspace.blockGroupSet.remove(this);
        super.delete();
    }

    private void handleGroupSetChanged(Change<? extends Block> change) {

        if (change.wasAdded()) {
            Block block = change.getElementAdded();
            block.eventBlaster.add("deleted", groupBlockDeletedListener);
            block.eventBlaster.add(groupBlockChangedListener);
        } else {
            Block block = change.getElementRemoved();
            block.eventBlaster.remove("deleted", groupBlockDeletedListener);
            block.eventBlaster.remove(groupBlockChangedListener);
        }

        if (childBlocks.size() < 2) {
            delete();
        } else {
            calculateSize();
        }
    }

    private void observeAllChildBlocks() {
        for (Block block : childBlocks) {
            block.eventBlaster.add("deleted", groupBlockDeletedListener);
            block.eventBlaster.add(groupBlockChangedListener);
        }
    }

    private void unObserveAllChildBlocks() {
        for (Block block : childBlocks) {
            block.eventBlaster.remove("deleted", groupBlockDeletedListener);
            block.eventBlaster.remove(groupBlockChangedListener);
        }
    }

    private void handleGroupBlockDeleted(PropertyChangeEvent event) {
        Block block = (Block) event.getSource();
        if (block == null) {
            return;
        }
        childBlocks.remove(block);
    }

    private void handleGroupBlockChanged(PropertyChangeEvent event) {
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

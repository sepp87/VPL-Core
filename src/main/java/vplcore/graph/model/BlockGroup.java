package vplcore.graph.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.SetChangeListener.Change;
import javafx.event.ActionEvent;
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
import jo.vpl.xml.BlockReferenceTag;
import jo.vpl.xml.GroupTag;
import jo.vpl.xml.ObjectFactory;
import vplcore.IconType;
import vplcore.context.EditorMode;
import static vplcore.graph.io.GraphSaver.getObjectFactory;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author JoostMeulenkamp
 */
public class BlockGroup extends VplElement {

    public int id;

    private static int counter;
    public ObservableSet<Block> childBlocks;

    public VplButton binButton;

    private final EventHandler<MouseEvent> groupPressedHandler = this::handleGroupPressed;
    private final EventHandler<MouseEvent> groupReleasedHandler = this::handleGroupReleased;
    private final SetChangeListener<Block> groupSetChangedListener = this::handleGroupSetChanged;
    private final PropertyChangeListener groupBlockDeletedListener = this::handleGroupBlockDeleted;
    private final PropertyChangeListener groupBlockChangedListener = this::handleGroupBlockChanged; // is this listening to transforms e.g. move and resize? otherwise groupBlockTransformedListener
    private final EventHandler<ActionEvent> binButtonClickedHandler = this::handleBinButtonClicked;

    public BlockGroup(WorkspaceController workspaceController) {
        super(workspaceController);

        getStyleClass().add("block-group");

        id = counter++;

        childBlocks = FXCollections.observableSet();
        setOnMousePressed(groupPressedHandler);
        setOnMouseReleased(groupReleasedHandler);

        setName("Name group here...");

        binButton = new VplButton(IconType.FA_MINUS_CIRCLE);
        binButton.setVisible(false);
        menuBox.getChildren().addAll( binButton);
        binButton.setOnAction(binButtonClickedHandler);
    }

    public void handleBinButtonClicked(ActionEvent event) {
        delete();
    }

    public void handleVplElementEntered(MouseEvent event) {
        super.handleVplElementEntered(event);
        binButton.setVisible(true);
    }

    public void handleVplElementExited(MouseEvent event) {
        super.handleVplElementExited(event);
        binButton.setVisible(false);
    }

    public void setChildBlocks(Collection<Block> blockSet) {
        childBlocks.addAll(blockSet);
        childBlocks.addListener(groupSetChangedListener);
        observeAllChildBlocks();
        calculateSize();
    }

    private void handleGroupPressed(MouseEvent event) {
        for (Block block : childBlocks) {

            block.oldMousePosition = new Point2D(event.getSceneX(), event.getSceneY());

            block.setSelected(true);
            workspaceController.blocksSelectedOnWorkspace.add(block);
        }
//        workspaceController.setEditorMode(EditorMode.GROUP_SELECTION_MODE); // prevent group from being deselected
        workspaceController.setSelectingBlockGroup(); // prevent group from being deselected
    }

    private void handleGroupReleased(MouseEvent event) {
        workspaceController.setIdle();
//        workspaceController.setEditorMode(EditorMode.IDLE_MODE);
//        event.consume();
    }

    @Override
    public void delete() {
        unObserveAllChildBlocks();
        workspaceController.groupsOfBlocks.remove(this);
        binButton.setOnAction(null);
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

    public void serialize(GroupTag xmlTag) {
        ObjectFactory factory = getObjectFactory();
        xmlTag.setName(getName());
        for (Block block : childBlocks) {
            BlockReferenceTag blockReferenceTag = factory.createBlockReferenceTag();
            blockReferenceTag.setUUID(block.uuid.toString());
            xmlTag.getBlockReference().add(blockReferenceTag);
        }
    }

    public void deserialize(GroupTag xmlTag) {
        setName(xmlTag.getName());
        List<BlockReferenceTag> blockReferenceTagList = xmlTag.getBlockReference();
        List<Block> blocks = new ArrayList<>();
        for (BlockReferenceTag blockReferenceTag : blockReferenceTagList) {
            for (Block block : workspaceController.blocksOnWorkspace) {
                if (block.uuid.toString().equals(blockReferenceTag.getUUID())) {
                    blocks.add(block);
                    break;
                }
            }
        }
        setChildBlocks(blocks);
    }
}

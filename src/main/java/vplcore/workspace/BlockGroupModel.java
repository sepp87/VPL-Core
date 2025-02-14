package vplcore.workspace;

import vplcore.graph.model.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

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
import vplcore.App;
import vplcore.IconType;
import vplcore.context.ActionManager;
import vplcore.context.command.RemoveGroupCommand;
import static vplcore.graph.io.GraphSaver.getObjectFactory;

/**
 *
 * @author JoostMeulenkamp
 */
public class BlockGroupModel extends VplElement {

    private ActionManager actionManager;

    public int id;

    private static int counter;
    public ObservableSet<BlockModel> childBlocks;

    public VplButton binButton;

    private final WorkspaceModel workspaceModel;

    private final EventHandler<MouseEvent> groupPressedHandler = this::handleGroupPressed;
    private final EventHandler<MouseEvent> groupReleasedHandler = this::handleGroupReleased;
    private final SetChangeListener<BlockModel> groupSetChangedListener = this::handleGroupSetChanged;
    private final ChangeListener<Object> blockRemovedListener = this::onBlockRemoved;
    private final ChangeListener<Object> blockTransformedListener = this::onBlockTransformed; // is this listening to transforms e.g. move and resize? otherwise groupBlockTransformedListener

    private final EventHandler<ActionEvent> binButtonClickedHandler = this::handleBinButtonClicked;

    public BlockGroupModel(String contextId, WorkspaceController workspaceController, WorkspaceModel workspaceModel) {
        super(workspaceController);
        this.workspaceModel = workspaceModel;
        actionManager = App.getContext(contextId).getActionManager();

        getStyleClass().add("block-group");

        id = counter++;

        childBlocks = FXCollections.observableSet();
        setOnMousePressed(groupPressedHandler);
        setOnMouseReleased(groupReleasedHandler);

        setName("Name group here...");

        binButton = new VplButton(IconType.FA_MINUS_CIRCLE);
        binButton.setVisible(false);
        menuBox.getChildren().addAll(binButton);
        binButton.setOnAction(binButtonClickedHandler);
    }

    public void handleBinButtonClicked(ActionEvent event) {
        RemoveGroupCommand command = new RemoveGroupCommand(actionManager.getWorkspaceModel(), this);
        actionManager.executeCommand(command);
    }

    public void handleVplElementEntered(MouseEvent event) {
        super.handleVplElementEntered(event);
        binButton.setVisible(true);
    }

    public void handleVplElementExited(MouseEvent event) {
        super.handleVplElementExited(event);
        binButton.setVisible(false);
    }

    public void setChildBlocks(Collection<BlockModel> blockSet) {
        childBlocks.addAll(blockSet);
        childBlocks.addListener(groupSetChangedListener);
        observeAllChildBlocks();
        calculateSize();
    }

    private void handleGroupPressed(MouseEvent event) {
        for (BlockModel block : childBlocks) {

            BlockController blockController = workspaceController.getBlockController(block);
            blockController.startPoint = new Point2D(event.getSceneX(), event.getSceneY());

            workspaceController.selectBlock(block);
        }
        workspaceController.setSelectingBlockGroup(); // prevent group from being deselected
    }

    private void handleGroupReleased(MouseEvent event) {
        workspaceController.setIdle();
//        event.consume();
    }

    @Override
    public void delete() {
        unObserveAllChildBlocks();
        binButton.setOnAction(null);
        super.delete();
    }

    private void handleGroupSetChanged(Change<? extends BlockModel> change) {

        if (change.wasAdded()) {
            BlockModel block = change.getElementAdded();
            addListeners(block);
        } else {
            BlockModel block = change.getElementRemoved();
            removeListeners(block);
        }

        if (childBlocks.size() < 2) {
            delete();
        } else {
            calculateSize();
        }
    }

    private void observeAllChildBlocks() {
        for (BlockModel block : childBlocks) {
            addListeners(block);
        }
    }

    private void unObserveAllChildBlocks() {
        for (BlockModel block : childBlocks) {
            removeListeners(block);
        }
    }

    private void addListeners(BlockModel block) {
        block.removedProperty().addListener(blockRemovedListener);
        block.layoutXProperty().addListener(blockTransformedListener);
        block.layoutYProperty().addListener(blockTransformedListener);
        block.widthProperty().addListener(blockTransformedListener);
        block.heightProperty().addListener(blockTransformedListener);
    }

    private void removeListeners(BlockModel block) {
        block.removedProperty().removeListener(blockRemovedListener);
        block.layoutXProperty().removeListener(blockTransformedListener);
        block.layoutYProperty().removeListener(blockTransformedListener);
        block.widthProperty().removeListener(blockTransformedListener);
        block.heightProperty().removeListener(blockTransformedListener);
    }

    private void onBlockRemoved(ObservableValue b, Object o, Object n) {
        BlockModel block = (BlockModel) b;
        if (block == null) {
            return;
        }
        childBlocks.remove(block);
    }

//    private void handleGroupBlockDeleted(PropertyChangeEvent event) {
//        Block block = (Block) event.getSource();
//        if (block == null) {
//            return;
//        }
//        childBlocks.remove(block);
//    }
    private void onBlockTransformed(ObservableValue b, Object o, Object n) {

        // TODO optimize here so only the changed block model is used the re-calculate the size
        calculateSize();
    }

//    private void handleGroupBlockChanged(PropertyChangeEvent event) {
//        calculateSize();
//    }
    private void calculateSize() {
        if (childBlocks.isEmpty()) {
            return;
        }

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for (BlockModel block : childBlocks) {
            BlockView blockView = workspaceController.getBlockController(block).getView();
            
            if (block.layoutXProperty().get() < minX) {
                minX = block.layoutXProperty().get();
            }
            if (block.layoutYProperty().get() < minY) {
                minY = block.layoutYProperty().get();
            }
            if ((block.layoutXProperty().get() + blockView.widthProperty().get()) > maxX) {
                maxX = block.layoutXProperty().get() + blockView.widthProperty().get();
            }
            if ((block.layoutYProperty().get() + blockView.heightProperty().get()) > maxY) {
                maxY = block.layoutYProperty().get() + blockView.heightProperty().get();
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
        for (BlockModel block : childBlocks) {
            BlockReferenceTag blockReferenceTag = factory.createBlockReferenceTag();
            blockReferenceTag.setUUID(block.idProperty().get());
            xmlTag.getBlockReference().add(blockReferenceTag);
        }
    }

    public void deserialize(GroupTag xmlTag) {
        setName(xmlTag.getName());
        List<BlockReferenceTag> blockReferenceTagList = xmlTag.getBlockReference();
        List<BlockModel> blocks = new ArrayList<>();
        for (BlockReferenceTag blockReferenceTag : blockReferenceTagList) {
            for (BlockModel block : workspaceModel.getBlockModels()) {
                if (block.idProperty().get().equals(blockReferenceTag.getUUID())) {
                    blocks.add(block);
                    break;
                }
            }
        }
        setChildBlocks(blocks);
    }
}

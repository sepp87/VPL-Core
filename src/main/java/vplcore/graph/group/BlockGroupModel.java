package vplcore.graph.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.collections.SetChangeListener.Change;
import jo.vpl.xml.BlockReferenceTag;
import jo.vpl.xml.GroupTag;
import jo.vpl.xml.ObjectFactory;
import vplcore.graph.block.BlockModel;
import vplcore.graph.block.BaseModel;
import vplcore.workspace.WorkspaceModel;
import static vplcore.graph.io.GraphSaver.getObjectFactory;

/**
 *
 * @author JoostMeulenkamp
 */
public class BlockGroupModel extends BaseModel {

    private int id;
    private static int counter;
    private final ObservableSet<BlockModel> blocks;
    private final WorkspaceModel workspaceModel;


    public BlockGroupModel(WorkspaceModel workspaceModel) {
        this.workspaceModel = workspaceModel;
        id = counter++;
        blocks = FXCollections.observableSet();
        nameProperty().set("Name group here...");
    }


    public void setBlocks(Collection<BlockModel> blockSet) {
        blocks.addAll(blockSet);
        blocks.addListener(blocksListener);
        observeAllChildBlocks();
    }
    
    public Collection<BlockModel> getBlocks() {
        return blocks;
    }

    @Override
    public void remove() {
        super.remove();
        unObserveAllChildBlocks();
    }

    private final SetChangeListener<BlockModel> blocksListener = this::onBlocksChanged;

    private void onBlocksChanged(Change<? extends BlockModel> change) {

        if (change.wasAdded()) {
            BlockModel block = change.getElementAdded();
            block.removedProperty().addListener(blockRemovedListener);
        } else {
            BlockModel block = change.getElementRemoved();
            block.removedProperty().removeListener(blockRemovedListener);
        }

        if (blocks.size() < 2) {
            remove();
        }
    }

    private void observeAllChildBlocks() {
        for (BlockModel block : blocks) {
            block.removedProperty().addListener(blockRemovedListener);

        }
    }

    private void unObserveAllChildBlocks() {
        for (BlockModel block : blocks) {
            block.removedProperty().removeListener(blockRemovedListener);
        }
    }

    private final ChangeListener<Object> blockRemovedListener = this::onBlockRemoved;

    private void onBlockRemoved(ObservableValue b, Object o, Object n) {
        BlockModel block = (BlockModel) b;
        if (block == null) {
            return;
        }
        System.out.println("BlockGroupModel.onBlockRemoved()");
        blocks.remove(block);
    }


    public void serialize(GroupTag xmlTag) {
        ObjectFactory factory = getObjectFactory();
        xmlTag.setName(nameProperty().get());
        for (BlockModel block : blocks) {
            BlockReferenceTag blockReferenceTag = factory.createBlockReferenceTag();
            blockReferenceTag.setUUID(block.idProperty().get());
            xmlTag.getBlockReference().add(blockReferenceTag);
        }
    }

    public void deserialize(GroupTag xmlTag) {
        nameProperty().set(xmlTag.getName());
        List<BlockReferenceTag> blockReferenceTagList = xmlTag.getBlockReference();
        List<BlockModel> list = new ArrayList<>();
        for (BlockReferenceTag blockReferenceTag : blockReferenceTagList) {
            for (BlockModel block : workspaceModel.getBlockModels()) {
                if (block.idProperty().get().equals(blockReferenceTag.getUUID())) {
                    list.add(block);
                    break;
                }
            }
        }
        setBlocks(list);
    }
}

package btscore.graph.group;

import java.util.Collection;
import java.util.HashSet;
import javafx.beans.property.ReadOnlySetProperty;
import javafx.beans.property.ReadOnlySetWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import btsxml.BlockReferenceTag;
import btsxml.GroupTag;
import btsxml.ObjectFactory;
import btscore.graph.block.BlockModel;
import btscore.graph.base.BaseModel;
import static btsxml.io.GraphSaver.getObjectFactory;
import btscore.workspace.BlockGroupIndex;

/**
 *
 * @author JoostMeulenkamp
 */
public class BlockGroupModel extends BaseModel {

    private final ObservableSet<BlockModel> internalBlocks = FXCollections.observableSet();
    private final ObservableSet<BlockModel> readonlyBlocks = FXCollections.unmodifiableObservableSet(internalBlocks);
    private final BlockGroupIndex blockGroupIndex;

    public BlockGroupModel(BlockGroupIndex blockGroupIndex) {
        this.blockGroupIndex = blockGroupIndex;

        nameProperty().set("Name group here...");
    }

    public void setBlocks(Collection<BlockModel> blocks) {
        for (BlockModel blockModel : blocks) {
            addBlock(blockModel);
        }
    }

    public void addBlock(BlockModel blockModel) {
        internalBlocks.add(blockModel);
        blockModel.groupedProperty().set(true);
        blockGroupIndex.register(blockModel, this);
    }

    public void removeBlock(BlockModel blockModel) {
        internalBlocks.remove(blockModel);
        blockModel.groupedProperty().set(false);
        blockGroupIndex.unregister(blockModel);
    }

    // return set as immutable
    public ObservableSet<BlockModel> getBlocks() {
        return readonlyBlocks;
    }

    @Override
    public void remove() {
        for (BlockModel blockModel : new HashSet<>(internalBlocks)) {
            removeBlock(blockModel);
        }
        super.remove();
    }

    public void serialize(GroupTag xmlTag) {
        ObjectFactory factory = getObjectFactory();
        xmlTag.setName(nameProperty().get());
        for (BlockModel block : internalBlocks) {
            BlockReferenceTag blockReferenceTag = factory.createBlockReferenceTag();
            blockReferenceTag.setUUID(block.idProperty().get());
            xmlTag.getBlockReference().add(blockReferenceTag);
        }
    }

    public void deserialize(GroupTag xmlTag) {

    }
}

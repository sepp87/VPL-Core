package vplcore.graph.group;

import java.util.Collection;
import java.util.HashSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import jo.vpl.xml.BlockReferenceTag;
import jo.vpl.xml.GroupTag;
import jo.vpl.xml.ObjectFactory;
import vplcore.graph.block.BlockModel;
import vplcore.graph.base.BaseModel;
import static vplcore.graph.io.GraphSaver.getObjectFactory;
import vplcore.workspace.BlockGroupIndex;

/**
 *
 * @author JoostMeulenkamp
 */
public class BlockGroupModel extends BaseModel {

    private final ObservableSet<BlockModel> blocks;
    private final BlockGroupIndex blockGroupIndex;

    public BlockGroupModel(BlockGroupIndex blockGroupIndex) {
        this.blockGroupIndex = blockGroupIndex;
        blocks = FXCollections.observableSet();
        nameProperty().set("Name group here...");
    }

    public void setBlocks(Collection<BlockModel> blocks) {
        for (BlockModel blockModel : blocks) {
            addBlock(blockModel);
        }
    }

    public void addBlock(BlockModel blockModel) {
        blocks.add(blockModel);
        blockModel.groupedProperty().set(true);
        blockGroupIndex.register(blockModel, this);
    }

    public void removeBlock(BlockModel blockModel) {
        blocks.remove(blockModel);
        blockModel.groupedProperty().set(false);
        blockGroupIndex.unregister(blockModel);
    }

    // return set as immutable
    public ObservableSet<BlockModel> getBlocks() {
        return blocks;
    }

    @Override
    public void remove() {
        for (BlockModel blockModel : new HashSet<>(blocks)) {
            removeBlock(blockModel);
        }
        super.remove();
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

    }
}

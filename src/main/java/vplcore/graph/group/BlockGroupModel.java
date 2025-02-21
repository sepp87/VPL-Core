package vplcore.graph.group;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    private final ObservableSet<BlockModel> children;
    private final WorkspaceModel workspaceModel;
    
    public BlockGroupModel(WorkspaceModel workspaceModel) {
        this.workspaceModel = workspaceModel;
        id = counter++;
        children = FXCollections.observableSet();
        nameProperty().set("Name group here...");
    }
    
    public void setBlocks(Collection<BlockModel> blocks) {
        for (BlockModel blockModel : blocks) {
            addBlock(blockModel);
        }
    }
    
    public void addBlock(BlockModel blockModel) {
        children.add(blockModel);
        blockModel.groupedProperty().set(true);
    }
    
    public void removeBlock(BlockModel blockModel) {
        children.remove(blockModel);
        blockModel.groupedProperty().set(false);
    }

    // return set as immutable
    public ObservableSet<BlockModel> getBlocks() {
        return children;
    }
    
    @Override
    public void remove() {
        for (BlockModel blockModel : children) {
            blockModel.groupedProperty().set(false);
        }
        children.clear();
        super.remove();
    }
    
    public void serialize(GroupTag xmlTag) {
        ObjectFactory factory = getObjectFactory();
        xmlTag.setName(nameProperty().get());
        for (BlockModel block : children) {
            BlockReferenceTag blockReferenceTag = factory.createBlockReferenceTag();
            blockReferenceTag.setUUID(block.idProperty().get());
            xmlTag.getBlockReference().add(blockReferenceTag);
        }
    }
    
    public void deserialize(GroupTag xmlTag) {
//        nameProperty().set(xmlTag.getName());
//        List<BlockReferenceTag> blockReferenceTagList = xmlTag.getBlockReference();
//        List<BlockModel> list = new ArrayList<>();
//        for (BlockReferenceTag blockReferenceTag : blockReferenceTagList) {
//            for (BlockModel block : workspaceModel.getBlockModels()) {
//                if (block.idProperty().get().equals(blockReferenceTag.getUUID())) {
//                    list.add(block);
//                    break;
//                }
//            }
//        }
//        setBlocks(list);
    }
}

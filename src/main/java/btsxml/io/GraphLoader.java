package btsxml.io;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import btsxml.*;
import btscore.workspace.WorkspaceModel;
import btscore.graph.block.BlockFactory;
import btscore.graph.group.BlockGroupModel;
import btscore.graph.block.BlockModel;
import btscore.graph.port.PortModel;

/**
 *
 * @author joostmeulenkamp
 */
public class GraphLoader {

    public static void deserialize(File file,  WorkspaceModel workspaceModel) {
        try {
            JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            JAXBElement<?> document = (JAXBElement) unmarshaller.unmarshal(file);
            DocumentTag documentTag = (DocumentTag) document.getValue();

            // deserialize workspace and settings
            workspaceModel.setZoomFactor(documentTag.getScale());
            workspaceModel.translateXProperty().set(documentTag.getTranslateX());
            workspaceModel.translateYProperty().set(documentTag.getTranslateY());

            // deserialize blocks of graph
            BlocksTag blocksTag = documentTag.getBlocks();
            deserializeBlocks(blocksTag, workspaceModel);

            // deserialize connections of graph
            ConnectionsTag connectionsTag = documentTag.getConnections();
            deserializeConnections(connectionsTag, workspaceModel);

            // deserialize groups of graph
            GroupsTag groups = documentTag.getGroups();
            deserializeGroups(groups, workspaceModel);

            // set file reference for quick save
            workspaceModel.fileProperty().set(file);

        } catch (JAXBException | SecurityException | IllegalArgumentException ex) {
            Logger.getLogger(GraphLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void deserializeBlocks(BlocksTag blocksTag, WorkspaceModel workspaceModel) {
        List<BlockTag> blockTagList = blocksTag.getBlock();
        if (blockTagList == null) {
            return;
        }

        for (BlockTag blockTag : blockTagList) {

            String blockIdentifier = blockTag.getType();

//            BlockModel blockModel = BlockFactory.createBlock(blockIdentifier, workspaceModel);
            BlockModel blockModel = BlockFactory.createBlock(blockIdentifier);
            if (blockModel == null) {
                System.out.println("WARNING: Could not instantiate block type " + blockIdentifier);
                return;
            }
            blockModel.deserialize(blockTag);
            workspaceModel.addBlockModel(blockModel);

        }
    }

    private static void deserializeConnections(ConnectionsTag connectionsTag, WorkspaceModel workspaceModel) {
        List<ConnectionTag> connectionTagList = connectionsTag.getConnection();
        if (connectionTagList == null) {
            return;
        }

        for (ConnectionTag connectionTag : connectionTagList) {

            String startBlockUuid = connectionTag.getStartBlock();
            int startPortIndex = connectionTag.getStartIndex();
            String endBlockUuid = connectionTag.getEndBlock();
            int endPortIndex = connectionTag.getEndIndex();

            BlockModel startBlock = null;
            BlockModel endBlock = null;
            for (BlockModel blockModel : workspaceModel.getBlockModels()) {
                if (blockModel.idProperty().get().compareTo(startBlockUuid) == 0) {
                    startBlock = blockModel;
                } else if (blockModel.idProperty().get().compareTo(endBlockUuid) == 0) {
                    endBlock = blockModel;
                }
            }

            if (startBlock != null && endBlock != null) {
                PortModel startPort = startBlock.getOutputPorts().get(startPortIndex);
                PortModel endPort = endBlock.getInputPorts().get(endPortIndex);
                workspaceModel.addConnectionModel(startPort, endPort);
            }

        }
    }

    private static void deserializeGroups(GroupsTag groupsTag, WorkspaceModel workspaceModel) {
        if (groupsTag == null) {
            return;
        }

        List<GroupTag> groupTagList = groupsTag.getGroup();
        if (groupTagList == null) {
            return;
        }

        for (GroupTag groupTag : groupTagList) {
//            BlockGroupModel group = new BlockGroupModel(workspaceController.getContextId(), workspaceController, workspaceModel);
//            BlockGroupModel group = new BlockGroupModel(workspaceModel);
//            group.deserialize(groupTag);
//            workspaceModel.addBlockGroupModel(group);

//            nameProperty().set(xmlTag.getName());
//            List<BlockReferenceTag> blockReferenceTagList = xmlTag.getBlockReference();
//            List<BlockModel> list = new ArrayList<>();
//            for (BlockReferenceTag blockReferenceTag : blockReferenceTagList) {
//                for (BlockModel block : workspaceModel.getBlockModels()) {
//                    if (block.idProperty().get().equals(blockReferenceTag.getUUID())) {
//                        list.add(block);
//                        break;
//                    }
//                }
//            }
//            setBlocks(list);
            BlockGroupModel group = new BlockGroupModel(workspaceModel.getBlockGroupIndex());
            group.nameProperty().set(groupTag.getName());
            List<BlockReferenceTag> blockReferenceTagList = groupTag.getBlockReference();
            List<BlockModel> list = new ArrayList<>();
            for (BlockReferenceTag blockReferenceTag : blockReferenceTagList) {
                for (BlockModel block : workspaceModel.getBlockModels()) {
                    if (block.idProperty().get().equals(blockReferenceTag.getUUID())) {
                        list.add(block);
                        break;
                    }
                }
            }
            group.setBlocks(list); // blocks should be set beforehand, because otherwise WorkspaceBlockGroupHelper.addBlockGroupModel() does NOT index grouped blocks
            workspaceModel.addBlockGroupModel(group);
        }
    }

}

package vplcore.graph.io;

import vplcore.graph.model.Block;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import jo.vpl.xml.ConnectionTag;
import jo.vpl.xml.ConnectionsTag;
import jo.vpl.xml.DocumentTag;
import jo.vpl.xml.BlockTag;
import jo.vpl.xml.BlocksTag;
import jo.vpl.xml.GroupTag;
import jo.vpl.xml.GroupsTag;
import jo.vpl.xml.ObjectFactory;
import vplcore.workspace.WorkspaceModel;
import vplcore.graph.model.BlockGroup;
import vplcore.graph.model.Port;
import vplcore.graph.util.BlockFactory;
import vplcore.graph.util.BlockModelFactory;
import vplcore.workspace.BlockGroupModel;
import vplcore.workspace.BlockModel;
import vplcore.workspace.PortModel;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author joostmeulenkamp
 */
public class GraphLoader {

    public static void deserialize(File file, WorkspaceController workspaceController, WorkspaceModel workspaceModel) {
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
            deserializeBlocks(blocksTag, workspaceController, workspaceModel);

            // deserialize connections of graph
            ConnectionsTag connectionsTag = documentTag.getConnections();
            deserializeConnections(connectionsTag, workspaceController, workspaceModel);

            // deserialize groups of graph
            GroupsTag groups = documentTag.getGroups();
            deserializeGroups(groups, workspaceController, workspaceModel);

        } catch (JAXBException | SecurityException | IllegalArgumentException ex) {
            Logger.getLogger(GraphLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void deserializeBlocks(BlocksTag blocksTag, WorkspaceController workspaceController, WorkspaceModel workspaceModel) {
        List<BlockTag> blockTagList = blocksTag.getBlock();
        if (blockTagList == null) {
            return;
        }

        for (BlockTag blockTag : blockTagList) {

            String blockIdentifier = blockTag.getType();

            if (vplcore.App.BLOCK_MVC) {
                BlockModel blockModel = BlockModelFactory.createBlock(blockIdentifier, workspaceModel);
                if (blockModel == null) {
                    System.out.println("WARNING: Could not instantiate block type " + blockIdentifier);
                    return;
                }
                blockModel.deserialize(blockTag);
                workspaceModel.addBlockModel(blockModel);
            } else {
                Block block = BlockFactory.createBlock(blockIdentifier, workspaceController);
                if (block == null) {
                    System.out.println("WARNING: Could not instantiate block type " + blockIdentifier);
                    return;
                }
                block.deserialize(blockTag);
                workspaceController.addBlock(block);
            }
        }
    }

    private static void deserializeConnections(ConnectionsTag connectionsTag, WorkspaceController workspaceController, WorkspaceModel workspaceModel) {
        List<ConnectionTag> connectionTagList = connectionsTag.getConnection();
        if (connectionTagList == null) {
            return;
        }

        for (ConnectionTag connectionTag : connectionTagList) {

            if (vplcore.App.BLOCK_MVC) {
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
            } else {
                UUID startBlockUuid = UUID.fromString(connectionTag.getStartBlock());
                int startPortIndex = connectionTag.getStartIndex();
                UUID endBlockUuid = UUID.fromString(connectionTag.getEndBlock());
                int endPortIndex = connectionTag.getEndIndex();
                Block startBlock = null;
                Block endBlock = null;
                for (Block block : workspaceController.getBlocks()) {
                    if (block.uuid.compareTo(startBlockUuid) == 0) {
                        startBlock = block;
                    } else if (block.uuid.compareTo(endBlockUuid) == 0) {
                        endBlock = block;
                    }
                }

                if (startBlock != null && endBlock != null) {
                    Port startPort = startBlock.outPorts.get(startPortIndex);
                    Port endPort = endBlock.inPorts.get(endPortIndex);
                    workspaceController.addConnection(startPort, endPort);
                }
            }
        }
    }

    private static void deserializeGroups(GroupsTag groupsTag, WorkspaceController workspaceController, WorkspaceModel workspaceModel) {
        if (groupsTag == null) {
            return;
        }

        List<GroupTag> groupTagList = groupsTag.getGroup();
        if (groupTagList == null) {
            return;
        }

        for (GroupTag groupTag : groupTagList) {
            if (vplcore.App.BLOCK_MVC) {
                BlockGroupModel group = new BlockGroupModel(workspaceController.getContextId(), workspaceController, workspaceModel);
                group.deserialize(groupTag);
            } else {
                BlockGroup group = new BlockGroup(workspaceController);
                group.deserialize(groupTag);
            }
        }
    }

}

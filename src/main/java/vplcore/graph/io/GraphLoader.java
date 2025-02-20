package vplcore.graph.io;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.util.List;
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
import vplcore.graph.util.BlockModelFactory;
import vplcore.graph.group.BlockGroupModel;
import vplcore.graph.block.BlockModel;
import vplcore.graph.port.PortModel;
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

            BlockModel blockModel = BlockModelFactory.createBlock(blockIdentifier, workspaceModel);
            if (blockModel == null) {
                System.out.println("WARNING: Could not instantiate block type " + blockIdentifier);
                return;
            }
            blockModel.deserialize(blockTag);
            workspaceModel.addBlockModel(blockModel);

        }
    }

    private static void deserializeConnections(ConnectionsTag connectionsTag, WorkspaceController workspaceController, WorkspaceModel workspaceModel) {
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

    private static void deserializeGroups(GroupsTag groupsTag, WorkspaceController workspaceController, WorkspaceModel workspaceModel) {
        if (groupsTag == null) {
            return;
        }

        List<GroupTag> groupTagList = groupsTag.getGroup();
        if (groupTagList == null) {
            return;
        }

        for (GroupTag groupTag : groupTagList) {
//            BlockGroupModel group = new BlockGroupModel(workspaceController.getContextId(), workspaceController, workspaceModel);
            BlockGroupModel group = new BlockGroupModel(workspaceModel);
            group.deserialize(groupTag);
            workspaceModel.addBlockGroupModel(group);
        }
    }

}

package vplcore.graph.io;

import vplcore.graph.model.Block;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.File;
import java.util.Collection;
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
import vplcore.graph.model.Connection;
import vplcore.workspace.BlockGroupModel;
import vplcore.workspace.BlockModel;
import vplcore.workspace.ConnectionModel;
import vplcore.workspace.WorkspaceController;

/**
 *
 * @author joostmeulenkamp
 */
public class GraphSaver {

    private static ObjectFactory objectFactory;

    public static ObjectFactory getObjectFactory() {
        if (objectFactory == null) {
            objectFactory = new ObjectFactory();
        }
        return objectFactory;
    }

    public static void serialize(File file, WorkspaceController workspaceController, WorkspaceModel workspaceModel) {
        try {

            ObjectFactory factory = getObjectFactory();

            // serialize workspace and settings
            DocumentTag documentTag = factory.createDocumentTag();
            documentTag.setScale(workspaceModel.zoomFactorProperty().get());
            documentTag.setTranslateX(workspaceModel.translateXProperty().get());
            documentTag.setTranslateY(workspaceModel.translateYProperty().get());

            // serialize blocks of graph
            if (vplcore.App.BLOCK_MVC) {
                Collection<BlockModel> blocks = workspaceModel.getBlockModels();
                BlocksTag blocksTag = serializeBlockModels(blocks);
                documentTag.setBlocks(blocksTag);
            } else {
                Collection<Block> blocks = workspaceController.getBlocks();
                BlocksTag blocksTag = serializeBlocks(blocks);
                documentTag.setBlocks(blocksTag);
            }

            // serialize connections of graph
            if (vplcore.App.BLOCK_MVC) {
                Collection<ConnectionModel> connections = workspaceModel.getConnectionModels();
                ConnectionsTag connectionsTag = serializeConnnectionModels(connections);
                documentTag.setConnections(connectionsTag);
            } else {
                Collection<Connection> connections = workspaceController.getConnections();
                ConnectionsTag connectionsTag = serializeConnnections(connections);
                documentTag.setConnections(connectionsTag);
            }

            // serialize groups of graph
            if (vplcore.App.BLOCK_MVC) {
                Collection<BlockGroupModel> groups = workspaceModel.getBlockGroupModels();
                if (!groups.isEmpty()) {
                    GroupsTag groupsTag = serializeGroupModels(groups);
                    documentTag.setGroups(groupsTag);
                }
            } else {
                Collection<BlockGroup> groups = workspaceController.getBlockGroups();
                if (!groups.isEmpty()) {
                    GroupsTag groupsTag = serializeGroups(groups);
                    documentTag.setGroups(groupsTag);
                }
            }

            // serialize the conplete document and save to file
            JAXBElement<DocumentTag> document = factory.createDocument(documentTag);
            JAXBContext context = JAXBContext.newInstance("jo.vpl.xml");
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(document, file);

        } catch (JAXBException ex) {
            Logger.getLogger(GraphSaver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static BlocksTag serializeBlockModels(Collection<BlockModel> blocks) {
        ObjectFactory factory = getObjectFactory();
        BlocksTag blocksTag = factory.createBlocksTag();
        for (BlockModel block : blocks) {
            BlockTag blockTag = factory.createBlockTag();
            block.serialize(blockTag);
            blocksTag.getBlock().add(blockTag);
        }
        return blocksTag;
    }

    private static ConnectionsTag serializeConnnectionModels(Collection<ConnectionModel> connections) {
        ObjectFactory factory = getObjectFactory();
        ConnectionsTag connectionsTag = factory.createConnectionsTag();
        for (ConnectionModel connection : connections) {
            ConnectionTag connectionTag = factory.createConnectionTag();
            connection.serialize(connectionTag);
            connectionsTag.getConnection().add(connectionTag);
        }
        return connectionsTag;
    }

    private static GroupsTag serializeGroupModels(Collection<BlockGroupModel> groups) {
        ObjectFactory factory = getObjectFactory();
        GroupsTag groupsTag = factory.createGroupsTag();
        for (BlockGroupModel group : groups) {
            GroupTag groupTag = factory.createGroupTag();
            group.serialize(groupTag);
            groupsTag.getGroup().add(groupTag);
        }
        return groupsTag;
    }

    private static BlocksTag serializeBlocks(Collection<Block> blocks) {
        ObjectFactory factory = getObjectFactory();
        BlocksTag blocksTag = factory.createBlocksTag();
        for (Block block : blocks) {
            BlockTag blockTag = factory.createBlockTag();
            block.serialize(blockTag);
            blocksTag.getBlock().add(blockTag);
        }
        return blocksTag;
    }

    private static ConnectionsTag serializeConnnections(Collection<Connection> connections) {
        ObjectFactory factory = getObjectFactory();
        ConnectionsTag connectionsTag = factory.createConnectionsTag();
        for (Connection connection : connections) {
            ConnectionTag connectionTag = factory.createConnectionTag();
            connection.serialize(connectionTag);
            connectionsTag.getConnection().add(connectionTag);
        }
        return connectionsTag;
    }

    private static GroupsTag serializeGroups(Collection<BlockGroup> groups) {
        ObjectFactory factory = getObjectFactory();
        GroupsTag groupsTag = factory.createGroupsTag();
        for (BlockGroup group : groups) {
            GroupTag groupTag = factory.createGroupTag();
            group.serialize(groupTag);
            groupsTag.getGroup().add(groupTag);
        }
        return groupsTag;
    }

}

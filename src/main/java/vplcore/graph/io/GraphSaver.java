package vplcore.graph.io;

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
import vplcore.graph.group.BlockGroupModel;
import vplcore.graph.block.BlockModel;
import vplcore.graph.connection.ConnectionModel;
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
            Collection<BlockModel> blocks = workspaceModel.getBlockModels();
            BlocksTag blocksTag = serializeBlockModels(blocks);
            documentTag.setBlocks(blocksTag);

            // serialize connections of graph
            Collection<ConnectionModel> connections = workspaceModel.getConnectionModels();
            ConnectionsTag connectionsTag = serializeConnnectionModels(connections);
            documentTag.setConnections(connectionsTag);

            // serialize groups of graph
            Collection<BlockGroupModel> groups = workspaceModel.getBlockGroupModels();
            if (!groups.isEmpty()) {
                GroupsTag groupsTag = serializeGroupModels(groups);
                documentTag.setGroups(groupsTag);
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

}

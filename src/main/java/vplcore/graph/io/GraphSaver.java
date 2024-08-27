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
import jo.vpl.xml.ObjectFactory;
import vplcore.graph.model.Connection;
import vplcore.workspace.Workspace;

/**
 *
 * @author joostmeulenkamp
 */
public class GraphSaver {

    public static void serialize(File file, Workspace workspace) {
        try {

            ObjectFactory factory = new ObjectFactory();

            // serialize workspace and settings
            DocumentTag documentTag = factory.createDocumentTag();
            documentTag.setScale(workspace.getScale());
            documentTag.setTranslateX(workspace.getTranslateX());
            documentTag.setTranslateY(workspace.getTranslateY());

            // serialize blocks of graph
            Collection<Block> blocks = workspace.blockSet;
            BlocksTag blocksTag = serializeBlocks(blocks, factory);
            documentTag.setBlocks(blocksTag);

            // serialize connections of graph
            Collection<Connection> connections = workspace.connectionSet;
            ConnectionsTag connectionsTag = serializeConnnections(connections, factory);
            documentTag.setConnections(connectionsTag);

            JAXBElement<DocumentTag> document = factory.createDocument(documentTag);
            JAXBContext context = JAXBContext.newInstance("jo.vpl.xml");
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(document, file);

        } catch (JAXBException ex) {
            Logger.getLogger(GraphSaver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static BlocksTag serializeBlocks(Collection<Block> blocks, ObjectFactory factory) {
        BlocksTag blocksTag = factory.createBlocksTag();
        for (Block block : blocks) {
            BlockTag blockTag = factory.createBlockTag();
            block.serialize(blockTag);
            blocksTag.getBlock().add(blockTag);
        }
        return blocksTag;
    }

    private static ConnectionsTag serializeConnnections(Collection<Connection> connections, ObjectFactory factory) {
        ConnectionsTag connectionsTag = factory.createConnectionsTag();
        for (Connection connection : connections) {
            ConnectionTag connectionTag = factory.createConnectionTag();
            connection.serialize(connectionTag);
            connectionsTag.getConnection().add(connectionTag);
        }
        return connectionsTag;
    }

}

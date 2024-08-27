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
import jo.vpl.xml.ObjectFactory;
import vplcore.graph.model.Connection;
import vplcore.graph.model.Port;
import vplcore.graph.util.BlockFactory;
import vplcore.workspace.Workspace;

/**
 *
 * @author joostmeulenkamp
 */
public class GraphLoader {

    public static void deserialize(File file, Workspace workspace) {
        try {
            JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            JAXBElement<?> document = (JAXBElement) unmarshaller.unmarshal(file);
            DocumentTag documentTag = (DocumentTag) document.getValue();

            // deserialize workspace and settings
            workspace.setScale(documentTag.getScale());
            workspace.setTranslateX(documentTag.getTranslateX());
            workspace.setTranslateY(documentTag.getTranslateY());

            // deserialize blocks of graph
            BlocksTag blocksTag = documentTag.getBlocks();
            deserializeBlocks(blocksTag, workspace);

            // deserialize connections of graph
            ConnectionsTag connectionsTag = documentTag.getConnections();
            deserializeConnections(connectionsTag, workspace);
            
        } catch (JAXBException | SecurityException | IllegalArgumentException ex) {
            Logger.getLogger(GraphLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void deserializeBlocks(BlocksTag blocksTag, Workspace workspace) {
        List<BlockTag> blockTagList = blocksTag.getBlock();
        if (blockTagList != null) {

            for (BlockTag blockTag : blockTagList) {

                String blockIdentifier = blockTag.getType();
                Block block = BlockFactory.createBlock(blockIdentifier, workspace);

                if (block == null) {
                    System.out.println("WARNING: Could not instantiate block type " + blockIdentifier);
                    return;
                }

                block.deserialize(blockTag);
                workspace.blockSet.add(block);
                workspace.getChildren().add(block);
            }
        }
    }

    private static void deserializeConnections(ConnectionsTag connectionsTag, Workspace workspace) {
        List<ConnectionTag> connectionTagList = connectionsTag.getConnection();
        if (connectionTagList
                != null) {
            for (ConnectionTag connectionTag : connectionTagList) {

                UUID startBlockUuid = UUID.fromString(connectionTag.getStartBlock());
                int startPortIndex = connectionTag.getStartIndex();
                UUID endBlockUuid = UUID.fromString(connectionTag.getEndBlock());
                int endPortIndex = connectionTag.getEndIndex();

                Block startBlock = null;
                Block endBlock = null;
                for (Block Block : workspace.blockSet) {
                    if (Block.uuid.compareTo(startBlockUuid) == 0) {
                        startBlock = Block;
                    } else if (Block.uuid.compareTo(endBlockUuid) == 0) {
                        endBlock = Block;
                    }
                }

                if (startBlock != null && endBlock != null) {
                    Port startPort = startBlock.outPorts.get(startPortIndex);
                    Port endPort = endBlock.inPorts.get(endPortIndex);
                    Connection connection = new Connection(workspace, startPort, endPort);
                    workspace.connectionSet.add(connection);
                }
            }
        }
    }

}

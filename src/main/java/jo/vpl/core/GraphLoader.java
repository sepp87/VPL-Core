package jo.vpl.core;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Label;
import jo.vpl.block.ReflectionBlock;
import jo.vpl.util.IconType;
import jo.vpl.xml.ConnectionTag;
import jo.vpl.xml.ConnectionsTag;
import jo.vpl.xml.DocumentTag;
import jo.vpl.xml.BlockTag;
import jo.vpl.xml.BlocksTag;
import jo.vpl.xml.ObjectFactory;

/**
 *
 * @author joostmeulenkamp
 */
public class GraphLoader {

    public static void deserialize(File file, Workspace workspace) {

        String errorMessage = "";

        try {
            JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            JAXBElement<DocumentTag> document = (JAXBElement<DocumentTag>) unmarshaller.unmarshal(file);
            DocumentTag documentTag = document.getValue();

            workspace.setScale(documentTag.getScale());
            workspace.setTranslateX(documentTag.getTranslateX());
            workspace.setTranslateY(documentTag.getTranslateY());

            BlocksTag blocksTag = documentTag.getBlocks();
            List<BlockTag> blockTagList = blocksTag.getBlock();
            if (blockTagList != null) {

                for (BlockTag blockTag : blockTagList) {
                    errorMessage = "Block type " + blockTag.getType() + " not found.";
                    Class<?> type = BlockLoader.BLOCK_TYPE_MAP.get(blockTag.getType());
                    Block block = null;

                    ////////////////////////////////////////////////////////
                    ////////////////////////////////////////////////////////
                    ////////////////////////////////////////////////////////
                    // TODO create a block factory class
                    if (type.equals(ReflectionBlock.class)) {
                        String methodName = blockTag.getMethod();
                        Method mType = (Method) BlockLoader.BLOCK_LIBRARY.get(methodName);

                        BlockInfo info = mType.getAnnotation(BlockInfo.class);
                        
//                            public ReflectionBlock(Workspace hostCanvas, String identifier, String category, String description, String[] tags, Method method) {

                        ReflectionBlock reflectionBlock = new ReflectionBlock(workspace, info.identifier(), info.category(), info.description(), info.tags(), mType);

                        Class<?> returnType = mType.getReturnType();
                        if (returnType.equals(Number.class)) {
                            reflectionBlock.addOutPortToBlock("double", double.class);
                        } else if (List.class.isAssignableFrom(returnType)) {

                            reflectionBlock.isListOperatorListReturnType = true;
                            reflectionBlock.addOutPortToBlock(Object.class.getSimpleName(), Object.class);
                        } else {
                            reflectionBlock.addOutPortToBlock(returnType.getSimpleName(), returnType);
                        }

                        if (!info.name().equals("") && info.icon().equals(IconType.NULL)) {
                            reflectionBlock.setName(info.name());
                            Label label = new Label(info.name());
                            label.getStyleClass().add("hub-text");
                            reflectionBlock.addControlToBlock(label);
                        } else {
                            String shortName = info.identifier().split("\\.")[1];
                            reflectionBlock.setName(shortName);
                            Label label = new Label(shortName);
                            label.getStyleClass().add("hub-text");
                            reflectionBlock.addControlToBlock(label);
                        }

                        if (!info.icon().equals(IconType.NULL)) {
                            Label label = reflectionBlock.getAwesomeIcon(info.icon());
                            reflectionBlock.addControlToBlock(label);
                        }

                        // If first input parameter is of type list, then this is a list operator block
                        if (List.class.isAssignableFrom(mType.getParameters()[0].getType())) {
                            reflectionBlock.isListOperator = true;
                        }

                        for (Parameter p : mType.getParameters()) {
                            if (List.class.isAssignableFrom(p.getType())) {
                                reflectionBlock.addInPortToBlock("Object : List", Object.class);
                            } else {
                                reflectionBlock.addInPortToBlock(p.getName(), p.getType());
                            }
                        }

                        block = reflectionBlock;

                        ////////////////////////////////////////////////////////
                        ////////////////////////////////////////////////////////
                        ////////////////////////////////////////////////////////
                    } else {
                        block = (Block) type.getConstructor(Workspace.class).newInstance(workspace);
                    }

                    if (block == null) {
                        continue; // TODO show error message and tell that the block does not exist 
                    }

                    block.deserialize(blockTag);
                    workspace.blockSet.add(block);
                    workspace.getChildren().add(block);
                }
            }

            ConnectionsTag connectionsTag = documentTag.getConnections();
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
        } catch (JAXBException | InstantiationException | IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(GraphLoader.class.getName()).log(Level.SEVERE, errorMessage, ex);
        }
    }

}

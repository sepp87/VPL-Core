package vplcore.graph.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Label;
import vplcore.IconType;
import vplcore.graph.model.Block;
import vplcore.graph.model.BlockInfo;
import vplcore.workspace.Workspace;

/**
 *
 * @author joostmeulenkamp
 */
public class BlockFactory {

    public static Block createBlock(String blockIdentifier, Workspace workspace) {
        Block block = null;
        Object type = BlockLoader.BLOCK_LIBRARY.get(blockIdentifier);

        if (type.getClass().equals(Class.class)) {
            Class<?> clazz = (Class) type;
            block = createBlockFromClass(clazz, workspace);
        } else if (type.getClass().equals(Method.class)) {
            Method method = (Method) type;
            block = createBlockFromMethod(method, workspace);
        }
        return block;
    }

    public static Block createBlockFromClass(Class<?> clazz, Workspace workspace) {
        Block block = null;
        try {
            block = (Block) clazz.getConstructor(Workspace.class).newInstance(workspace);
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            Logger.getLogger(SelectBlock.class.getName()).log(Level.SEVERE, null, e);
        }
        return block;
    }

    public static Block createBlockFromMethod(Method method, Workspace workspace) {
        ReflectionBlock block = null;
        try {
            BlockInfo info = method.getAnnotation(BlockInfo.class);
            block = new ReflectionBlock(workspace, method);

            Class<?> returnType = method.getReturnType();
            if (returnType.equals(Number.class)) {
                block.addOutPortToBlock("double", double.class);
            } else if (List.class.isAssignableFrom(returnType)) {

                block.isListOperatorListReturnType = true;
                block.addOutPortToBlock(Object.class.getSimpleName(), Object.class);
            } else {
                block.addOutPortToBlock(returnType.getSimpleName(), returnType);
            }

            if (!info.name().equals("") && info.icon().equals(IconType.NULL)) {
                block.setName(info.name());
                Label label = new Label(info.name());
                label.getStyleClass().add("block-text");
                block.addControlToBlock(label);
            } else {
                String shortName = info.identifier().split("\\.")[1];
                block.setName(shortName);
                Label label = new Label(shortName);
                label.getStyleClass().add("block-text");
                block.addControlToBlock(label);
            }

            if (!info.icon().equals(IconType.NULL)) {
                Label label = block.getAwesomeIcon(info.icon());
                block.addControlToBlock(label);
            }

            // If first input parameter is of type list, then this is a list operator block
            if (List.class.isAssignableFrom(method.getParameters()[0].getType())) {
                block.isListOperator = true;
            }

            for (Parameter p : method.getParameters()) {
                if (List.class.isAssignableFrom(p.getType())) {
                    block.addInPortToBlock("Object : List", Object.class);
                } else {
                    block.addInPortToBlock(p.getName(), p.getType());
                }
            }

        } catch (Exception e) {
            Logger.getLogger(SelectBlock.class.getName()).log(Level.SEVERE, null, e);
        }
        return block;
    }

}

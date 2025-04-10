package vplcore.graph.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import vplcore.IconType;
import vplcore.graph.block.BlockModel;
import vplcore.workspace.WorkspaceModel;
import vplcore.graph.block.BlockMetadata;

/**
 *
 * @author joostmeulenkamp
 */
public class BlockFactory {

//    public static BlockModel createBlock(String blockIdentifier, WorkspaceModel workspaceModel) {
    public static BlockModel createBlock(String blockIdentifier) {
        BlockModel blockModel = null;
        Object type = BlockLibraryLoader.BLOCK_LIBRARY.get(blockIdentifier);

        if (type.getClass().equals(Class.class)) {
            Class<?> clazz = (Class) type;
//            blockModel = createBlockFromClass(clazz, workspaceModel);
            blockModel = createBlockFromClass(clazz);
        } else if (type.getClass().equals(Method.class)) {
            Method method = (Method) type;
//            blockModel = createBlockFromMethod(method, workspaceModel);
            blockModel = createBlockFromMethod(method);
        }
        return blockModel;
    }

//    public static BlockModel createBlockFromClass(Class<?> clazz, WorkspaceModel workspaceModel) {
    public static BlockModel createBlockFromClass(Class<?> clazz) {
        BlockModel blockModel = null;
        try {
//            blockModel = (BlockModel) clazz.getConstructor(workspaceModel.getClass()).newInstance(workspaceModel);
            blockModel = (BlockModel) clazz.getConstructor().newInstance();
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            Logger.getLogger(BlockFactory.class.getName()).log(Level.SEVERE, null, e);
        }
        return blockModel;
    }

//    public static MethodBlock createBlockFromMethod(Method method, WorkspaceModel workspaceModel) {
    public static MethodBlock createBlockFromMethod(Method method) {
        MethodBlock blockModel = null;
        try {
            BlockMetadata info = method.getAnnotation(BlockMetadata.class);
            blockModel = new MethodBlock(method);

            Class<?> returnType = method.getReturnType();
            if (returnType.equals(Number.class)) {
                blockModel.addOutputPort("double", double.class);
            } else if (List.class.isAssignableFrom(returnType)) {

                blockModel.isListReturnType = true;
                blockModel.addOutputPort(Object.class.getSimpleName(), Object.class);
            } else {
                blockModel.addOutputPort(returnType.getSimpleName(), returnType);
            }

            if (!info.name().equals("") && info.icon().equals(IconType.NULL)) {
                blockModel.nameProperty().set(info.name());
            } else {
                String shortName = info.identifier().split("\\.")[1];
                blockModel.nameProperty().set(shortName);
            }

            // If first input parameter is of type list, then this is a list operator block
            if (method.getParameters().length > 0 && List.class.isAssignableFrom(method.getParameters()[0].getType())) {
                blockModel.isListOperator = true;
            }

            for (Parameter p : method.getParameters()) {
                if (List.class.isAssignableFrom(p.getType())) {
                    blockModel.addInputPort("Object : List", Object.class);
                } else {
                    blockModel.addInputPort(p.getName(), p.getType());
                }
            }

        } catch (Exception e) {
            Logger.getLogger(BlockFactory.class.getName()).log(Level.SEVERE, null, e);
        }
        return blockModel;
    }

}

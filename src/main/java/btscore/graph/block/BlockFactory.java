package btscore.graph.block;

import btscore.AppLauncher;
import btscore.graph.port.AutoConnectable;
import btscore.icons.FontAwesomeSolid;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author joostmeulenkamp
 */
public class BlockFactory {

    public static BlockModel createBlock(String blockIdentifier) {
        BlockModel blockModel = null;
        Object type = BlockLibraryLoader.BLOCK_LIBRARY.get(blockIdentifier);

        if (type.getClass().equals(Class.class)) {
            Class<?> clazz = (Class) type;
            blockModel = createBlockFromClass(clazz);
        } else if (type.getClass().equals(Method.class)) {
            Method method = (Method) type;
            blockModel = createBlockFromMethod(method);
        }
        return blockModel;
    }

    public static BlockModel createBlockFromClass(Class<?> clazz) {
        BlockModel blockModel = null;
        try {
            blockModel = (BlockModel) clazz.getConstructor().newInstance();
        } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException e) {
            Logger.getLogger(BlockFactory.class.getName()).log(Level.SEVERE, null, e);
        }
        return blockModel;
    }

    public static MethodBlock createBlockFromMethod(Method method) {
        MethodBlock blockModel = null;
        try {
            BlockMetadata info = method.getAnnotation(BlockMetadata.class);
            blockModel = new MethodBlock(method);

            // set input ports
            for (Parameter p : method.getParameters()) {
                boolean isAutoConnectable = AutoConnectable.class.isAssignableFrom(p.getType());
                if (List.class.isAssignableFrom(p.getType())) {
                    blockModel.addInputPort("Object : List", Object.class, isAutoConnectable);
                } else {
                    blockModel.addInputPort(p.getName(), p.getType(), isAutoConnectable);
                }
            }

            // set output port
            Class<?> returnType = method.getReturnType();
            boolean isAutoConnectable = AutoConnectable.class.isAssignableFrom(returnType);

            if (returnType.equals(Number.class)) {
                blockModel.addOutputPort("double", double.class, isAutoConnectable);
            } else if (List.class.isAssignableFrom(returnType)) {

                Type genericReturnType = method.getGenericReturnType();
                if (genericReturnType instanceof ParameterizedType) { // if list TODO refactor since the return type is a ParameterizedType of type list
                    ParameterizedType pt = (ParameterizedType) genericReturnType;
                    Type typeArgument = pt.getActualTypeArguments()[0];
                    if (typeArgument instanceof Class) {
                        Class<?> clazz = (Class<?>) typeArgument;
                        blockModel.addOutputPort(clazz.getSimpleName(), clazz, isAutoConnectable);
                    } else {
                        blockModel.isListReturnType = true;
                        blockModel.addOutputPort(Object.class.getSimpleName(), Object.class, isAutoConnectable);
                    }
                }

            } else {
                blockModel.addOutputPort(returnType.getSimpleName(), returnType, isAutoConnectable);
            }

            // set the name
            if (!info.name().equals("") && info.icon().equals(FontAwesomeSolid.NULL)) {
                blockModel.nameProperty().set(info.name());
            } else {
                String shortName = info.identifier().split("\\.")[1];
                blockModel.nameProperty().set(shortName);
            }

            // If first input parameter is of type list, then this is a list operator block
            if (method.getParameters().length > 0 && List.class.isAssignableFrom(method.getParameters()[0].getType())) {
                blockModel.isListOperator = true;
            }

        } catch (Exception e) {
            Logger.getLogger(BlockFactory.class.getName()).log(Level.SEVERE, null, e);
        }
        return blockModel;
    }

    static void testMethod() {
        for (Method m : AppLauncher.class.getDeclaredMethods()) {
            if (m.getName().equals("getKey")) {
                Type genericReturnType = m.getGenericReturnType();
                if (genericReturnType instanceof ParameterizedType) {
                    ParameterizedType pt = (ParameterizedType) genericReturnType;

                    System.out.println("Raw type: " + pt.getRawType()); // Map

                    for (Type arg : pt.getActualTypeArguments()) {
                        System.out.println("Type arg: " + arg); // String, Integer
                    }
                } else {
                    System.out.println("Not parameterized");
                    System.out.println(genericReturnType.getTypeName());
                }

                Type[] types = m.getGenericParameterTypes();
                for (Type type : types) {
//                    if (type instanceof ParameterizedType pt) {
//                        System.out.println("Raw type: " + pt.getRawType()); // Map
//
//                        for (Type arg : pt.getActualTypeArguments()) {
//                            System.out.println("Type arg: " + arg); // String, Integer
//                        }
//                    } else {
                    System.out.println("Not parameterized");
                    System.out.println(type.getTypeName());

//                    }
                }
            }
        }
    }

    public static BlockModel createBlockFromField(Field field) {
        BlockModel blockModel = null;
        if (List.class.isAssignableFrom(field.getType())) {
            if (field.getGenericType() instanceof ParameterizedType pt) {
                if (pt.getActualTypeArguments()[0] instanceof Class<?> clazz) {
                    System.out.println(pt.getActualTypeArguments()[0]);
                    System.out.println(clazz.getSimpleName());
                }

            }

        }
        return blockModel;

    }

}

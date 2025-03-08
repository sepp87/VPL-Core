package vplcore.graph.util;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import vplcore.Config;
import org.reflections.Reflections;
import vplcore.util.FileUtils;
import vplcore.graph.block.BlockModel;
import vplcore.graph.block.BlockMetadata;

/**
 *
 * @author joostmeulenkamp
 */
public class BlockLibraryLoader {

    private static final List<String> INTERNAL_PACKAGES = List.of("vpllib.input", "vpllib.file", "vpllib.spreadsheet");
    private static final List<Class<?>> INTERNAL_METHOD_BLOCKS = List.of(
            vpllib.method.DateMethods.class,
            vpllib.method.FileMethods.class,
            vpllib.method.JsonMethods.class,
            vpllib.method.ListMethods.class,
            vpllib.method.MathMethods.class,
            vpllib.method.ObjectMethods.class,
            vpllib.method.SpreadsheetMethods.class,
            vpllib.method.StringMethods.class
    );

    private static final Logger LOGGER = Logger.getLogger(BlockLibraryLoader.class.getName());

    public static final ObservableList<String> BLOCK_TYPE_LIST = observableArrayList();
    public static final ObservableMap<String, Object> BLOCK_LIBRARY = javafx.collections.FXCollections.observableHashMap();

    private static final Set<String> EXTERNAL_BLOCKS = new HashSet<>();

    public static void loadBlocks() {
        loadInternalBlockClasses();
        loadInternalBlockMethods();
        loadExternalBlocks();
        Collections.sort(BLOCK_TYPE_LIST);
    }

    public static void reloadExternalBlocks() {
        BLOCK_TYPE_LIST.removeAll(EXTERNAL_BLOCKS);
        BLOCK_LIBRARY.keySet().removeAll(EXTERNAL_BLOCKS);
        EXTERNAL_BLOCKS.clear();
        loadExternalBlocks();
        Collections.sort(BLOCK_TYPE_LIST);
    }

    private static void loadExternalBlocks() {
        File dir = new File(Config.get().libraryDirectory());
        File[] libraries = FileUtils.getFilesByExtensionFrom(dir, ".jar");
        List<Class<?>> externalClasses = JarClassLoader.getClassesFromLibraries(libraries);
        loadExternalBlockClasses(externalClasses);
        loadExternalBlockMethods(externalClasses);
    }

    /**
     * Retrieve all blocks from INTERNAL_PACKAGES
     */
    private static void loadInternalBlockClasses() {
        Set<Class<? extends BlockModel>> blockTypes = getBlockTypes(INTERNAL_PACKAGES);
        for (Class<?> blockType : blockTypes) {
            String identifier = getBlockMetadata(blockType).identifier();
            addBlockType(identifier, blockType, false);
        }
    }

    private static Set<Class<? extends BlockModel>> getBlockTypes(List<String> packages) {
        Set<Class<? extends BlockModel>> result = new HashSet<>();
        for (String p : packages) {
            Reflections reflections = new Reflections(p);
            Set<Class<? extends BlockModel>> blockTypes = reflections.getSubTypesOf(BlockModel.class);
            result.addAll(blockTypes);
        }
        return result;
    }

    /**
     * Retrieve all blocks from external libraries
     */
    private static void loadExternalBlockClasses(List<Class<?>> externalClasses) {
        externalClasses = filterEligibleClasses(externalClasses);
        for (Class<?> blockType : externalClasses) {
            String identifier = getBlockMetadata(blockType).identifier();
            addBlockType(identifier, blockType, true);
        }
    }

    private static BlockMetadata getBlockMetadata(Method blockType) {
        return blockType.getAnnotation(BlockMetadata.class);
    }

    private static BlockMetadata getBlockMetadata(Class<?> blockType) {
        return blockType.getAnnotation(BlockMetadata.class);
    }

    private static void addBlockType(String identifier, Object blockType, boolean isExternal) {
        BLOCK_TYPE_LIST.add(identifier);
        BLOCK_LIBRARY.put(identifier, blockType);
        if (isExternal) {
            EXTERNAL_BLOCKS.add(identifier);
        }
    }

    private static List<Class<?>> filterEligibleClasses(List<Class<?>> classes) {
        List<Class<?>> result = new ArrayList<>();
        for (Class<?> clazz : classes) {
            if (BlockModel.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(BlockMetadata.class)) {
                result.add(clazz);
            }
        }
        return result;
    }


    private static void loadInternalBlockMethods() {
        loadMethodBlocks(INTERNAL_METHOD_BLOCKS, false);
    }

    private static void loadExternalBlockMethods(List<Class<?>> externalClasses) {
        loadMethodBlocks(externalClasses, true);
    }

    private static void loadMethodBlocks(List<Class<?>> classes, boolean isExternal) {
        List<Method> methods = getStaticMethodsFromClasses(classes);
        methods = filterEligibleMethods(methods);
        for (Method blockType : methods) {
            String identifier = getBlockMetadata(blockType).identifier();
            addBlockType(identifier, blockType, isExternal);
        }
    }

    private static List<Method> filterEligibleMethods(List<Method> methods) {
        List<Method> result = new ArrayList<>();
        for (Method method : methods) {
            if (method.isAnnotationPresent(BlockMetadata.class)) {
                result.add(method);
            }
        }
        return result;
    }

    private static List<Method> getStaticMethodsFromClasses(List<Class<?>> classes) {
        List<Method> result = new ArrayList<>();
        for (Class<?> clazz : classes) {
            List<Method> methods = getStaticMethodsFromClass(clazz);
            result.addAll(methods);
        }
        return result;
    }

    private static List<Method> getStaticMethodsFromClass(Class<?> clazz) {
        List<Method> result = new ArrayList<>();
        Method[] methods = clazz.getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            if (Modifier.isStatic(m.getModifiers())) {
                result.add(m);
            }
        }
        return result;
    }

    private static void loadStaticFieldsAsBlocks() {
        List<Field> fields = getStaticFieldsFromClass(Math.class);
        for (Field f : fields) {
            try {
                System.out.println(f.get(null));
            } catch (IllegalArgumentException | IllegalAccessException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }

        }
    }

    private static List<Field> getStaticFieldsFromClass(Class<?> c) {
        List<Field> result = new ArrayList<>();
        Field[] fields = c.getFields();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            if (Modifier.isStatic(f.getModifiers())) {
                result.add(f);
            }
        }
        return result;
    }
}

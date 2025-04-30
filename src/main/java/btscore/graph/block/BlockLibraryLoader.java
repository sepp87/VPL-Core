package btscore.graph.block;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import static javafx.collections.FXCollections.observableArrayList;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import btscore.Config;
import org.reflections.Reflections;
import btscore.utils.FileUtils;
import btscore.utils.JarClassLoaderUtils;

/**
 *
 * @author joostmeulenkamp
 */
public class BlockLibraryLoader {

    private static final List<String> INTERNAL_PACKAGES = List.of(
            btslib.input.BooleanBlock.class.getPackageName(),
            btslib.file.ObserveFileBlock.class.getPackageName(),
            btslib.spreadsheet.DataSheetBlock.class.getPackageName(),
            btslib.autoconnect.ReceiverBlock.class.getPackageName()
    );
    private static final List<Class<?>> INTERNAL_METHOD_BLOCKS = List.of(
            btslib.method.DateMethods.class,
            btslib.method.FileMethods.class,
            btslib.method.JsonMethods.class,
            btslib.method.ListMethods.class,
            btslib.method.MathMethods.class,
            btslib.method.ObjectMethods.class,
            btslib.method.SpreadsheetMethods.class,
            btslib.method.StringMethods.class
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
        List<Class<?>> externalClasses = JarClassLoaderUtils.getClassesFromLibraries(libraries);
        loadExternalBlockClasses(externalClasses);
        loadExternalBlockMethods(externalClasses);
    }

    /**
     * Retrieve all blocks from INTERNAL_PACKAGES
     */
    private static void loadInternalBlockClasses() {
        Set<Class<? extends BlockModel>> blockTypes = getBlockTypes(INTERNAL_PACKAGES);
        loadBlockClasses(blockTypes, false);
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
        List<Class<? extends BlockModel>> blockTypes = filterEligibleClasses(externalClasses);
        loadBlockClasses(blockTypes, true);
    }

    private static void loadBlockClasses(Collection<Class<? extends BlockModel>> classes, boolean isExternal) {
        for (Class<?> blockType : classes) {
            BlockMetadata metadata = getBlockMetadata(blockType);
            if(metadata ==  null) {
                continue;
            }
            String identifier = metadata.identifier();
            addBlockType(identifier, blockType, isExternal);
            for (String alias : metadata.aliases()) {
                addBlockType(alias, blockType, isExternal);
            }
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

    private static List<Class<? extends BlockModel>> filterEligibleClasses(List<Class<?>> classes) {
        List<Class<? extends BlockModel>> result = new ArrayList<>();
        for (Class<?> clazz : classes) {
            if (BlockModel.class.isAssignableFrom(clazz) && clazz.isAnnotationPresent(BlockMetadata.class)) {
                result.add((Class<? extends BlockModel>) clazz);
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
            BlockMetadata metadata = getBlockMetadata(blockType);
            String identifier = metadata.identifier();
            addBlockType(identifier, blockType, isExternal);
            for (String alias : metadata.aliases()) {
                addBlockType(alias, blockType, isExternal);
            }
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

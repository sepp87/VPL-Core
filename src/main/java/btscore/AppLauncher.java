package btscore;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.util.IOUtils;
import org.xml.sax.SAXException;
import btscore.graph.port.PortType;
import btscore.graph.block.BlockLibraryLoader;
import btslib.method.JsonMethods;



//
/**
 *
 * @author joostmeulenkamp
 */
public class AppLauncher {

    public static void main(String[] args) throws IOException, OpenXML4JException, SAXException, Exception {
//        TestGetIntegerValue();
//        TestGetLongValue();
//        TestGetDoubleValue();
        testField();

        IOUtils.setByteArrayMaxOverride(300_000_000);
        if (false) {
            return;
        }

        //Load all block types
        BlockLibraryLoader.loadBlocks();

        System.out.println("Launcher.main() Number of loaded blocks is " + BlockLibraryLoader.BLOCK_TYPE_LIST.size());

//        test();
        // a = 0,0 > 0,10
        // b = 125,0 > 
        // c = 0,95 > 10,95
        // 
        //Launch the UI
        App.launch(App.class);
    }
    List<ChronoUnit> stringList;

    static void testField() {
        try {
            Field f = AppLauncher.class.getDeclaredField("stringList");
            if (List.class.isAssignableFrom(f.getType())) {
                if (f.getGenericType() instanceof ParameterizedType pt) {
                    if (pt.getActualTypeArguments()[0] instanceof Class<?> clazz) {
                        System.out.println(pt.getActualTypeArguments()[0]);
                        System.out.println(clazz.getName());
                    }
                    List<TemporalUnit> test = List.of(ChronoUnit.MONTHS);
                    List<PortType> test2 = List.of(PortType.INPUT);

                }

            }

        } catch (NoSuchFieldException | SecurityException ex) {
            Logger.getLogger(AppLauncher.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    static <T> T getKey(Map<String, T> map) {
        return map.get("tets");
    }

    static Map<String, Integer> testGenerics(List<String> list, Double dbl, Map<String, Integer> map) {
        return Collections.emptyMap();
    }

    static void TestJsonAsList() {
        JsonMethods.asList("[\"str\",6,7]");
        JsonMethods.asList("[1,6,7]");
        JsonMethods.asList("[1.0,6,7]");
        JsonMethods.asList("[1.1,6,7]");
    }

    static void TestGetIntegerValue() {
        String rawValue = "-133,452";
        String regExp = "-?[0-9]{1,10}";
        boolean isLong = rawValue.matches(regExp);
        System.out.println(isLong);
        System.out.println(Integer.valueOf("1.0"));
        System.out.println(Integer.valueOf(" 1.0 "));
        System.out.println(Integer.valueOf(" -1.0 "));
    }

    static void TestGetLongValue() {
        String rawValue = "-12345678911234567892";
        String regExp = "-?[0-9]{1,19}";
        boolean isLong = rawValue.matches(regExp);
        System.out.println(isLong);
        Long lng = Long.parseLong("123456");
    }

}

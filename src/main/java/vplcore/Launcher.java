package vplcore;

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
import vplcore.graph.port.PortType;
import vplcore.graph.block.BlockLibraryLoader;
import vpllib.method.JsonMethods;

// 0 REFACTOR event handlers on scene / editor controller level
// 0 adding huge lists to input.text causes long loading. should be made concurrent also
// 1 IMPROVEMENT Method block List methods output cant be used in further operations - TODO TEST FIX
// 
// WORK IN PROGRESS
// 1 IMPROVEMENT clean up App and Workspace according to UI structure
//      rename Workspace helpers to managers?
//
// BACKLOG
// 3 REFACTOR merge integer and double slider and refactor event handlers
// 0 refactor port data type to support data structures e.g. with TypeTokens also used in GSON. 
// 0 refactor concurrency of method blocks
// 0 refactor methodblock progressIndicator spinner need for checking label width != 0.0
// 0 REFACTOR BlockInfoPanel, BlockExceptionPanel to MVC
// 1 IMPROVEMENT update overall UI to show port data hints ... 
// 4 IMPROVEMENT create elaborate tests TBD what to test
// 3 IMPROVEMENT differentiate between mouse wheel and touch pad. Add trackpad support e.g. zoom by pinch, pan by drag
// 3 IMPROVEMENT DirectoryBlock (DirectoryChooser) and MultiFileBlock (showOpenMultipleDialog)
// ? TODO potential bug - monitor if selected blocks list is updated according to the number of selected blocks on the workspace
// 0 evaluate removal bidirectional binding with layoutx&y of block view to model, somewhere LayoutX & Y is set, which is causing an error message. replace by translatex&y
// 5 IMPROVEMENT look into mouse support on mac in zoomcontroller scrolling
// 5 IMPROVEMENT styling of scrollbars for BlockSearch and dynamically resize BlockSearch according to ListView size
// 5 IMPROVEMENT - SpreadSheets
//      - datasheet viewer reordering columns does not update data sheet
//      - add getColumn and transpose to Matrix methods
//      - Improve transpose with jagged lists
//      - DataSheet support LocalDate instead of Date
//      - support reading larger excel files. at the moment excels of 80k rows, with 5MB are okay, 12MB or so are not. At least 20MB should be okay
//      - Spreadsheet methods support loading multiple sheets
//      - DataSheetViewer - hide trailing cells with no data
//
// BACKLOG BLOCKS
// 3DViewerBlock
// Geometry Blocks
// ChatGPT Block
// WebClientBlock
// 2d Map
// special block/new control: retrigger block process 
// accumulate results + reset possibility
//
// QUESTIONS
// getResourceAsStream - path should contain forward dashes and cannot use File.separatorChar... why?
//
// DONE
// FILE get encoding of file
// Password block
// REFACIOR Block search - list view select cell on hover
// IMPROVEMENT aliases for blocks from classes
// ComboBoxBlock - ON HOLD, currently no use to further develop, since the only use case for now is the temporal unit block
// TemporalUnitBlock
// 0 reimplement focusing e.g. when click onFreeSpace the edtor should request focus. Because at the moment it is to strict e.g. hovering a combo box list loses focus to the editor and collapses the menu
//      solved - added global key input listeners on scene level
// JSON get key e.g. foo.bar.x[1]
// BlockInfoPanel remove space below each label e.g. port descriptions
// 0 IMRPOVEMENT Method block exceptions e.g. when not all inputs are set, the exception is not quite understandable
//      - Still not quite clear, but cleaned up superfluous exceptions
// Support block aliases
// Bug fix - Json String as list, fix parsing numbers instead lazily parsed numbers
// 0 REFACTOR Port - evaluate if process() is not called too often, e.g. double call onIncomingConnectionAdded and incomingDataChanged, also on onConnectionRemoved
//      - Trigger only when connections are added or removed and data changed. In case data changed and a connection was removed or added, process safely is only called once.
//
// NOTES
// Mouse position is needed when pasting blocks and when creating a new connection 
// Code duplication - Radial Menu, Selection Rectangle and Block Search all test if mouse is on Editor View or Workspace View 
// Check where workspaceController.getView() is used and refactor it
//
// GUIDELINES
// Put everything inside the models that needs to be saved and all business logic needed to make a script work in headless mode
// TODO naming conventions for event handlers & change listeners and placement of handlers / listeners in code
// When a dedicated listener is needed, it should be declared directly above the method it calls, so it easier to find it
// Naming
//            Name	Purpose                                 Implied Behavior                    Good For
//            *Index	Lookup tables, mostly passive           Read-heavy, structure-only          Groups, hierarchies
//            *Registry	Active coordination via registration	Event-aware, dynamic resolution     Plugins, wireless ports
//            *Manager	Heavy orchestration, lifecycles         Stateful control, complex logic     NodeManager, SessionManager
//
// TESTS 
// Create connection - Link backward and link forward
// MethodBlock - lacing of lists
// Remove block - remove block and connections
/**
 *
 * @author joostmeulenkamp
 */
public class Launcher {

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
            Field f = Launcher.class.getDeclaredField("stringList");
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
            Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void testMethod() {
        for (Method m : Launcher.class.getDeclaredMethods()) {
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

package vplcore;

import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.util.IOUtils;
import org.xml.sax.SAXException;
import vplcore.graph.util.BlockLibraryLoader;
import vpllib.method.JsonMethods;

// 0 adding huge lists to input.text causes long loading. should be made concurrent also
// 0 IMRPOVEMENT Method block exceptions e.g. when not all inputs are set, the exception is not quite understandable
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
// 0 REFACTOR Port - evaluate if process() is not called too often
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
//
// BACKLOG BLOCKS
// JSON get key e.g. foo.bar.x[1]
// FILE get encoding of file
// TemporalUnitBlock
// 3DViewerBlock
// Geometry Blocks
// ChatGPT Block
// WebClientBlock
// 2d Map
// special block/new control: retrigger block process 
// accumulate results + reset possibility
// 
//
// QUESTIONS
// getResourceAsStream - path should contain forward dashes and cannot use File.separatorChar... why?
//
//
// DONE
// 4 IMPROVEMENT multi workspace support with copy-paste
//      - blocks are now unaware of their workspace, so blocks can be pasted from one workspace to another freely
// 0 return BlockGroup blocks as immutable list
// 3 IMPROVEMENT test between integer or boolean using modulus operation instead of trying to cast
// 4 IMPROVEMENT add save and save as commands
// 0 when built the app cannot find circle-xmark-solid.svg
// 1 IMPROVEMENT Add undo/redo functionality
//      0 REJECTED Move and Resize commands do not need to be executed, only recorded > current behaviour does NOT break the app
// 0 BUG re-ordering columns in table view does not update the columns to A, B, C but B, A, C
// 0 BUG tableviewblock rename block causes info button not to align on the right
//      Solved by binding BaseLabel.textField.prefWidth to BaseLabel.width and 
// 0 BUG baseLabel prevents block to resize to size less than its width
//      Solved by setting BaseLabel.minWidth and prefWidth to Region.USE_COMPUTED_SIZE and menuBox.setMinWidth(0) and menuBox.setPrefWidth(0);
// Show progress indicator for CPU intensive (method) blocks 
// 0 Improve Json.asList to cast as long or integer
// 0 DataSheet throw index out of bounds for -2 and int number bigger than row count, and use -1 as default case so user can inspect data
// Boolean input block
// Excel Blocks
// CSV Blocks
// 0 DataSheet support spreadsheets without headers
// 0 DataSheet support name of sheet
// 0 BUG adding readExcel to textpanel while already connected to DataSheetBlock triggers recalculation? 
//          Resolved in PortModel onConnectionsChanged. Only call onIncoming added or removed for input ports
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

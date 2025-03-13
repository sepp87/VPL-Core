package vplcore;

import java.io.File;
import java.io.IOException;
import vplcore.util.DataParsingUtils;
import vplcore.graph.util.BlockLibraryLoader;
import vpllib.method.JsonMethods;
import vpllib.method.SpreadsheetMethods;
import vpllib.spreadsheet.DataSheet;

// 0 BUG align commands not working because the selectedblocks were zero when initialized
// 0 BUG letterbar does not resize to absolute column minimum width, due to label needing minimum width
// 0 BUG tableviewblock rename block causes info button not to align on the right
// 0 remove handlers, listeners, bindings of customization
// 0 IMRPOVEMENT Method block exceptions e.g. when not all inputs are set, the exception is not quite understandable
// 0 Block Loader refactor
// 0 return BlockGroup blocks as immutable list
// 1 IMPROVEMENT Method block List methods output cant be used in further operations - TODO TEST FIX
// 4 IMPROVEMENT add scrollbars for TextBlock 
// 4 IMPROVEMENT styling of scrollbars for BlockSearch and dynamically resize BlockSearch according to ListView size
//
// WORK IN PROGRESS
// 1 IMPROVEMENT clean up App and Workspace according to UI structure
// 1 IMPROVEMENT Add undo/redo functionality
//      0 TODO Move and Resize commands do not need to be executed, only recorded
//
// BACKLOG
// 0 REFACTOR Port - evaluate if calculate is not called to often
// 0 REFACTOR BlockInfoPanel, BlockExceptionPanel to MVC
// 1 IMPROVEMENT update overall UI to show port data hints ... 
// 4 IMPROVEMENT create elaborate tests TBD what to test
// 4 IMPROVEMENT add save and save as commands
// 4 IMPROVEMENT reload plugins on demand from menu bar
// 4 IMPROVEMENT multi workspace support with copy-paste
// 4 IMPROVEMENT TODO set blocks in CopyPasteMemory to deactivated to disable unnecessary calculations. First needed when introducing dynamic blocks (e.g. timers, counters, file observers and so on) anything that could trigger an automatic recalculation
// 3 IMPROVEMENT differentiate between mouse wheel and touch pad. Add trackpad support e.g. zoom by pinch, pan by drag
// 3 IMPROVEMENT DirectoryBlock (DirectoryChooser) and MultiFileBlock (showOpenMultipleDialog)
// 3 IMPROVEMENT test between integer or boolean using modulus operation instead of trying to cast
// 3 REFACTOR merge integer and double slider and refactor event handlers
// ? TODO potential bug - monitor if selected blocks list is updated according to the number of selected blocks on the workspace
// 0 evaluate removal bidirectional binding with layoutx&y of block view to model, somewhere LayoutX & Y is set, which is causing an error message. replace by translatex&y
// 5 IMPROVEMENT look into mouse support on mac in zoomcontroller scrolling
//
// BACKLOG BLOCKS
// TemporalUnitBlock
// 3DViewerBlock
// Geometry Blocks
// ChatGPT Block
// WebClientBlock
// 2d Map
// Excel Blocks
// CSV Blocks
// 
// DONE
// 0 BUG exception panel closes if new exception is added - solved by removing old exceptions first after processing, keeping the exceptions list populated, so the exception panel does not remove itself
// 0 BUG Connections are not removed when block is removed
// 0 BUG null from string not forwarded to json aslist
// 0 BUG for blockmethod, even without input block already throws exception
// 0 baseModel removed to readonly
// 0 Reset undo/redo stack when loading file and creating a new file
// undo/redo - align, createBlock, moveBlock, resizeBlock, RemoveSelectedBlocks, RemoveGroup, GroupBlocks, CreateConnection, RemoveConnection, PasteBlocks
//      0 revive block subclasses, add abstract initialize method to BlockModel and call from revive();
//      0 revive ports
//      0 GroupBlocks command should not be recorded in case there are less than two selected blocks 
//      0 PasteBlocks command should not be recorded in case there are no copied blocks available
//      0 remove connection command in conjunction with removal of block
//      0 remove connection command in conjunction with connection created for occupied port
//      0 revive block / groups / connections routinemake new objects or revive old objects? 
//          0 thought on using new objects - guaranteed that listeners, handlers and bindings are correct, but when replacing old block with new blocks, redo/undo could yield unexecutable situations because there is no reference to the replacement block -> so revival method needed
// 0 IMPROVEMENT first set blocks active when added to workspace, instead of deactivating in copypastememory and activating on paste
// File Methods
// ObserveFileBlock - observe if file is updated
// Date.fromString block
// StringBlock add date support
// 0 BaseModel activeProperty should be readonly
// BUG addition throws error in text panel, cause? append text replaced by setText
// 0 REFACTOR Connection / Port who removes what? does Port remove itself and connection also only removes itself
// BUG string to text connection does not calculate immediately
// 2 IMPROVEMENT Add MethodHub support for methods with more than 2 in ports 
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

    public static void main(String[] args) throws IOException {

        if (false) {
            return;
        }

        //Load all block types
        BlockLibraryLoader.loadBlocks();

        System.out.println("Launcher.main() Number of loaded blocks is " + BlockLibraryLoader.BLOCK_TYPE_LIST.size());

//        TestGetIntegerValue();
//        TestGetDoubleValue();
//        TestGetLongValue();
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
    }

    static void TestGetLongValue() {
        String rawValue = "-12345678911234567892";
        String regExp = "-?[0-9]{1,19}";
        boolean isLong = rawValue.matches(regExp);
        System.out.println(isLong);
        Long lng = Long.parseLong("123456");
    }

    static void TestGetDoubleValue() {
        Double value = DataParsingUtils.getDoubleValue("100 000");
        value = DataParsingUtils.getDoubleValue("100,000");
        value = DataParsingUtils.getDoubleValue("100000");
        value = DataParsingUtils.getDoubleValue("100000d");
        System.out.println(value + " ");
    }

}

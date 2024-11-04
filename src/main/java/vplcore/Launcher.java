package vplcore;

import vplcore.util.DataParsingUtils;
import vplcore.graph.util.BlockLoader;
import vpllib.method.JsonMethods;

// 4 BUG remove button of connection on copied blocks is shown on original connection
// 4 BUG Directly paste after select blocks > copy, blocks are pasted at 0,0
// 4 BUG hit space on input.string causes zoomtofit and " " without triggering recalculation
// 4 IMPROVEMENT reload plugins on demand from menu bar
// 1 BUG Info panel does not move down when aligning blocks
// 1 IMPROVEMENT Set block exception panel when block throws exception
// 1 IMPROVEMENT Method block List methods output cant be used in further operations - TODO TEST FIX
// 2 IMPROVEMENT Add MethodHub support for methods with more than 2 in ports 
// 3 IMPROVEMENT test between integer or boolean using modulus operation instead of trying to cast
// 3 IMPROVEMENT DirectoryBlock (DirectoryChooser) and MultiFileBlock (showOpenMultipleDialog)
// 3 REFACTOR integer and double slider event handlers
// 3 IMPROVEMENT differentiate between mouse wheel and touch pad. Add trackpad support e.g. zoom by pinch, pan by drag
// 4 REFACTOR merge KeyEventHandlers of KeyboardInputHandler and ZoomManager
// 4 IMPROVEMENT create elaborate tests TBD what to test
// 4 IMPROVEMENT setup clean up method event handlers after deleting vpllib blocks
// 4 IMPROVEMENT add scrollbars for TextBlock 
// 4 IMPROVEMENT styling of scrollbars for BlockSearch and dynamically resize BlockSearch according to ListView size
// 4 IMPROVEMENT add save and save as commands
//
// WORK IN PROGRESS
// 1 IMPROVEMENT clean up App and Workspace according to UI structure
// 1 IMPROVEMENT update overall UI to show port data hints ... 
//
// DONE
// 1 IMPROVEMENT ignore zoom when block search openened and add zoom start and stop for mac
// 1 BUG selection discarded when clicking on menu bar
// 1 BUG selection discarded when exiting radial menu
//
// NOTES
// Mouse position is needed when pasting blocks and when creating a new connection 
// Code duplication - Radial Menu, Selection Rectangle and Block Search all test if mouse is on Editor View or Workspace View 
// Check where workspaceController.getView() is used and refactor it
/**
 *
 * @author JoostMeulenkamp
 */
public class Launcher {

    public static void main(String[] args) {

        //Load all block types
        BlockLoader.loadInternalBlocks();
        BlockLoader.loadExternalBlocks();
        BlockLoader.loadInternalMethodBlocks();
        BlockLoader.loadExternalMethodBlocks();

//        TestGetIntegerValue();
//        TestGetDoubleValue();
//        TestGetLongValue();
//        test();
        // a = 0,0 > 0,10
        // b = 125,0 > 
        // c = 0,95 > 10,95
        // 
        if (false) {
            return;
        }
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


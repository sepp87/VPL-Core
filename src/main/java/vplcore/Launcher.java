package vplcore;

import vplcore.util.DataParsingUtils;
import vplcore.graph.util.BlockLoader;
import vpllib.method.JsonMethods;


// 0 remove WorkspaceController from blockmodel
// 0 remove bidirectional binding with layoutx&y of block view to model, somewhere LayoutX & Y is set, which is causing an error message
// 0 info panel to block
// 0 info panel move with block
// 0 text block transfer min & pref size to content grid
// 0 group extents
// 0 color / double / file / integer blocks
// 0 remove block > should workspace listen to block wishing to be removed or should workspace remove block actively... same with selected

// 1 IMPROVEMENT Add undo/redo functionality
// 1 BUG Info panel does not move down when aligning blocks
// 4 IMPROVEMENT reload plugins on demand from menu bar
// 1 IMPROVEMENT Set block exception panel when block throws exception
// 1 IMPROVEMENT Method block List methods output cant be used in further operations - TODO TEST FIX
// 2 IMPROVEMENT Add MethodHub support for methods with more than 2 in ports 
// 1 IMPROVEMENT update overall UI to show port data hints ... 
// 4 IMPROVEMENT add scrollbars for TextBlock 
// 4 IMPROVEMENT styling of scrollbars for BlockSearch and dynamically resize BlockSearch according to ListView size
// 4 IMPROVEMENT add save and save as commands
// 3 IMPROVEMENT test between integer or boolean using modulus operation instead of trying to cast
// 3 IMPROVEMENT DirectoryBlock (DirectoryChooser) and MultiFileBlock (showOpenMultipleDialog)
// 3 REFACTOR integer and double slider event handlers
// 3 IMPROVEMENT differentiate between mouse wheel and touch pad. Add trackpad support e.g. zoom by pinch, pan by drag
// 4 IMPROVEMENT create elaborate tests TBD what to test
// 4 IMPROVEMENT setup clean up method event handlers after deleting vpllib blocks
// 4 IMPROVEMENT TODO set blocks in CopyPasteMemory to deactivated to disable unnecessary calculations. First needed when introducing dynamic blocks (e.g. timers, counters, file observers and so on) anything that could trigger an automatic recalculation
// ? TODO potential bug - monitor if selected blocks list is updated according to the number of selected blocks on the workspace
// 4 IMPROVEMENT multi workspace support with copy-paste
//
// WORK IN PROGRESS
// 1 IMPROVEMENT clean up App and Workspace according to UI structure
// 1 IMPROVEMENT Block to MVC Pattern - WorkspaceController handle onConnectionsChanged, onBlockGroupsChanged 
// 1 IMPROVEMENT Block to MVC Pattern - BlockMethodModel
// 1 IMPROVEMENT Block to MVC Pattern - Input Blocks
// 1 IMPROVEMENT Block to MVC Pattern - Block Model, View, Controller
// 1 IMPROVEMENT Block to MVC Pattern - BlockInfoPanel, BlockExceptionPanel
//
// DONE
// 1 IMPROVEMENT ignore zoom when block search openened and add zoom start and stop for mac
// 1 BUG selection discarded when clicking on menu bar
// 1 BUG selection discarded when exiting radial menu
// 4 BUG hit space on input.string causes zoomtofit and " " without triggering recalculation
// 4 REFACTOR merge KeyEventHandlers of KeyboardInputHandler and ZoomManager
// 4 BUG mouse position is not registered correctly after dragging
// 4 BUG mouse position is not registered correctly when dragging
// 4 BUG remove button of connection on copied blocks is shown on connection it was last seen on
// 1 IMPROVEMENT introduce global action manager, state manager and event router
// 4 BUG group moving does not work
// 4 BUG move single blocks does not work for group
// 1 IMPROVEMENT add clipboard for copying
// 4 BUG connections are not pasted when pasting deleted blocks that were copied > implement clipboard
//
// NOTES
// Mouse position is needed when pasting blocks and when creating a new connection 
// Code duplication - Radial Menu, Selection Rectangle and Block Search all test if mouse is on Editor View or Workspace View 
// Check where workspaceController.getView() is used and refactor it
//
// GUIDELINES
// Put everything inside the models that needs to be saved and all business logic needed to make a script work in headless mode
// TODO naming conventions for event handlers & change listeners and placement of handlers / listeners in code
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

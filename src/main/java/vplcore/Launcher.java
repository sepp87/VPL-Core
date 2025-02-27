package vplcore;

import vplcore.util.DataParsingUtils;
import vplcore.graph.util.BlockLoader;
import vpllib.method.JsonMethods;

// string to text connection does not calculate immediately
// 0 return BlockGroup blocks as immutable list
// 1 IMPROVEMENT Method block List methods output cant be used in further operations - TODO TEST FIX
// 2 IMPROVEMENT Add MethodHub support for methods with more than 2 in ports 
// 4 IMPROVEMENT add scrollbars for TextBlock 
// 4 IMPROVEMENT styling of scrollbars for BlockSearch and dynamically resize BlockSearch according to ListView size
// 0 BaseModel activeProperty should be readonly
// 0 PreConnection create connection should trigger create connection command and keep track if connections were removed (and not trigger seperate commands)
//
// WORK IN PROGRESS
// 1 IMPROVEMENT clean up App and Workspace according to UI structure
//
// BACKLOG
// 0 REFACTOR BlockInfoPanel, BlockExceptionPanel to MVC
// 0 REFACTOR Connection / Port who removes what? does Port remove itself or also connections? and vice versa
// 1 IMPROVEMENT update overall UI to show port data hints ... 
// 1 IMPROVEMENT Add undo/redo functionality
//      0 remove connection command in conjunction with removal of block
//      0 remove connection command in conjunction with connection created for occupied port
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
// DONE
// 0 BlockGroup double click should still trigger BlockSearch
// 0 REFACTOR Connection MVC - double check if all listeners and bindings are removed
// 0 REFACTOR BlockGroup MVC - double check if all listeners and bindings are removed
// 0 BlockInfoPanel should be set by workspace e.g. like connection, block and group
// 0 REFACTOR Block MVC - double check if all listeners and bindings are removed
// 0 BlockModelInfoPanel place below buttons but above all else > panel blocked basebuttons > solved with setpickonbounds(false) for infopanel
// 0 BlockView move to top when clicked
// 0 WorkspaceView split into GroupLayer, BlockLayer, ConnectionLayer, InfoLayer
// 0 BUG remove block does not remove exception panel
// 1 IMPROVEMENT Block to MVC Pattern
// 1 IMPROVEMENT Set block exception panel when block throws exception > file block throw file not exists exception
// 0 remove WorkspaceController from blockmodel
// 0 Port to MVC
// BUG when delete a block, the connection is not removed and there is an error message
// 0 Connection to MVC
//      0 Connection should be removed by the workspace when a block is removed and not by itself
//      0 connection constructor remove from copypastememory
//      0 connection constructor remove fron workspacemodel
// BUG when group is placed above connection, the removebutton is not shown anymore
// 0 BlockGroup remove single block
// 0 BlockGroup blocks only in single group
// 0 BlockGroup to MVC
//      When loading a vplxml, the size of the group is not right. MVC pattern might automatically solve this issue
// 0 remove block > should workspace listen to block requesting to be removed or should workspace remove block actively... same with selected... if block requests, then workspace should create listeners for removal and selection
//      0 to avoid an overhead of listeners, direct control is preferred
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
// Link backward
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

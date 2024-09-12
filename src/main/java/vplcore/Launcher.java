package vplcore;

import vplcore.graph.util.BlockLoader;
import vpllib.method.JsonMethods;

// 1 BUG short cuts are not working, maybe because blocked by zoommanager
// 1 IMPROVEMENT Method block List methods output cant be used in further operations - TODO TEST FIX
// 1 IMPROVEMENT update overall UI to show port data hints, exceptions, ... 
// 1 IMPROVEMENT Ensure that event handlers and bindings are removed as soon objects are removed from the workspace to prevent memory leaks
// 1 IMPROVEMENT clean up App and Workspace according to UI structure
// 2 IMPROVEMENT select block should stay the same size regardless of the zoom (probably needs to be on the contentGroup and not on the workspace, just like the radial menu)
// 2 IMPROVEMENT Add MethodHub support for methods with more than 2 in ports 
// 2 IMPROVEMENT create elaborate tests TBD what to test
// 3 IMPROVEMENT test between integer or boolean using modulus operation instead of trying to cast
// 3 IMPROVEMENT DirectoryBlock (DirectoryChooser) and MultiFileBlock (showOpenMultipleDialog)
// 3 IMPROVEMENT Add trackpad support e.g. zoom by pinch, pan by drag
// 3 REFACTOR integer and double slider event handlers
// 3 IMPROVEMENT differentiate between mouse wheel and touch pad, zoom increments should be less big on mac when zooming with a mouse wheel
// 4 BUG Zoom start and stop does not work on windows
// 4 REFACTOR merge KeyEventHandlers of KeyboardInputHandler and ZoomManager
//
// WORK IN PROGRESS
// 1 IMPROVEMENT replace method references with event handlers (BlockX, Group, Port, Connection, Workspace
//
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
        Double value = Util.getDoubleValue("100 000");
        value = Util.getDoubleValue("100,000");
        value = Util.getDoubleValue("100000");
        value = Util.getDoubleValue("100000d");
        System.out.println(value + " ");
    }

}

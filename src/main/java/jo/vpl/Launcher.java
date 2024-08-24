package jo.vpl;

import jo.vpl.core.BlockLoader;
import jo.vpl.core.Util;
import jo.vpl.lib.JsonMethods;

// List methods output cant be used in further operations - TODO TEST FIX
// 1 IMPROVEMENT rename blocks to blocks
// 1 IMPROVEMENT save output type to file so connections don't get lost when loading a file
// 2 IMPROVEMENT create elaborate tests TBD what to test
// 2 IMPROVEMENT update overall UI to show port data hints, exceptions, ... 
// 2 IMPROVEMENT Copy reflection block
// 2 IMPROVEMENT refactor select block so the block creation is delegated to a block factory
// 2 IMPROVEMENT improve package structure e.g. block stuff put together, workspace stuff together, base input blocks in vpl-core, etc.
// 3 BUG where block is created at the top left when not moving the mouse after initially booting up the app and immediately double clicking to create a new block
// 3 BUG when opening a file and cancelling the action, everything is removed as if a new file was created
// 3 BUG selection rectangle offset when directly dragging directly after being on a block before, does not always trigger
// 3 IMPROVEMENT differentiate between mouse wheel and touch pad, zoom increments should be less big on mac when zooming with a mouse wheel
// 3 IMPROVEMENT select block should stay the same size regardless of the zoom (probably needs to be on a different pane)
// 3 IMPROVEMENT isolate RadialMenu to a standalone library
// 3 IMPROVEMENT remove external OBJ code, due to enforcing unnecessary GPL license
// 3 IMPROVEMENT test between integer or boolean using modulus operation instead of trying to cast
// 3 IMPROVEMENT sift through jo.vpl.util package and consolidate
/**
 *
 * @author JoostMeulenkamp
 */
public class Launcher {

    public static void main(String[] args) {

        //Load all block types
        BlockLoader.loadInternalBlocks();
        BlockLoader.loadExternalBlocks();
        BlockLoader.loadStaticMethodsAsBlocks();

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

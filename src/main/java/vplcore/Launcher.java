package vplcore;

import javafx.geometry.Point2D;
import vplcore.graph.util.BlockLoader;
import vpllib.method.JsonMethods;


// 1 IMPROVEMENT Method block List methods output cant be used in further operations - TODO TEST FIX
// 1 IMPROVEMENT clean up App and Workspace according to UI structure
// 2 IMPROVEMENT Add MethodHub support for methods with more than 2 in ports 
// 3 IMPROVEMENT test between integer or boolean using modulus operation instead of trying to cast
// 3 IMPROVEMENT DirectoryBlock (DirectoryChooser) and MultiFileBlock (showOpenMultipleDialog)
// 3 REFACTOR integer and double slider event handlers
// 3 IMPROVEMENT differentiate between mouse wheel and touch pad. Add trackpad support e.g. zoom by pinch, pan by drag
// 4 REFACTOR merge KeyEventHandlers of KeyboardInputHandler and ZoomManager
// 4 IMPROVEMENT create elaborate tests TBD what to test
// 4 IMPROVEMENT setup clean up method event handlers after deleting vpllib blocks
//
// WORK IN PROGRESS
// 1 IMPROVEMENT update overall UI to show port data hints, exceptions, ... 
//
// DONE
// 1 IMPROVEMENT replace method references with event handlers (BlockX, Group, Port, Connection, Workspace
// 1 IMPROVEMENT Ensure that event handlers and bindings are removed as soon objects are removed from the workspace to prevent memory leaks
// 2 IMPROVEMENT select block should stay the same size regardless of the zoom (probably needs to be on the contentGroup and not on the workspace, just like the radial menu)
// 1 BUG space bar causes zoom in
// 3 IMPROVEMENT zoom increments should be less big on mac when zooming with a mouse wheel
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

    public static Point2D getPerpendicularOffset(Point2D vector, double offset) {
        // Perpendicular vector: (-y, x)
        Point2D perpendicular = new Point2D(-vector.getY(), vector.getX());

        // Normalize the perpendicular vector
        Point2D normalizedPerpendicular = perpendicular.normalize();

        // Scale by the desired offset
        return normalizedPerpendicular.multiply(offset);
    }

    public static void test() {
        Point2D originalVector = new Point2D(0, 125);
        double offset = 10;

        Point2D offsetVector = getPerpendicularOffset(originalVector, offset);
        System.out.println("Offset vector: " + offsetVector);

        Point2D b = new Point2D(125, 0);
        Point2D c = new Point2D(0, 95);
        Point2D vector = c.subtract(b);
        offsetVector = getPerpendicularOffset(vector, offset);
        System.out.println("Offset vector: " + vector.subtract(offsetVector));
        
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

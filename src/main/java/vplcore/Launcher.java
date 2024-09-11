package vplcore;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.beans.property.ReadOnlyObjectProperty;
import vplcore.graph.util.BlockLoader;
import vpllib.method.JsonMethods;

// List methods output cant be used in further operations - TODO TEST FIX
// 2 BUG Zoom start and stop does not work on windows
// 2 IMPROVEMENT Ensure that event handlers and bindings are removed as soon objects are removed from the workspace to prevent memory leaks
// 2 IMPROVEMENT clean up App and Workspace according to UI structure
// 2 IMPROVEMENT create elaborate tests TBD what to test
// 2 IMPROVEMENT update overall UI to show port data hints, exceptions, ... 
// 3 IMPROVEMENT select block should stay the same size regardless of the zoom (probably needs to be on the contentGroup and not on the workspace, just like the radial menu)
// 3 IMPROVEMENT test between integer or boolean using modulus operation instead of trying to cast
// 3 IMPROVEMENT DirectoryBlock (DirectoryChooser) and MultiFileBlock (showOpenMultipleDialog)
// 3 IMPROVEMENT Add trackpad support e.g. zoom by pinch, pan by drag
// 3 IMPROVEMENT Add MethodHub support for methods with more than 2 in ports 
//
// WORK IN PROGRESS
// 2 IMPROVEMENT replace method references with event handlers (BlockX, Group, Port, Connection, Workspace
// 1 IMPROVEMENT save output type to file so connections don't get lost when loading a file
// 3 IMPROVEMENT differentiate between mouse wheel and touch pad, zoom increments should be less big on mac when zooming with a mouse wheel
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

    // Used to test if method reference listeners are removed, and they are not 
    // addListener(this::method) and then removeListener(this::method) will still yield one listener
    public static <T> List<Object> TestNumberOfListeners(ReadOnlyObjectProperty<T> property) {
        List<Object> list = new ArrayList<>();

        try {

//            System.out.println(property.getClass().getSuperclass().getSuperclass().getName());
//            for (Field f : property.getClass().getSuperclass().getSuperclass().getDeclaredFields()) {
//                System.out.println(f.getName());
//            }
            Field helperField = property.getClass().getSuperclass().getSuperclass().getDeclaredField("helper");
            helperField.setAccessible(true);
            Object helper = helperField.get(property);

            // zero listeners
            if (helper == null) {
                System.out.println("LISTENERS 0");
                return list;
            }
            // single listener case
            try {
                Field listenersField = helper.getClass().getDeclaredField("listener");
                listenersField.setAccessible(true);
                Object object = listenersField.get(helper);
                list.add(object);

                System.out.println("LISTENERS 1");
            } catch (Exception e) {

            }
            // multiple listeners case
            try {
                Field listenersField = helper.getClass().getDeclaredField("changeListeners");
                listenersField.setAccessible(true);
                Object object = listenersField.get(helper);
                Object[] array = (Object[]) object; // the number of listeners in the array is not reliable, the sizeField is
                Collections.addAll(list, array);

                Field sizeField = helper.getClass().getDeclaredField("changeSize");
                sizeField.setAccessible(true);
                Object size = sizeField.get(helper);
                System.out.println("LISTENERS " + size);

            } catch (Exception e) {

            }
            try {
                Field listenersField = helper.getClass().getDeclaredField("invalidationListeners");
                listenersField.setAccessible(true);
                Object object = listenersField.get(helper);
                Object[] array = (Object[]) object;
                Collections.addAll(list, array);

            } catch (Exception e) {

            }

//https://stackoverflow.com/questions/37162216/get-all-registered-listeners-to-an-observablevalue
        } catch (Exception e) {

        }
        return list;
    }
}

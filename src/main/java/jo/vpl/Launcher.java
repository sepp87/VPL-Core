package jo.vpl;

import java.util.ArrayList;
import java.util.List;
import jo.vpl.core.HubLoader;
import jo.vpl.core.Util;

// Copy reflection hub
// Cast Json.asList to typed list
// List methods output cant be used in further operations - TODO TEST FIX

/**
 *
 * @author JoostMeulenkamp
 */
public class Launcher {

    public static void main(String[] args) {

        //Load all Hub types
        HubLoader.loadInternalHubs();
        HubLoader.loadExternalHubs();
        HubLoader.loadStaticMethodsAsHubs();

//        TestGetIntegerValue();
//        TestGetDoubleValue();
//        TestGetLongValue();
        if (false) {
            return;
        }
        //Launch the UI
        App.launch(App.class);
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

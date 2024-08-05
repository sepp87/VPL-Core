package jo.vpl;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import jo.vpl.core.HubLoader;

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

        for (Object o : HubLoader.HUB_LIBRARY.values()) {
            if (!o.getClass().equals(Method.class)) {
                continue;
            }
            Method m = (Method) o;
            for (Parameter p : m.getParameters()) {
                System.out.println(p.getName() + " " + p.getType().getSimpleName());
            }

        }

//        if (true) {
//            return;
//        }
        //Launch the UI
        App.launch(App.class);
    }
}

package jo.vpl;

import jo.vpl.core.HubLoader;
import jo.vpl.hub.methods.MathMethods;

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

        Object a = 1.;
        System.out.println(a instanceof Integer);
        
        System.out.println(MathMethods.add(1., 2.).getClass().getSimpleName());
        System.out.println(MathMethods.add(1, 2).getClass().getSimpleName());
        
        if (true) {
            return;
        }
        //Launch the UI
        App.launch(App.class);
    }

}

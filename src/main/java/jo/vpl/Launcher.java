package jo.vpl;

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

        if (true) {
            return;
        }
        //Launch the UI
        App.launch(App.class);
    }

}

package jo.vpl;

import jo.vpl.core.HubLoader;

/**
 *
 * @author JoostMeulenkamp
 */
public class Main {

    public static void main(String[] args) {

        System.out.println(Thread.currentThread().getName());

        //Load all Hub types
        HubLoader.loadInternalHubs();
        HubLoader.loadExternalHubs();

        //Launch the UI
        VplTester.launch(VplTester.class);
    }
}

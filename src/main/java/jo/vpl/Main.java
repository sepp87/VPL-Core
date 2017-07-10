package jo.vpl;

import jo.vpl.core.VplGlobal;

/**
 *
 * @author JoostMeulenkamp
 */
public class Main {

    public static void main(String[] args) {
        
        //Initialize runtime path
        VplGlobal.defineRuntimePath();
        
        //Load all Hub types
        VplGlobal.loadInternalHubs();
        VplGlobal.loadExternalHubs();
        
        //Launch the UI
        VplTester.launch(VplTester.class);
    }
}

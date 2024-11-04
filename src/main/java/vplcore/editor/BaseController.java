package vplcore.editor;

import vplcore.EventRouter;
import vplcore.workspace.ActionManager;

/**
 *
 * @author Joost
 */
public class BaseController {

    private ActionManager actionManager;
    private EventRouter eventRouter;
    public BaseController parent;

    public BaseController() {
    }
    
    public BaseController(BaseController parent) {
        this.parent = parent;
        System.out.println("parent set" + this.parent);
    }

    public ActionManager getActionManager() {
        System.out.println(parent);
        return actionManager != null ? actionManager : parent.getActionManager();
    }

    public EventRouter getEventRouter() {
        return eventRouter != null ? eventRouter : parent.getEventRouter();
    }

    protected final void initializeActionManager(ActionManager actionManager) {
      if(actionManager == null) {
            this.actionManager = actionManager;
        }
    }

    protected final void initializeEventRouter(EventRouter eventRouter) {
        if(eventRouter == null) {
            this.eventRouter = eventRouter;
        }
    }

}

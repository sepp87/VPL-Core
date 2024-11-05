package vplcore.editor;

/**
 *
 * @author Joost
 */
public class BaseController {

    private String contextId;
    private BaseController parent;

    public BaseController(String contextId) {
        this.contextId = contextId;
    }

    public BaseController(BaseController parent) {
        this.parent = parent;
    }

    public String getContextId() {
        return contextId == null ? parent.getContextId() : contextId;
    }

}

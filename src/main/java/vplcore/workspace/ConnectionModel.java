package vplcore.workspace;

/**
 * Extension idea - see PortModel
 *
 * @author Joost
 */
public class ConnectionModel {
    
    private final PortModel sending;
    private final PortModel receiving;
    
    public ConnectionModel(PortModel sending, PortModel receiving) throws IllegalArgumentException {
        this.sending = sending;
        this.receiving = receiving;
        bindData();
    }
    
    private void bindData() {
        sending.dataProperty().bind(receiving.dataProperty());
    }
    
    public void remove() {
        sending.removeConnection(this);
        receiving.removeConnection(this);
        unbindData();
    }
    
    private void unbindData() {
        receiving.dataProperty().unbind();
    }
}

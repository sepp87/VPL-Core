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

    private void onSendingDataChanged(Object b, Object o, Object newData) {
        Object data = copyData(newData);
    }

    private Object copyData(Object data) {
        return data;
    }

    private void bindData() {
        receiving.dataProperty().bind(sending.dataProperty());
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

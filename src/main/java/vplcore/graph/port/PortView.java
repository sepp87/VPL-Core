package vplcore.graph.port;

import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

/**
 *
 * @author JoostMeulenkamp
 */
public class PortView extends VBox {

    private final Tooltip tooltip;
    private final PortType portType;

    public PortView(PortType portType) {
        this.portType = portType;
        this.tooltip = new Tooltip();
        Tooltip.install(this, tooltip);

        getStyleClass().add("port");
        getStyleClass().add("port-" + portType.toString().toLowerCase());
    }

    public Tooltip getTooltip() {
        return tooltip;
    }

    public void setActive(boolean isActive) {
        getStyleClass().removeAll("port", "port-active");
        getStyleClass().add(isActive ? "port-active" : "port");
    }
}

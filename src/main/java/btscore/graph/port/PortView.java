package btscore.graph.port;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;

/**
 *
 * @author JoostMeulenkamp
 */
public class PortView extends VBox {

    private final Tooltip tooltip;
    private final PortType portType;
    private final DoubleProperty centerX = new SimpleDoubleProperty(-1);
    private final DoubleProperty centerY = new SimpleDoubleProperty(-1);

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
    
    public DoubleProperty centerXProperty() {
        return centerX;
    }
    
    public DoubleProperty centerYProperty() {
        return centerY;
    }

 
}

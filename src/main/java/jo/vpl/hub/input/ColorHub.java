package jo.vpl.hub.input;

import jo.vpl.core.Hub;
import jo.vpl.core.VplControl;
import javafx.scene.paint.Color;
import javax.xml.namespace.QName;
import jo.vpl.core.HubInfo;
import jo.vpl.util.CustomColorBox;
import jo.vpl.xml.HubTag;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "In.Color",
        category = "Input",
        description = "Pick a nice color from the palette",
        tags = {"input", "color"})
public class ColorHub extends Hub {

    public ColorHub(VplControl hostCanvas) {
        super(hostCanvas);

        setName("Color Picker");

        addOutPortToHub("color", Color.class);

        CustomColorBox picker = new CustomColorBox();

        outPorts.get(0).dataProperty().bind(picker.customColorProperty());

        addControlToHub(picker);
    }

    public Color getColor() {
        CustomColorBox picker = (CustomColorBox) controls.get(0);
        return picker.customColorProperty().get();
    }

    public void setColor(Color color) {
        CustomColorBox picker = (CustomColorBox) controls.get(0);
        picker.customColorProperty().set(color);
    }

    @Override
    public void calculate() {
    }

    @Override
    public void serialize(HubTag xmlTag) {
        super.serialize(xmlTag);
        xmlTag.getOtherAttributes().put(QName.valueOf("color"), getColor().toString());
    }

    @Override
    public void deserialize(HubTag xmlTag) {
        super.deserialize(xmlTag);
        String color = xmlTag.getOtherAttributes().get(QName.valueOf("color"));
        this.setColor(Color.valueOf(color));
    }

    @Override
    public Hub clone() {
        ColorHub hub = new ColorHub(hostCanvas);
        hub.setColor(this.getColor());
        return hub;
    }
}

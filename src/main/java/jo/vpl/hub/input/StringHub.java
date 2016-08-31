package jo.vpl.hub.input;

import jo.vpl.core.VPLControl;
import jo.vpl.core.Hub;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javax.xml.namespace.QName;
import jo.vpl.core.HubInfo;
import jo.vpl.xml.HubTag;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "In.String",
        category = "Input",
        description = "Input a line of text",
        tags = {"input", "line", "string"})
public class StringHub extends Hub {

    public StringHub(VPLControl hostCanvas) {
        super(hostCanvas);
        setName("String");

        addOutPortToHub("str", String.class);

        TextField text = new TextField();
        text.setPromptText("Write here...");
        text.setFocusTraversable(false);
        text.setMinWidth(100);
        text.setStyle("-fx-pref-column-count: 26;\n"
                + "fx-font-size: 10;\n");

        addControlToHub(text);

        text.setOnKeyReleased(this::textField_KeyRelease);
        this.setOnMouseEntered(this::textField_MouseEnter);
        this.setOnMouseExited(this::textField_MouseExit);
    }

    private void textField_MouseEnter(MouseEvent e) {
        TextField text = (TextField) controls.get(0);
        text.requestFocus();
    }

    private void textField_MouseExit(MouseEvent e) {
        hostCanvas.requestFocus();
    }

    private void textField_KeyRelease(KeyEvent e) {
        calculate();
    }

    public void setString(String str) {
        TextField textField = (TextField) controls.get(0);
        textField.setText(str);
    }

    public String getString() {
        TextField textField = (TextField) controls.get(0);
        return textField.getText();
    }

    @Override
    public void calculate() {
        //Forward empty string as null
        if ("".equals(getString())) {
            outPorts.get(0).setData(null);
        }
        outPorts.get(0).setData(getString());
    }

    @Override
    public void serialize(HubTag xmlTag) {
        super.serialize(xmlTag);
        xmlTag.getOtherAttributes().put(QName.valueOf("string"), getString());
    }

    @Override
    public void deserialize(HubTag xmlTag) {
        super.deserialize(xmlTag);
        String str = xmlTag.getOtherAttributes().get(QName.valueOf("string"));
        this.setString(str);
    }

    @Override
    public Hub clone() {
        StringHub hub = new StringHub(hostCanvas);
        hub.setString(this.getString());
        return hub;
    }

}

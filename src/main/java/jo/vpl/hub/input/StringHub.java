package jo.vpl.hub.input;

import jo.vpl.core.VplControl;
import jo.vpl.core.Hub;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javax.xml.namespace.QName;
import static jo.vpl.core.Util.getBooleanValue;
import static jo.vpl.core.Util.getDoubleValue;
import static jo.vpl.core.Util.getIntegerValue;
import static jo.vpl.core.Util.getLongValue;
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

    public StringHub(VplControl hostCanvas) {
        super(hostCanvas);
        setName("String");

        addOutPortToHub("String : Value", String.class);

        TextField text = new TextField();
        text.setPromptText("Write here...");
        text.setFocusTraversable(false);
        text.setMinWidth(100);
        text.setStyle("-fx-pref-column-count: 26;\n"
                + "fx-font-size: 10;\n");

        addControlToHub(text);

//        text.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
//
//            @Override
//            public void handle(javafx.scene.input.KeyEvent event) {
//                if (event.getCode() == KeyCode.TAB) {
//                    System.out.println("TAB pressed");
//                    event.consume(); // do nothing
//                }
//            }
//        });
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
//        System.out.println(e.getCode());
//        if (e.getCode().equals(KeyCode.TAB)) {
//
//        }
        calculate();
    }

    public void setString(String str) {
        TextField textField = (TextField) controls.get(0);
        textField.setText(str);
        calculate();
    }

    public String getString() {
        TextField textField = (TextField) controls.get(0);
        return textField.getText();
    }

    @Override
    public void calculate() {
        //Forward empty string as null
        if ("".equals(getString())) {
            outPorts.get(0).dataType = String.class;
            outPorts.get(0).setName("String : Value");
            outPorts.get(0).setData(null);
        }

        String str = getString();

        Boolean bool = getBooleanValue(str);
        if (bool != null) {

            //Set outgoing data
            outPorts.get(0).dataType = Boolean.class;
            outPorts.get(0).setName("Boolean : Value");
            outPorts.get(0).setData(bool);
            return;
        }

        Integer integer = getIntegerValue(str);
        if (integer != null) {

            //Set outgoing data
            outPorts.get(0).dataType = Integer.class;
            outPorts.get(0).setName("Integer : Value");
            outPorts.get(0).setData(integer);
            return;
        }

        Long lng = getLongValue(str);
        if (lng != null) {

            //Set outgoing data
            outPorts.get(0).dataType = Long.class;
            outPorts.get(0).setName("Long : Value");
            outPorts.get(0).setData(lng);
            return;
        }

        Double dbl = getDoubleValue(str);
        if (dbl != null) {

            //Set outgoing data
            outPorts.get(0).dataType = Double.class;
            outPorts.get(0).setName("Double : Value");
            outPorts.get(0).setData(dbl);
            return;
        }

        outPorts.get(0).dataType = String.class;
        outPorts.get(0).setName("String : Value");
        outPorts.get(0).setData(getString());
    }

    @Override
    public void serialize(HubTag xmlTag) {
        super.serialize(xmlTag);
        xmlTag.getOtherAttributes().put(QName.valueOf("string"), getString());
        xmlTag.getOtherAttributes().put(QName.valueOf("outDataType"), outPorts.get(0).dataType.getSimpleName());
    }

    @Override
    public void deserialize(HubTag xmlTag) {
        super.deserialize(xmlTag);
        String str = xmlTag.getOtherAttributes().get(QName.valueOf("string"));
        this.setString(str);

        //Retrieval of custom attribute
        String value = xmlTag.getOtherAttributes().get(QName.valueOf("outDataType"));
        switch (value) {
            case "Double":
                outPorts.get(0).dataType = Double.class;
                outPorts.get(0).setName("Double : Value");
                break;

            case "Integer":
                outPorts.get(0).dataType = Integer.class;
                outPorts.get(0).setName("Integer : Value");
                break;

            case "Long":
                outPorts.get(0).dataType = Long.class;
                outPorts.get(0).setName("Long : Value");
                break;

            case "Boolean":
                outPorts.get(0).dataType = Boolean.class;
                outPorts.get(0).setName("Boolean : Value");
                break;

            case "String":
                outPorts.get(0).dataType = String.class;
                outPorts.get(0).setName("String : Value");
                break;

        }
        calculate();
    }

    @Override
    public Hub clone() {
        StringHub hub = new StringHub(hostCanvas);
        System.out.println("found " + this.getString());
        hub.setString(this.getString());
        return hub;
    }

}

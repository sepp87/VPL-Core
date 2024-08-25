package vpllib.input;

import vplcore.workspace.Workspace;
import vplcore.graph.model.Block;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javax.xml.namespace.QName;
import static vplcore.Util.getBooleanValue;
import static vplcore.Util.getDoubleValue;
import static vplcore.Util.getIntegerValue;
import static vplcore.Util.getLongValue;
import jo.vpl.xml.BlockTag;
import vplcore.graph.model.BlockInfo;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockInfo(
        identifier = "Input.string",
        category = "Input",
        description = "Input a line of text",
        tags = {"input", "line", "string"})
public class StringBlock extends Block {

    public StringBlock(Workspace hostCanvas) {
        super(hostCanvas);
        setName("String");

        addOutPortToBlock("String : Value", String.class);

        TextField text = new TextField();
        text.setPromptText("Write here...");
        text.setFocusTraversable(false);
        text.setMinWidth(100);
        text.setStyle("-fx-pref-column-count: 26;\n"
                + "fx-font-size: 10;\n");

        addControlToBlock(text);

        text.setOnKeyReleased(this::textField_KeyRelease);
        this.setOnMouseEntered(this::textField_MouseEnter);
        this.setOnMouseExited(this::textField_MouseExit);

//        outPorts.get(0).setData(null);
    }

    private void textField_MouseEnter(MouseEvent e) {
        TextField text = (TextField) controls.get(0);
        text.requestFocus();
    }

    private void textField_MouseExit(MouseEvent e) {
        workspace.requestFocus();
    }

    private void textField_KeyRelease(KeyEvent e) {
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
        String str = getString();

        //Forward empty string as null
        if (str.equals("")) {
            outPorts.get(0).dataType = String.class;
            outPorts.get(0).setName("String : Value");
            outPorts.get(0).setData(null);
            return;
        }

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
        outPorts.get(0).setData(str);
    }

    @Override
    public void serialize(BlockTag xmlTag) {
        super.serialize(xmlTag);
        xmlTag.getOtherAttributes().put(QName.valueOf("string"), getString());
        xmlTag.getOtherAttributes().put(QName.valueOf("outDataType"), outPorts.get(0).dataType.getSimpleName());
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
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
    public Block clone() {
        StringBlock block = new StringBlock(workspace);
        block.setString(this.getString());
        return block;
    }

}

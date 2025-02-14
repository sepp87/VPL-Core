package vpllib.input;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import vplcore.workspace.WorkspaceController;
import vplcore.graph.model.Block;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javax.xml.namespace.QName;
import static vplcore.util.DataParsingUtils.getBooleanValue;
import static vplcore.util.DataParsingUtils.getDoubleValue;
import static vplcore.util.DataParsingUtils.getIntegerValue;
import static vplcore.util.DataParsingUtils.getLongValue;
import jo.vpl.xml.BlockTag;
import vplcore.graph.model.BlockMetadata;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockMetadata(
        identifier = "Input.string",
        category = "Input",
        description = "Input a line of text",
        tags = {"input", "line", "string"})
public class StringBlock extends Block {

    private final TextField text;

    private final ChangeListener<String> calculateOnChangeHandler = this::handleCalculateOnChange;
//    private final EventHandler<KeyEvent> keyReleasedHandler = createKeyReleasedHandler();

    public StringBlock(WorkspaceController hostCanvas) {
        super(hostCanvas);
        setName("String");

        addOutPortToBlock("String : Value", String.class);

        text = new TextField();
        text.setPromptText("Write here...");
        text.setFocusTraversable(false);
        text.setMinWidth(100);
        text.setStyle("-fx-pref-column-count: 26;\n"
                + "fx-font-size: 10;\n");

        addControlToBlock(text);

        text.textProperty().addListener(calculateOnChangeHandler);
        text.setOnKeyPressed(this::blockShortcuts);
        this.setOnMouseEntered(this::focusOnTextField);
    }

    private void handleCalculateOnChange(Object b, Object o, Object n) {
        calculate();
    }
    
    private void blockShortcuts(KeyEvent event) {
        event.consume();
    }

    private void focusOnTextField(MouseEvent event) {
        text.requestFocus();
    }

    public void setString(String str) {
        text.setText(str);
        calculate();
    }

    public String getString() {
        return text.getText();
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
        StringBlock block = new StringBlock(workspaceController);
        block.setString(this.getString());
        return block;
    }

}

package vpllib.input;

import java.time.LocalDate;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javax.xml.namespace.QName;
import static vplcore.util.DataParsingUtils.getBooleanValue;
import static vplcore.util.DataParsingUtils.getDoubleValue;
import static vplcore.util.DataParsingUtils.getIntegerValue;
import static vplcore.util.DataParsingUtils.getLongValue;
import jo.vpl.xml.BlockTag;
import vplcore.graph.block.BlockModel;
import vplcore.workspace.WorkspaceModel;
import vplcore.graph.block.BlockMetadata;
import vplcore.util.DateTimeUtils;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockMetadata(
        identifier = "Input.string",
        category = "Input",
        description = "Input a line of text",
        tags = {"input", "line", "string"})
public class StringBlock extends BlockModel {

    private final StringProperty string = new SimpleStringProperty();
    private TextField textField;

    public StringBlock(WorkspaceModel workspace) {
        super(workspace);
        this.nameProperty().set("String");
        addOutputPort("Value", String.class);
        initialize();
    }

    @Override
    protected final void initialize() {
        string.addListener(stringListener);
    }

    @Override
    public Region getCustomization() {
        textField = new TextField();
        textField.setPromptText("Write here...");
        textField.setFocusTraversable(false);
        textField.setMinWidth(100);
        textField.setStyle(
                "-fx-pref-column-count: 26;\n"
                + "fx-font-size: 10;\n");
        textField.textProperty().bindBidirectional(string);
        textField.setOnKeyPressed(this::ignoreShortcuts);
        return textField;
    }

    @Override
    public EventHandler<MouseEvent> onMouseEntered() {
        return this::focusOnTextField;
    }

    private final ChangeListener<String> stringListener = this::onStringChanged;

    private void onStringChanged(Object b, Object o, Object n) {
        processSafely();
    }

    private void ignoreShortcuts(KeyEvent event) {
        event.consume();
    }

    private void focusOnTextField(MouseEvent event) {
        textField.requestFocus();
    }

    @Override
    public void process() {
        String str = string.get();

        //Forward null and empty string as null
        if (str == null || str.equals("")) {
            outputPorts.get(0).dataTypeProperty().set(String.class);
            outputPorts.get(0).setData(null);
            return;
        }

        Boolean bool = getBooleanValue(str);
        if (bool != null) {

            //Set outgoing data
            outputPorts.get(0).dataTypeProperty().set(Boolean.class);
            outputPorts.get(0).setData(bool);
            return;
        }

        Integer integer = getIntegerValue(str);
        if (integer != null) {

            //Set outgoing data
            outputPorts.get(0).dataTypeProperty().set(Integer.class);
            outputPorts.get(0).setData(integer);
            return;
        }

        Long lng = getLongValue(str);
        if (lng != null) {

            //Set outgoing data
            outputPorts.get(0).dataTypeProperty().set(Long.class);
            outputPorts.get(0).setData(lng);
            return;
        }

        Double dbl = getDoubleValue(str);
        if (dbl != null) {

            //Set outgoing data
            outputPorts.get(0).dataTypeProperty().set(Double.class);
            outputPorts.get(0).setData(dbl);
            return;
        }

        LocalDate date = DateTimeUtils.getLocalDateFrom(str);
        if (date != null) {

            //Set outgoing data
            outputPorts.get(0).dataTypeProperty().set(LocalDate.class);
            outputPorts.get(0).setData(date);
            return;
        }

        outputPorts.get(0).dataTypeProperty().set(String.class);
        outputPorts.get(0).setData(str);
    }

    @Override
    public void serialize(BlockTag xmlTag) {
        super.serialize(xmlTag);
        String str = string.get() != null ? string.get() : "";
        xmlTag.getOtherAttributes().put(QName.valueOf("string"), str);
        xmlTag.getOtherAttributes().put(QName.valueOf("outDataType"), outputPorts.get(0).getDataType().getSimpleName());
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
        String str = xmlTag.getOtherAttributes().get(QName.valueOf("string"));
        str = !str.isEmpty() ? str : null;
        string.set(str);

        //Retrieval of custom attribute
        String value = xmlTag.getOtherAttributes().get(QName.valueOf("outDataType"));
        switch (value) {
            case "Double":
                outputPorts.get(0).dataTypeProperty().set(Double.class);
                break;

            case "Integer":
                outputPorts.get(0).dataTypeProperty().set(Integer.class);
                break;

            case "Long":
                outputPorts.get(0).dataTypeProperty().set(Long.class);
                break;

            case "Boolean":
                outputPorts.get(0).dataTypeProperty().set(Boolean.class);
                break;

            case "String":
                outputPorts.get(0).dataTypeProperty().set(String.class);
                break;

            case "LocalDate":
                outputPorts.get(0).dataTypeProperty().set(LocalDate.class);
                break;

        }
        processSafely();
    }

    @Override
    public BlockModel copy() {
        StringBlock block = new StringBlock(workspace);
        block.string.set(this.string.get());
        return block;
    }

    @Override
    public void onRemoved() {
        string.removeListener(stringListener);
        if (textField != null) {
            textField.textProperty().unbindBidirectional(string);
            textField.setOnKeyPressed(null);
        }
    }

}

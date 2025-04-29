package btslib.input;

import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javax.xml.namespace.QName;
import btsxml.BlockTag;
import btscore.graph.block.BlockModel;
import btscore.graph.block.BlockMetadata;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockMetadata(
        identifier = "Input.text",
        category = "Input",
        description = "Input text or observe output as text",
        tags = {"input", "panel", "text"})
public class TextBlock extends BlockModel {

    private final StringProperty string = new SimpleStringProperty();
    private final BooleanProperty editable = new SimpleBooleanProperty();
    private TextArea textArea;

    public TextBlock() {
        nameProperty().set("Panel");
        resizableProperty().set(true);
        addInputPort("any", Object.class);
        addOutputPort("String", String.class);
        initialize();

    }

    @Override
    protected final void initialize() {
        string.addListener(stringListener);
        editable.bind(inputPorts.get(0).activeProperty().not());
        editable.addListener(editableListener);
    }

    @Override
    public Region getCustomization() {
        textArea = new TextArea();
        textArea.setMinSize(220, 220);
        textArea.setPrefSize(220, 220);
        textArea.textProperty().bindBidirectional(string);
        textArea.setOnKeyPressed(this::ignoreShortcuts);
        textArea.editableProperty().bind(editable); // set text area to editable when there is no active connection
        return textArea;
    }

    @Override
    public EventHandler<MouseEvent> onMouseEntered() {
        return this::focusOnTextArea;
    }

    private final ChangeListener<String> stringListener = this::onStringChanged;

    private void onStringChanged(Object b, Object o, Object n) {
        if (editable.get()) {
            processSafely();
        }
    }

    private void ignoreShortcuts(KeyEvent event) {
        event.consume();
    }

    private void focusOnTextArea(MouseEvent event) {
        textArea.requestFocus();
    }

    private final ChangeListener<Boolean> editableListener = (b, o, n) -> onEditable();

    private void onEditable() {
        if (editable.get()) {
            string.set(null);
        }
    }

    /**
     * Print whatever is incoming
     */
    @Override
    public void process() {
//        System.out.println("editable " + editable.get() + "; textAreaExists " + (textArea != null) + "; data " + inputPorts.get(0).getData() + "; portActive " + inputPorts.get(0).activeProperty().get());

        // If data is text input by user
        if (editable.get()) {
            outputPorts.get(0).setData(string.get());
            return;
        }

        // If data is from an incoming connection
        Object data = inputPorts.get(0).getData();
        outputPorts.get(0).setData(data);

        // Update textArea if available
        if (textArea == null) {
            return;
        }

        textArea.setText("");
//        System.out.println("editable " + editable.get() + "; textAreaExists " + (textArea != null) + "; data " + inputPorts.get(0).getData() + "; portActive " + inputPorts.get(0).activeProperty().get());

        //Do Action
        if (data != null) {

            //Set data type corresponding to source
            outputPorts.get(0).dataTypeProperty().set(inputPorts.get(0).getConnections().iterator().next().getStartPort().getDataType());
            outputPorts.get(0).nameProperty().set(inputPorts.get(0).getConnections().iterator().next().getStartPort().nameProperty().get());
            if (data instanceof List) {
                List list = (List) data;
                String value = "";

                for (Object object : list) {
                    if (object == null) {
                        value += "null\n";
                    } else {
                        value += object.toString() + "\n";
                    }
                }
                textArea.setText(value);
            } else {
                textArea.setText(data.toString());
            }
        } else {
            //Set data type back to string
            outputPorts.get(0).dataTypeProperty().set(String.class);
            outputPorts.get(0).nameProperty().set("String");
            if (inputPorts.get(0).activeProperty().get()) {
                textArea.setText("null");
            } else {
                textArea.setText("");
            }
        }
    }

    @Override
    public void serialize(BlockTag xmlTag) {
        super.serialize(xmlTag);
        String text = "";
        if (this.textArea.isEditable()) {
            text = textArea.getText();
        }
        xmlTag.getOtherAttributes().put(QName.valueOf("text"), text);
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
        String str = xmlTag.getOtherAttributes().get(QName.valueOf("text"));
        this.string.set(str);
        // Process is triggered by string's change listener, meaning outgoing data is set automatically
    }

    @Override
    public BlockModel copy() {
        TextBlock block = new TextBlock();
        block.widthProperty().set(this.widthProperty().get());
        block.heightProperty().set(this.heightProperty().get());
        if (editable.get()) {
            block.string.set(this.string.get());
        }
        return block;
    }

    @Override
    public void onRemoved() {
        string.removeListener(stringListener);
        editable.unbind();
        editable.removeListener(editableListener);
        if (textArea != null) {
            textArea.textProperty().unbindBidirectional(string);
            textArea.setOnKeyPressed(null);
            textArea.editableProperty().unbind();
        }
    }

}

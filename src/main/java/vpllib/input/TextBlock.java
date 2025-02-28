package vpllib.input;

import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javax.xml.namespace.QName;
import jo.vpl.xml.BlockTag;
import vplcore.graph.block.BlockModel;
import vplcore.graph.port.PortModel;
import vplcore.workspace.WorkspaceModel;
import vplcore.graph.block.BlockMetadata;

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

    public TextBlock(WorkspaceModel workspace) {
        super(workspace);
        nameProperty().set("Panel");
        resizableProperty().set(true);
        PortModel input = addInputPort("Object", Object.class);
        addOutputPort("String", String.class);
        string.addListener(stringListener);
        editable.bind(input.activeProperty().not());
    }

    @Override
    public Region getCustomization() {
        textArea = new TextArea();
        textArea.setMinSize(220, 220);
        textArea.setPrefSize(220, 220);
        textArea.layoutBoundsProperty().addListener(layoutBoundsListener);
        textArea.textProperty().bindBidirectional(string);
        textArea.setOnKeyPressed(this::ignoreShortcuts);
        textArea.editableProperty().bind(editable); // set text area to editable when there is no active connection
        return textArea;
    }

    ChangeListener layoutBoundsListener = this::onLayoutBoundsChanged;

    private void onLayoutBoundsChanged(Object b, Object o, Object n) {
        ScrollBar scrollBarv = (ScrollBar) textArea.lookup(".scroll-bar:vertical");
        scrollBarv.setDisable(true);
        ScrollPane pane = (ScrollPane) textArea.lookup(".scroll-pane");
        pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }

    @Override
    public EventHandler<MouseEvent> onMouseEntered() {
        return this::focusOnTextArea;
    }

    private final ChangeListener<String> stringListener = this::onStringChanged;

    private void onStringChanged(Object b, Object o, Object n) {
        processSafely();
    }

    private void ignoreShortcuts(KeyEvent event) {
        event.consume();
    }

    private void focusOnTextArea(MouseEvent event) {
        textArea.requestFocus();
    }

    @Override
    public void onIncomingConnectionRemoved() {
        string.set(null);
    }

    /**
     * Print whatever is incoming
     */
    @Override
    public void process() {
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

        //Do Action
        if (inputPorts.get(0).getData() != null) {
            //Set data type corresponding to source
            outputPorts.get(0).dataTypeProperty().set(inputPorts.get(0).getConnections().iterator().next().getStartPort().getDataType());
            outputPorts.get(0).nameProperty().set(inputPorts.get(0).getConnections().iterator().next().getStartPort().nameProperty().get());
            if (data instanceof List) {
                List list = (List) data;

                for (Object object : list) {
                    if (object == null) {
                        textArea.appendText("null\n");
                    } else {
                        textArea.appendText(object.toString() + "\n");
                    }
                }
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
        TextBlock block = new TextBlock(workspace);
        block.widthProperty().set(this.widthProperty().get());
        block.heightProperty().set(this.heightProperty().get());
        if (editable.get()) {
            block.string.set(this.string.get());
        }
        return block;
    }

    @Override
    public void remove() {
        super.remove();
        string.removeListener(stringListener);
        editable.unbind();
        if (textArea != null) {
            textArea.layoutBoundsProperty().removeListener(layoutBoundsListener);
            textArea.textProperty().unbindBidirectional(string);
            textArea.setOnKeyPressed(null);
            textArea.editableProperty().unbind();
        }
    }

}

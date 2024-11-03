package vpllib.input;

import java.util.Arrays;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import vplcore.graph.model.Block;
import vplcore.workspace.WorkspaceController;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javax.xml.namespace.QName;
import vplcore.graph.model.Port;
import jo.vpl.xml.BlockTag;
import vplcore.graph.model.BlockInfo;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockInfo(
        identifier = "Input.text",
        category = "Input",
        description = "Input text or observe output as text",
        tags = {"input", "panel", "text"})
public class TextBlock extends Block {

    private final TextArea textArea;

    private final EventHandler<KeyEvent> textAreaKeyReleasedHandler = this::handleTextAreaKeyReleased;
    private final EventHandler<MouseEvent> blockEnteredHandler = this::handleBlockEntered;
    private final EventHandler<MouseEvent> blockExitedHandler = this::handleBlockExited;
    private final ChangeListener<Object> blockIncomingDataListener = this::handleBlockIncomingData;

    public TextBlock(WorkspaceController workspace) {
        super(workspace);
        setName("Panel");
        setResizable(true);

        addInPortToBlock("Object", Object.class);
        addOutPortToBlock("String", String.class);

        textArea = new TextArea();

        textArea.layoutBoundsProperty().addListener(e -> {
            ScrollBar scrollBarv = (ScrollBar) textArea.lookup(".scroll-bar:vertical");
            scrollBarv.setDisable(true);

            ScrollPane pane = (ScrollPane) textArea.lookup(".scroll-pane");
            pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        });

        addControlToBlock(textArea);

        inPorts.get(0).activeProperty().addListener(blockIncomingDataListener);

        contentGrid.setMinSize(220, 220);
        contentGrid.setPrefSize(220, 220);

        textArea.setOnKeyReleased(textAreaKeyReleasedHandler);
        this.setOnMouseEntered(blockEnteredHandler);
        this.setOnMouseExited(blockExitedHandler);
    }

    private void handleTextAreaKeyReleased(KeyEvent keyEvent) {
        String text = textArea.getText();
        this.setTextToData(text);
    }

    @Override
    protected void handleBlockEntered(MouseEvent event) {
        super.handleBlockEntered(event);
        textArea.requestFocus();
    }

    private void handleBlockIncomingData(ObservableValue obj, Object oldVal, Object isActive) {
        //Do Action
        if ((boolean) isActive) {
            textArea.setEditable(false);
            //          calculate();
        } else {
            textArea.setEditable(true);
//            calculate();
        }
    }

    public boolean setText(String text) {
        //Text can only be set when the text area is editable
        if (this.textArea.isEditable()) {
            this.textArea.setText(text);
            this.setTextToData(text);
            return true;
        } else {
            return false;
        }
    }

    //No text is null, multiple lines is a list, one line is a string
    private void setTextToData(String text) {
        Object data = null;
        if (text.contains("\n")) {
            data = Arrays.asList(text.split("\n"));
        } else if (!text.equals("")) {
            data = text;
        }
        outPorts.get(0).setData(data);
    }

    @Override
    public void handleIncomingConnectionRemoved(Port source) {
        textArea.setText("");
        textArea.setEditable(true);
        outPorts.get(0).setData(null);
    }

    /**
     * Print whatever is incoming
     */
    @Override
    public void calculate() {
        //Get controls and data
        Object data = inPorts.get(0).getData();
        textArea.setText("");

        //Do Action
        if (inPorts.get(0).getData() != null) {
            //Set data type corresponding to source
            outPorts.get(0).dataType = inPorts.get(0).connectedConnections.get(0).getStartPort().dataType;
            outPorts.get(0).setName(inPorts.get(0).connectedConnections.get(0).getStartPort().getName());
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
            outPorts.get(0).dataType = String.class;
            outPorts.get(0).setName("String");
            if (inPorts.get(0).isActive()) {
                textArea.setText("null");
            } else {
                textArea.setText("");
            }
        }

        //Set Data
        outPorts.get(0).setData(data);
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
        this.setText(str);
        //Set Data
        outPorts.get(0).setData(str);
    }

    @Override
    public Block clone() {
        TextBlock block = new TextBlock(workspaceController);
        if (textArea.isEditable()) {
            block.setText(textArea.getText());
        }
        return block;
    }
}

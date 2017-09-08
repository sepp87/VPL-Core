package jo.vpl.hub.input;

import java.util.Arrays;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import jo.vpl.core.Hub;
import jo.vpl.core.VplControl;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javax.xml.namespace.QName;
import jo.vpl.core.HubInfo;
import jo.vpl.core.Port;
import jo.vpl.xml.HubTag;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "In.Text",
        category = "Input",
        description = "Input text or observe output as text",
        tags = {"input", "panel", "text"})
public class TextHub extends Hub {

    boolean editable = true;

    public TextHub(VplControl hostCanvas) {
        super(hostCanvas);
        setName("Panel");
        setResizable(true);

        addInPortToHub("Object", Object.class);
        addOutPortToHub("String", String.class);

        TextArea area = new TextArea();

        area.layoutBoundsProperty().addListener(e -> {
            ScrollBar scrollBarv = (ScrollBar) area.lookup(".scroll-bar:vertical");
            scrollBarv.setDisable(true);

            ScrollPane pane = (ScrollPane) area.lookup(".scroll-pane");
            pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            
//            Text text = (Text) area.lookup(".text");
//            text.setLineSpacing(7);
        });

        addControlToHub(area);

        inPorts.get(0).activeProperty().addListener(this::handle_IncomingData);

        area.setOnKeyReleased(this::textArea_KeyRelease);
        this.setOnMouseEntered(this::textArea_MouseEnter);
        this.setOnMouseExited(this::textArea_MouseExit);
    }

    private void textArea_MouseEnter(MouseEvent e) {
        TextArea text = (TextArea) controls.get(0);
        text.requestFocus();
    }

    private void textArea_MouseExit(MouseEvent e) {
        hostCanvas.requestFocus();
    }

    private void textArea_KeyRelease(KeyEvent e) {
        String text = getText();
        this.setTextToData(text);
    }

    private void handle_IncomingData(ObservableValue obj, Object oldVal, Object isActive) {
        //Get controls
        TextArea area = (TextArea) controls.get(0);

        //Do Action
        if ((boolean) isActive) {
            area.setEditable(false);
  //          calculate();
        } else {
            area.setEditable(true);
//            calculate();
        }
    }

    public String getText() {
        TextArea area = (TextArea) controls.get(0);
        return area.getText();
    }

    public boolean setText(String text) {
        //Text can only be set when the text area is editable
        TextArea area = (TextArea) controls.get(0);
        if (area.isEditable()) {
            area.setText(text);
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
    public void handle_IncomingConnectionRemoved(Port source) {
        outPorts.get(0).setData(null);
    }

    
    
    /**
     * Print whatever is incoming
     */
    @Override
    public void calculate() {
        //Get controls and data
        TextArea area = (TextArea) controls.get(0);
        Object data = inPorts.get(0).getData();
        area.setText("");

        //Do Action
        if (inPorts.get(0).getData() != null) {
            //Set data type corresponding to source
            outPorts.get(0).dataType = inPorts.get(0).connectedConnections.get(0).getStartPort().dataType;
            outPorts.get(0).setName(inPorts.get(0).connectedConnections.get(0).getStartPort().getName());
            if (data instanceof List) {
                List list = (List) data;

                for (Object object : list) {
                    if (object == null) {
                        area.appendText("null" + "\n");
                    } else {
                        area.appendText(object.toString() + "\n");
                    }
                }
            } else {
                area.setText(data.toString());
            }
        } else {
            //Set data type back to string
            outPorts.get(0).dataType = String.class;
            outPorts.get(0).setName("String");
            if (inPorts.get(0).isActive()) {
                area.setText("null");
            } else {
                area.setText("");
            }
        }

        //Set Data
        outPorts.get(0).setData(data);

//        System.out.println(outPorts.get(0).dataType);
    }

    @Override
    public void serialize(HubTag xmlTag) {
        super.serialize(xmlTag);
        String text = "";
        TextArea area = (TextArea) controls.get(0);
        if (area.isEditable()) {
            text = getText();
        }
        xmlTag.getOtherAttributes().put(QName.valueOf("text"), text);
    }

    @Override
    public void deserialize(HubTag xmlTag) {
        super.deserialize(xmlTag);
        String str = xmlTag.getOtherAttributes().get(QName.valueOf("text"));
        this.setText(str);
        //Set Data
        outPorts.get(0).setData(str);
    }

    @Override
    public Hub clone() {
        TextHub hub = new TextHub(hostCanvas);
        TextArea area = (TextArea) controls.get(0);
        if (area.isEditable()) {
            hub.setText(this.getText());
        }
        return hub;
    }
}

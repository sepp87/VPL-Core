package jo.vpl.hub.input;

import jo.vpl.core.Hub;
import jo.vpl.core.VplControl;
import java.io.File;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javax.xml.namespace.QName;
import jo.vpl.core.HubButton;
import jo.vpl.core.HubInfo;
import jo.vpl.util.IconType;
import jo.vpl.xml.HubTag;

/**
 *
 * @author JoostMeulenkamp
 */
@HubInfo(
        name = "In.File",
        category = "Input",
        description = "Open a file",
        tags = {"file", "open", "load"}
)
public class FileHub extends Hub {

    public FileHub(VplControl hostCanvas) {
        super(hostCanvas);
        setName("File");

        addOutPortToHub("file", File.class);

        TextField text = new TextField();
        text.setPromptText("Open a file...");
        text.setFocusTraversable(false);

        HubButton button = new HubButton(IconType.FA_FOLDER_OPEN);
        button.setOnAction(this::button_openFile);

        HBox box = new HBox(5);
        box.getChildren().addAll(text, button);
        addControlToHub(box);

        text.setOnKeyReleased(this::textField_KeyRelease);
        text.setOnMouseEntered(this::textField_MouseEnter);
    }

    private void textField_KeyRelease(KeyEvent e) {
        calculate();
    }

    private void textField_MouseEnter(MouseEvent e) {
        HBox box = (HBox) controls.get(0);
        TextField textField = (TextField) box.getChildren().get(0);
        textField.requestFocus();
    }

    private void button_openFile(ActionEvent e) {

        //Do Action
        FileChooser picker = new FileChooser();
        picker.setTitle("Choose a file...");
        File file = picker.showOpenDialog(getScene().getWindow());

        //Set Data
        if (file != null) {
            String path = file.getPath();
            setPath(path);
            outPorts.get(0).setData(file);
        } else {
            outPorts.get(0).setData(null);
        }
    }

    public void setPath(String path) {
        HBox box = (HBox) controls.get(0);
        TextField textField = (TextField) box.getChildren().get(0);
        textField.setText(path);
    }

    public String getPath() {
        HBox box = (HBox) controls.get(0);
        TextField textField = (TextField) box.getChildren().get(0);
        return textField.getText();
    }

    @Override
    public void calculate() {
        //Do Action
        String path = getPath();
        File newFile = new File(path);

        //Set Data
        if (newFile.exists() && newFile.isFile()) {
            outPorts.get(0).setData(newFile);
        } else {
            outPorts.get(0).setData(null);
        }
    }

    @Override
    public void serialize(HubTag xmlTag) {
        super.serialize(xmlTag);
        xmlTag.getOtherAttributes().put(QName.valueOf("path"), getPath());
    }

    @Override
    public void deserialize(HubTag xmlTag) {
        super.deserialize(xmlTag);
        String path = xmlTag.getOtherAttributes().get(QName.valueOf("path"));
        this.setPath(path);
        this.calculate();
    }

    @Override
    public Hub clone() {
        FileHub hub = new FileHub(hostCanvas);
        hub.setPath(this.getPath());
        hub.calculate();
        return hub;
    }
}

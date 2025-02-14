package vpllib.input;

import vplcore.graph.model.Block;
import vplcore.workspace.WorkspaceController;
import java.io.File;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javax.xml.namespace.QName;
import vplcore.graph.model.VplButton;
import vplcore.IconType;
import jo.vpl.xml.BlockTag;
import vplcore.graph.model.BlockMetadata;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockMetadata(
        identifier = "Input.file",
        category = "Input",
        description = "Open a file",
        tags = {"file", "open", "load"}
)
public class FileBlock extends Block {

    private final TextField textField;

    private final EventHandler<ActionEvent> openFileHandler = this::handleOpenFile;
    private final EventHandler<KeyEvent> textFieldKeyReleasedHandler = this::handleTextFieldKeyReleased;
    private final EventHandler<MouseEvent> textFieldEnteredHandler = this::handleTextFieldMouseEntered;

    public FileBlock(WorkspaceController hostCanvas) {
        super(hostCanvas);
        setName("File");

        addOutPortToBlock("file", File.class);

        textField = new TextField();
        textField.setPromptText("Open a file...");
        textField.setFocusTraversable(false);

        VplButton button = new VplButton(IconType.FA_FOLDER_OPEN);
        button.setOnAction(openFileHandler);

        HBox box = new HBox(5);
        box.getChildren().addAll(textField, button);
        addControlToBlock(box);

        textField.setOnKeyReleased(textFieldKeyReleasedHandler);
        textField.setOnMouseEntered(textFieldEnteredHandler);
    }

    private void handleTextFieldKeyReleased(KeyEvent keyEvent) {
        calculate();
    }

    private void handleTextFieldMouseEntered(MouseEvent event) {
        textField.requestFocus();
    }

    private void handleOpenFile(ActionEvent event) {

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
        textField.setText(path);
    }

    public String getPath() {
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
    public void serialize(BlockTag xmlTag) {
        super.serialize(xmlTag);
        xmlTag.getOtherAttributes().put(QName.valueOf("path"), getPath());
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
        String path = xmlTag.getOtherAttributes().get(QName.valueOf("path"));
        this.setPath(path);
        this.calculate();
    }

    @Override
    public Block clone() {
        FileBlock block = new FileBlock(workspaceController);
        block.setPath(this.getPath());
        block.calculate();
        return block;
    }
}

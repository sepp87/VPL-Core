package vpllib.input;

import java.io.File;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javax.xml.namespace.QName;
import vplcore.graph.model.VplButton;
import vplcore.IconType;
import jo.vpl.xml.BlockTag;
import vplcore.graph.model.BlockMetadata;
import vplcore.workspace.BlockModel;
import vplcore.workspace.WorkspaceModel;

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
public class FileBlockNew extends BlockModel {

    private final StringProperty path = new SimpleStringProperty();
    
    private VplButton button;
    private TextField textField;

    private final EventHandler<ActionEvent> openFileHandler = this::handleOpenFile;
    private final EventHandler<KeyEvent> textFieldKeyReleasedHandler = this::handleTextFieldKeyReleased;
    private final EventHandler<MouseEvent> textFieldEnteredHandler = this::handleTextFieldMouseEntered;

    public FileBlockNew(WorkspaceModel workspaceModel) {
        super(workspaceModel);
        this.nameProperty().set("File");
        addOutputPort("file", File.class);
    }

    @Override
    public Region getCustomization() {
        textField = new TextField();
        textField.setPromptText("Open a file...");
        textField.setFocusTraversable(false);

        button = new VplButton(IconType.FA_FOLDER_OPEN);
        button.setOnAction(openFileHandler);

        HBox box = new HBox(5);
        box.getChildren().addAll(textField, button);

        textField.setOnKeyReleased(textFieldKeyReleasedHandler);
        textField.setOnMouseEntered(textFieldEnteredHandler);
        return box;
    }

    private void handleTextFieldKeyReleased(KeyEvent keyEvent) {
        process();
    }

    private void handleTextFieldMouseEntered(MouseEvent event) {
        textField.requestFocus();
    }

    private void handleOpenFile(ActionEvent event) {

        //Do Action
        FileChooser picker = new FileChooser();
        picker.setTitle("Choose a file...");
        Window window = button.getScene().getWindow();
        File file = picker.showOpenDialog(window);

        //Set Data
        if (file != null) {
            String path = file.getPath();
            setPath(path);
            outputPorts.get(0).setData(file);
        } else {
            outputPorts.get(0).setData(null);
        }
    }
    
    public StringProperty pathProperty() {
        return path;
    }

    public void setPath(String path) {
        textField.setText(path);
    }

    public String getPath() {
        return textField.getText();
    }

    @Override
    public void process() {
        //Do Action
        String path = getPath();
        File newFile = new File(path);

        //Set Data
        if (newFile.exists() && newFile.isFile()) {
            outputPorts.get(0).setData(newFile);
        } else {
            outputPorts.get(0).setData(null);
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
        this.process();
    }

    @Override
    public BlockModel copy() {
        FileBlockNew block = new FileBlockNew(workspace);
        block.setPath(this.getPath());
        block.process();
        return block;
    }

}

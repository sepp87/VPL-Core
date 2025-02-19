package vpllib.input;

import java.io.File;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
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
public class FileBlock extends BlockModel {

    private final StringProperty path = new SimpleStringProperty();

    private VplButton button;
    private TextField textField;

//    private final EventHandler<KeyEvent> textFieldKeyReleasedHandler = this::handleTextFieldKeyReleased;
    public FileBlock(WorkspaceModel workspaceModel) {
        super(workspaceModel);
        this.nameProperty().set("File");
        addOutputPort("file", File.class);
        path.addListener(pathListener);
//        outputPorts.get(0).dataProperty().bind(path);
    }

    @Override
    public Region getCustomization() {
        textField = new TextField();
        textField.setPromptText("Open a file...");
        textField.setFocusTraversable(false);

        button = new VplButton(IconType.FA_FOLDER_OPEN);
        button.setOnAction(this::handleOpenFile);

        HBox box = new HBox(5);
        box.getChildren().addAll(textField, button);

//        textField.setOnKeyReleased(textFieldKeyReleasedHandler);
        textField.setOnMouseEntered(this::focusOnTextField);
        textField.textProperty().bindBidirectional(path);
        return box;
    }

    ChangeListener<String> pathListener = this::onPathChanged;

    private void onPathChanged(Object b, String o, String n) {
        File file = new File(n);
        outputPorts.get(0).setData(file);
    }

//    private void handleTextFieldKeyReleased(KeyEvent keyEvent) {
//        process();
//    }
    private void focusOnTextField(MouseEvent event) {
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
            String filePath = file.getPath();
            this.path.set(filePath);
        } else {
            this.path.set(null);
            outputPorts.get(0).setData(null);
        }
    }

    public StringProperty pathProperty() {
        return path;
    }

//    public void setPath(String path) {
//        textField.setText(path);
//    }
//
//    public String getPath() {
//        return textField.getText();
//    }
    @Override
    public void process() {
//        //Do Action
//        String path = getPath();
//        File newFile = new File(path);
//
//        //Set Data
//        if (newFile.exists() && newFile.isFile()) {
//            outputPorts.get(0).setData(newFile);
//        } else {
//            outputPorts.get(0).setData(null);
//        }
    }

    @Override
    public void serialize(BlockTag xmlTag) {
        super.serialize(xmlTag);
        xmlTag.getOtherAttributes().put(QName.valueOf("path"), path.get());
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
        String filePath = xmlTag.getOtherAttributes().get(QName.valueOf("path"));
        this.path.set(filePath);
    }

    @Override
    public BlockModel copy() {
        FileBlock block = new FileBlock(workspace);
        block.path.set(this.path.get());
        return block;
    }

    @Override
    public void remove() {
        super.remove();
        path.removeListener(pathListener);

        if (textField != null) {
            textField.setOnMouseEntered(null);
            textField.textProperty().unbindBidirectional(path);
        }

        if (button != null) {
            button.setOnAction(null);
        }
    }

}

package btslib.file;

import btscore.graph.base.BaseButton;
import btscore.graph.block.BlockModel;
import btscore.graph.port.PortModel;
import btscore.icons.FontAwesomeSolid;
import btsxml.BlockTag;
import java.io.File;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javax.xml.namespace.QName;

/**
 *
 * @author joostmeulenkamp
 */
public abstract class FileBlock extends BlockModel {

    protected final PortModel output;
    protected final StringProperty path = new SimpleStringProperty();

    protected BaseButton button;
    private TextField textField;

    public FileBlock() {
        this.nameProperty().set("File");
        output = addOutputPort("file", File.class);
        initialize();
    }

    @Override
    protected final void initialize() {
        path.addListener(pathListener);
    }

    @Override
    public Region getCustomization() {
        textField = new TextField();
        textField.setFocusTraversable(false);
        customizeTextField(textField);

        button = new BaseButton(FontAwesomeSolid.FOLDER_OPEN);
        button.setOnAction(this::handleOpenFile);

        HBox box = new HBox(5);
        box.getChildren().addAll(textField, button);

        textField.setOnMouseEntered(this::focusOnTextField);
        textField.textProperty().bindBidirectional(path);
        return box;
    }

    protected abstract void customizeTextField(TextField textField);

    ChangeListener<String> pathListener = this::onPathChanged;

    private void onPathChanged(Object b, String o, String n) {
        processSafely();
    }

    private void focusOnTextField(MouseEvent event) {
        textField.requestFocus();
    }

    protected void handleOpenFile(ActionEvent event) {

        //Do Action
        FileChooser picker = new FileChooser();
        picker.setTitle("Choose a file...");
        Window window = button.getScene().getWindow();
        File file = picker.showOpenDialog(window);

        //Set Data
        if (file != null) {
            String filePath = file.getPath();
            path.set(filePath);
            // when path changes, process is triggered
        } else {
            path.set(null);
            // when path changes, process is triggered
        }
    }

    public StringProperty pathProperty() {
        return path;
    }

    @Override
    public void process() throws Exception {

        if (path.get() == null || path.get().isEmpty()) {
            outputPorts.get(0).setData(null);
            return;
        }

        File file = new File(path.get());
        processFile(file);
    }

    protected abstract void processFile(File file) throws Exception;

    @Override
    public void serialize(BlockTag xmlTag) {
        super.serialize(xmlTag);
        String filePath = path.get() != null ? path.get() : "";
        xmlTag.getOtherAttributes().put(QName.valueOf("path"), filePath);
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
        String filePath = xmlTag.getOtherAttributes().get(QName.valueOf("path"));
        filePath = !filePath.isEmpty() ? filePath : null;
        this.path.set(filePath);
    }

    @Override
    public void onRemoved() {
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

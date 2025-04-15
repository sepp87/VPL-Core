package vpllib.input;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javax.xml.namespace.QName;
import jo.vpl.xml.BlockTag;
import vplcore.IconType;
import vplcore.graph.base.BaseButton;
import vplcore.graph.block.BlockModel;
import vplcore.graph.block.BlockMetadata;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockMetadata(
        identifier = "Input.password",
        category = "Input",
        description = "This text field only hides its text, it does not encrypt it or something. Be careful, the password is still saved as plain text and can still be shown.",
        tags = {"input", "line", "string", "password"})
public class PasswordBlock extends BlockModel {

    private final BooleanProperty hidden = new SimpleBooleanProperty(true);
    private final StringProperty value = new SimpleStringProperty();

    private PasswordField passwordField;
    private TextField textField;
    private BaseButton toggleButton;

    public PasswordBlock() {
        this.nameProperty().set("Password");
        addOutputPort("value", String.class);
        initialize();
    }

    @Override
    protected final void initialize() {
        value.addListener(stringListener);
    }

    @Override
    public Region getCustomization() {

        passwordField = new PasswordField();
        passwordField.managedProperty().bind(hidden); // Bind visibility and management based on hidden property
        passwordField.visibleProperty().bind(hidden);
        passwordField.setPromptText("Write here...");
        passwordField.setFocusTraversable(false);
        passwordField.setMinWidth(100);
        passwordField.setStyle(
                "-fx-pref-column-count: 26;\n"
                + "fx-font-size: 10;\n");

        textField = new TextField();
        textField.setManaged(false); // Initially hide the text field
        textField.setVisible(false);
        textField.setPromptText("Write here...");
        textField.setFocusTraversable(false);
        textField.setMinWidth(100);
        textField.setStyle(
                "-fx-pref-column-count: 26;\n"
                + "fx-font-size: 10;\n");
        textField.textProperty().bindBidirectional(value);
        textField.managedProperty().bind(hidden.not()); // Bind visibility and management based on hidden property
        textField.visibleProperty().bind(hidden.not());
        textField.textProperty().bindBidirectional(value); // Sync content between password field and text field
        textField.textProperty().bindBidirectional(passwordField.textProperty());

        toggleButton = new BaseButton(IconType.FA_EYE_SLASH);
        toggleButton.setOnAction(this::toggleHidden);

        HBox root = new HBox(10, passwordField, textField, toggleButton);

        return root;
    }

    private void toggleHidden(ActionEvent event) {
        boolean isHidden = hidden.get();
        hidden.set(!isHidden);
        IconType icon = !isHidden ? IconType.FA_EYE_SLASH : IconType.FA_EYE;
        toggleButton.setText(icon);
    }

    @Override
    public EventHandler<MouseEvent> onMouseEntered() {
        return this::focusOnTextField;
    }

    private final ChangeListener<String> stringListener = this::onStringChanged;

    private void onStringChanged(Object b, Object o, Object n) {
        processSafely();
    }

    private void focusOnTextField(MouseEvent event) {
        textField.requestFocus();
    }

    @Override
    public void process() {
        String str = value.get();
        outputPorts.get(0).setData(str);
    }

    @Override
    public void serialize(BlockTag xmlTag) {
        super.serialize(xmlTag);
        String str = value.get() != null ? value.get() : "";
        xmlTag.getOtherAttributes().put(QName.valueOf("value"), str);
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
        String str = xmlTag.getOtherAttributes().get(QName.valueOf("value"));
        str = !str.isEmpty() ? str : null;
        value.set(str);
    }

    @Override
    public BlockModel copy() {
        PasswordBlock block = new PasswordBlock();
        block.value.set(this.value.get());
        return block;
    }

    @Override
    public void onRemoved() {
        value.removeListener(stringListener);
        if (textField != null) {
            textField.textProperty().unbindBidirectional(value);
            textField.setOnKeyPressed(null);
        }
    }

}

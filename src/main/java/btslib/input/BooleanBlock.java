package btslib.input;

import btscore.icons.FontAwesomeSolid;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javax.xml.namespace.QName;
import btsxml.BlockTag;
import btscore.graph.block.BlockMetadata;
import btscore.graph.block.BlockModel;
import btscore.graph.block.BlockView;

/**
 *
 * @author joostmeulenkamp
 */
@BlockMetadata(
        identifier = "Input.boolean",
        category = "Input",
        description = "Switch between TRUE and FALSE",
        tags = {"boolean", "true", "false"}
)
public class BooleanBlock extends BlockModel {

    private final BooleanProperty bool = new SimpleBooleanProperty(false);

    private Label onOffSwitch;
    private String offIcon;
    private String onIcon;

    public BooleanBlock() {
        this.nameProperty().set("Boolean");
        addOutputPort("value", Boolean.class);
        initialize();
    }

    @Override
    protected final void initialize() {
        outputPorts.get(0).dataProperty().bind(bool);
    }

    @Override
    public Region getCustomization() {
        onOffSwitch = BlockView.getAwesomeIcon(FontAwesomeSolid.TOGGLE_OFF);
        offIcon = onOffSwitch.getText();
        onIcon = FontAwesomeSolid.TOGGLE_ON.unicode();
        bool.addListener(boolListener);
        onOffSwitch.setOnMouseClicked(event -> bool.set(!bool.get()));
        return onOffSwitch;
    }

    ChangeListener<Boolean> boolListener = this::onBoolChanged;

    private void onBoolChanged(Object b, boolean o, boolean isOn) {
        String icon = isOn ? onIcon : offIcon;
        onOffSwitch.textProperty().set(icon);
    }

    public BooleanProperty booleanProperty() {
        return bool;
    }

    @Override
    public void process() {

    }

    @Override
    public void serialize(BlockTag xmlTag) {
        super.serialize(xmlTag);
        xmlTag.getOtherAttributes().put(QName.valueOf("boolean"), bool.getValue().toString());
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
        String value = xmlTag.getOtherAttributes().get(QName.valueOf("boolean"));
        this.bool.set(Boolean.parseBoolean(value));
    }

    @Override
    public BlockModel copy() {
        BooleanBlock block = new BooleanBlock();
//        BooleanBlock block = new BooleanBlock(workspace);
        block.bool.set(this.bool.get());
        return block;
    }

    @Override
    public void onRemoved() {
        outputPorts.get(0).dataProperty().unbind();
        if (onOffSwitch != null) {
            onOffSwitch.setOnMouseClicked(null);
        }
    }

}

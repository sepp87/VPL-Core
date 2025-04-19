package btslib.autoconnect;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javax.xml.namespace.QName;
import btscore.IconType;
import btsxml.BlockTag;
import btscore.graph.block.BlockMetadata;
import btscore.graph.block.BlockModel;
import btscore.graph.block.BlockView;

/**
 *
 * @author JoostMeulenkamp
 */
@BlockMetadata(
        identifier = "Autoconnect.Transmitter",
        category = "General",
        description = "A template block for further customization",
        tags = {"template", "dummy", "example"})
public class TransmitterBlock extends BlockModel {

    public TransmitterBlock() {
        this.nameProperty().set("Transmitter");
        addOutputPort("Client", HttpClient.class).wirelessProperty().set(true);
        initialize();
    }

    @Override
    protected final void initialize() {
        // Event handlers, change listeners and bindings
    }

    @Override
    public Region getCustomization() {
        Label label = BlockView.getAwesomeIcon(IconType.FA_PAPER_PLANE);
        return label;
    }

    /**
     * process function is called whenever new data is incoming
     */
    @Override
    public void process() {
    }

    @Override
    public void serialize(BlockTag xmlTag) {
        super.serialize(xmlTag);
    }

    @Override
    public void deserialize(BlockTag xmlTag) {
        super.deserialize(xmlTag);
    }

    @Override
    public BlockModel copy() {
        TransmitterBlock block = new TransmitterBlock();
        // Specify further copy statements here
        return block;
    }

    @Override
    protected void onRemoved() {
        // Remove event handlers, change listeners and bindings
    }
}

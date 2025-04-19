package btscore.graph.group;

import java.util.Collection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import btscore.IconType;
import btscore.graph.base.BaseLabel;
import btscore.graph.block.BlockView;
import btscore.graph.base.BaseButton;

/**
 *
 * @author JoostMeulenkamp
 */
public class BlockGroupView extends GridPane {

    public BaseLabel label;
    public HBox menuBox;
    public ObservableSet<BlockView> blocks;
    public BaseButton binButton;

    public BlockGroupView() {
        getStyleClass().add("vpl-element");
        getStyleClass().add("block-group");

        blocks = FXCollections.observableSet();

        binButton = new BaseButton(IconType.FA_MINUS_CIRCLE);
        binButton.setVisible(false);

        menuBox = new HBox(5);
        label = new BaseLabel(menuBox);
        label.getStyleClass().add("vpl-tag");
        label.setVisible(false);

        menuBox.setAlignment(Pos.BOTTOM_LEFT);
        menuBox.getStyleClass().add("block-header");
        menuBox.getChildren().addAll(label, binButton);

        add(menuBox, 1, 0);
    }

    public void setBlocks(Collection<BlockView> blocks) {
        this.blocks.addAll(blocks);
//        calculateSize();
    }

    public BaseLabel getLabel() {
        return label;
    }

    public BaseButton getBinButton() {
        return binButton;
    }

}

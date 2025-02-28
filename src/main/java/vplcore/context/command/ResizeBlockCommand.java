package vplcore.context.command;

import vplcore.context.Undoable;
import vplcore.graph.block.BlockController;

/**
 *
 * @author Joost
 */
public class ResizeBlockCommand implements Undoable {

    private final BlockController blockController;
    private final double previousWidth;
    private final double previousHeight;
    private final double width;
    private final double height;

    public ResizeBlockCommand(BlockController blockController, double width, double height) {
        this.blockController = blockController;
        this.previousWidth = blockController.getPreviousWidth();
        this.previousHeight = blockController.getPreviousHeight();
        this.width = width;
        this.height = height;
    }

    @Override
    public void execute() {
        // command is only executed to register the move, so it can be undone
    }

    @Override
    public void undo() {
        blockController.setSize(previousWidth, previousHeight);
    }

}

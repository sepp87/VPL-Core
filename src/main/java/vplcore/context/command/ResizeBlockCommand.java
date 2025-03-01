package vplcore.context.command;

import vplcore.context.Undoable;
import vplcore.graph.block.BlockController;

/**
 *
 * @author JoostMeulenkamp
 */
public class ResizeBlockCommand implements Undoable {

    private final BlockController blockController;
    private final double previousWidth;
    private final double previousHeight;
    private final double currentWidth;
    private final double currentHeight;

    public ResizeBlockCommand(BlockController blockController, double width, double height) {
        this.blockController = blockController;
        this.previousWidth = blockController.getPreviousWidth();
        this.previousHeight = blockController.getPreviousHeight();
        this.currentWidth = width;
        this.currentHeight = height;
    }

    @Override
    public void execute() {
        blockController.setSize(currentWidth, currentHeight);

    }

    @Override
    public void undo() {
        blockController.setSize(previousWidth, previousHeight);
    }

}

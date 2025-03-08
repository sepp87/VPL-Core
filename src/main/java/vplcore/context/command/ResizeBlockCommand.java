package vplcore.context.command;

import vplcore.graph.block.BlockController;
import vplcore.context.UndoableCommand;

/**
 *
 * @author JoostMeulenkamp
 */
public class ResizeBlockCommand implements UndoableCommand {

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
    public boolean execute() {
        blockController.getModel().widthProperty().set(currentWidth);
        blockController.getModel().heightProperty().set(currentHeight);
        return true;

    }

    @Override
    public void undo() {
        blockController.getModel().widthProperty().set(previousHeight);
        blockController.getModel().heightProperty().set(previousWidth);
    }

}

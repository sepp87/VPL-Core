package btscore.editor.commands;

import btscore.editor.context.Command;
import btscore.graph.block.BlockLibraryLoader;

/**
 *
 * @author joostmeulenkamp
 */
public class ReloadPluginsCommand implements Command {

    public ReloadPluginsCommand() {
    }

    @Override
    public boolean execute() {
        BlockLibraryLoader.reloadExternalBlocks();
        return true;

    }

}

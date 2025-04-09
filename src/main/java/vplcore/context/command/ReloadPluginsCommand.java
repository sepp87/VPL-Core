package vplcore.context.command;

import vplcore.context.Command;
import vplcore.graph.util.BlockLibraryLoader;

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

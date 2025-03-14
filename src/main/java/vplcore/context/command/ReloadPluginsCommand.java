package vplcore.context.command;

import vplcore.context.ResetHistoryCommand;
import vplcore.graph.util.BlockLibraryLoader;

/**
 *
 * @author joostmeulenkamp
 */
public class ReloadPluginsCommand implements ResetHistoryCommand {

    public ReloadPluginsCommand() {
    }

    @Override
    public boolean execute() {
        BlockLibraryLoader.reloadExternalBlocks();
        return true;

    }

}

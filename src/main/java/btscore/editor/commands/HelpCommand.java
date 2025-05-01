package btscore.editor.commands;

import btscore.HelpDialog;
import btscore.editor.context.Command;

/**
 *
 * @author joostmeulenkamp
 */
public class HelpCommand implements Command {

    @Override
    public boolean execute() {
        HelpDialog.show();
        return true;
    }

}

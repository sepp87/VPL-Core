package vplcore.workspace.input;

import javafx.event.EventHandler;
import static javafx.scene.input.KeyCode.A;
import static javafx.scene.input.KeyCode.C;
import static javafx.scene.input.KeyCode.DELETE;
import static javafx.scene.input.KeyCode.G;
import static javafx.scene.input.KeyCode.N;
import static javafx.scene.input.KeyCode.O;
import static javafx.scene.input.KeyCode.S;
import static javafx.scene.input.KeyCode.SPACE;
import static javafx.scene.input.KeyCode.V;
import javafx.scene.input.KeyEvent;
import vplcore.Config;
import static vplcore.Util.OperatingSystem.WINDOWS;
import vplcore.workspace.Actions;
import vplcore.workspace.Workspace;

/**
 *
 * @author joostmeulenkamp
 */
public class KeyboardInputHandler {

    private final Workspace workspace;
    private boolean init = false;

    public KeyboardInputHandler(Workspace workspace) {
        this.workspace = workspace;
        addInputHandlers();
    }

    private void addInputHandlers() {
        workspace.getScene().setOnKeyPressed(handleKeyPressed);
    }

    private final EventHandler<KeyEvent> handleKeyPressed = new EventHandler<>() {

        @Override
        public void handle(KeyEvent event) {

            boolean isModifierDown = isModifierDown(event);

            switch (event.getCode()) {
                case BACK_SPACE:
                    Actions.deleteSelectedBlocks(workspace);
                    break;
                case DELETE:
                    Actions.deleteSelectedBlocks(workspace);
                    break;
                case C:
                    if (isModifierDown) {
                        Actions.copyBlocks(workspace);
                    }
                    break;
                case V:
                    if (isModifierDown) {
                        Actions.pasteBlocks(workspace);
                    }
                    break;
                case G:
                    if (isModifierDown) {
                        Actions.groupBlocks(workspace);
                    }
                    break;
                case N:
                    if (isModifierDown) {
                        Actions.newFile(workspace);
                    }
                    break;
                case S:
                    if (isModifierDown) {
                        Actions.saveFile(workspace);
                    }
                    break;
                case O:
                    if (isModifierDown) {
                        Actions.openFile(workspace);
                    }
                    break;
                case A:
                    if (isModifierDown) {
                        Actions.selectAllBlocks(workspace);
                    }
                    break;
                case SPACE:
                    Actions.zoomToFit(workspace);
                    break;
            }
        }
    };

    private boolean isModifierDown(KeyEvent e) {
        switch (Config.get().operatingSystem()) {
            case WINDOWS:
                return e.isControlDown();
            case MACOS:
                return e.isMetaDown();
            case LINUX:
                return e.isMetaDown();
            default:
                return e.isControlDown();
        }
    }

}

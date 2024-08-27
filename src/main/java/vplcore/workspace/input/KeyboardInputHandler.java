package vplcore.workspace.input;

import vplcore.graph.model.Block;
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

        //TODO this method listener should be removed, use real listeners instead of method references
        this.workspace.sceneProperty().addListener(this::addInputHandlers);
    }

    private void addInputHandlers(Object obj, Object oldVal, Object newVal) {
        workspace.getScene().setOnKeyPressed(this::handle_KeyPress);
        workspace.getScene().setOnKeyReleased(this::handle_KeyRelease);
    }

    public void handle_KeyRelease(KeyEvent e) {

        boolean isModifierDown = isModifierDown(e);

        switch (e.getCode()) {
            case DELETE:
                for (Block block : workspace.selectedBlockSet) {
                    block.delete();
                }
                workspace.selectedBlockSet.clear();
                break;
            case C:
                if (isModifierDown) {
                    Actions.copyBlocks(workspace);
                }
                break;
            case V:
                if (isModifierDown) {
                    if (workspace.tempBlockSet == null || workspace.tempBlockSet.isEmpty()) {
                        return;
                    }
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
            case A: {
                if (isModifierDown) {
                    workspace.selectedBlockSet.clear();
                    for (Block block : workspace.blockSet) {
                        block.setSelected(true);
                        workspace.selectedBlockSet.add(block);
                    }
                }
            }
            break;
        }
    }

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

    /**
     * Move all the with a press on the arrow keys. A form of panning.
     *
     * @param e
     */
    public void handle_KeyPress(KeyEvent e) {

        switch (e.getCode()) {
            case SPACE:
                Actions.zoomToFit(workspace);
                break;
        }
    }

}

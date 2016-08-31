package jo.vpl.util;

import java.io.File;
import java.util.List;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author JoostMeulenkamp
 */
public class FilePicker {

    /**
     * Opens a file picker window for a single file.
     *
     * @param title the title of the window.
     * @param stage
     * @param type the file extension to open. Null for all files.
     * @return the file that is picked. Can return null.
     */
    public File openFile(String title, Stage stage, FileType type) {

        FileChooser picker = new FileChooser();
        picker.setTitle(title);

        if (type == FileType.IFC) {
            picker.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("IFC", "*.ifc"));
        } else if (type == FileType.mvdXML) {
            picker.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("mvdXML", "*.mvdxml"));
        } else {
            picker.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("All Files", "*.*"));
        }

        File file = picker.showOpenDialog(stage);
        return file;
    }

    /**
     * Opens a file picker window for multiple files.
     *
     * @param title the title of the window
     * @param stage
     * @return the files that are picked. Can return null
     */
    public List<File> openMultipleFiles(String title, Stage stage) {
        FileChooser picker = new FileChooser();
        picker.setTitle(title);
        List<File> files = picker.showOpenMultipleDialog(stage);
        return files;
    }

    /**
     * Opens a file save window for a single file.
     *
     * @param title the title of the window.
     * @param stage
     * @return the file to save to. Can return null.
     */
    public File saveFile(String title, Stage stage) {
        FileChooser picker = new FileChooser();
        picker.setTitle(title);
        File file = picker.showSaveDialog(stage);
        return file;
    }

    public File getDirectory(String title, Stage stage) {
        DirectoryChooser picker = new DirectoryChooser();
        picker.setTitle(title);
        File dir = picker.showDialog(stage);
        return dir;
    }

    public enum FileType {

        IFC,
        mvdXML,
        BCF
    }
}

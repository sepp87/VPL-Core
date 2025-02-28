package vpllib.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import vplcore.IconType;
import vplcore.graph.block.BlockMetadata;
import vplcore.graph.block.BlockModel;
import vplcore.graph.block.BlockView;
import vplcore.workspace.WorkspaceModel;

/**
 * Note to self - if multiple changes due to saving recur, it might be worth
 * debouncing with Sleep (TimeUnit.sleep() approach) as alternative approach to
 * sort out redundant file changes. Currently redundant file changes are
 * filtered out by checking the currentModifiedTime against lastModifiedTime.
 *
 *
 * @author JoostMeulenkamp
 */
@BlockMetadata(
        identifier = "File.observe",
        category = "File",
        description = "Observe a file",
        tags = {"file", "observe"}
)
public class ObserveFileBlock extends BlockModel {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Future<?> watchTask;
    private File observedFile;
    private long lastModifiedTime = 0;  // Track last modified time

    public ObserveFileBlock(WorkspaceModel workspace) {
        super(workspace);
        nameProperty().set("Observe");
        addInputPort("observed", File.class);
        addOutputPort("updated", File.class);
    }

    @Override
    public Region getCustomization() {
        Label icon = BlockView.getAwesomeIcon(IconType.FA_EYE);
        return icon;
    }

    @Override
    public synchronized void process() throws Exception {
        Object data = inputPorts.get(0).getData();

        if (data == null) {
            // If data is null, stop observation (if any)
            stopObservation();
            return; // Exit early
        }

        File newFile = (File) data;

        // Stop existing watcher if already running
        if (watchTask != null && !watchTask.isDone()) {
            watchTask.cancel(true);
        }

        observedFile = newFile;
        watchTask = executorService.submit(() -> observeFile(observedFile));
    }

    private void observeFile(File file) {
        Path filePath = file.toPath();
        Path directoryPath = filePath.getParent();

        if (directoryPath == null) {
            System.out.println("Invalid file path.");
            return;
        }

        try ( WatchService watchService = FileSystems.getDefault().newWatchService()) {
            directoryPath.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            System.out.println("Observing: " + file);

            while (!Thread.currentThread().isInterrupted()) {
                WatchKey key = watchService.take(); // Blocking call

                for (WatchEvent<?> event : key.pollEvents()) {
                    Path changedFile = directoryPath.resolve((Path) event.context());

                    // Only process events for the observed file
                    if (changedFile.equals(filePath)) {
                        long currentModifiedTime = file.lastModified();

                        // Ignore event if the modification time is the same as the last
                        if (currentModifiedTime == lastModifiedTime) {
                            continue;
                        }

                        lastModifiedTime = currentModifiedTime;
                        System.out.println("Change detected in: " + file);

                        // Create updated file
                        File updatedFile = new File(file.getPath());
                        outputPorts.get(0).setData(updatedFile);
                    }
                }

                if (!key.reset()) {
                    break; // Stop if directory is no longer accessible
                }
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("File watching stopped.");
        }
    }

    // Stop file observation
    private void stopObservation() {
        if (watchTask != null && !watchTask.isDone()) {
            watchTask.cancel(true); // Interrupt the thread
            System.out.println("File watcher stopped due to null data.");
        }
    }

    @Override
    public void remove() {
        // Stop the file watching task if it's running
        stopObservation();

        // Shutdown executor service
        executorService.shutdownNow();
        System.out.println("Executor service shut down.");

        // Notify that this block was removed
        super.remove();
    }

    @Override
    public BlockModel copy() {
        ObserveFileBlock fileBlock = new ObserveFileBlock(workspace);
        return fileBlock;
    }
}

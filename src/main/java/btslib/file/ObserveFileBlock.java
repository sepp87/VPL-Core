package btslib.file;

import btscore.icons.FontAwesomeSolid;
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
import btscore.graph.block.BlockMetadata;
import btscore.graph.block.BlockModel;
import btscore.graph.block.BlockView;

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
        tags = {"file", "observe", "watch"}
)
public class ObserveFileBlock extends BlockModel {

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Future<?> watchTask;
    private File observedFile;
    private long lastModifiedTime = 0;  // Track last modified time
    
    public ObserveFileBlock() {
        nameProperty().set("Observe");
        addInputPort("observed", File.class);
        addOutputPort("updated", File.class);

        // Register shutdown hook to stop the watcher when the app closes
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stopObservation();
            executorService.shutdownNow();
            System.out.println("File watcher and executor service shut down on app shutdown.");
        }));
    }

    @Override
    protected void initialize() {
        
    }
    
    @Override
    protected void onActiveChanged() {
        if (!this.isActive()) {
            stopObservation();
        }
        super.onActiveChanged();
    }

    @Override
    public Region getCustomization() {
        Label icon = BlockView.getAwesomeIcon(FontAwesomeSolid.EYE);
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

        // Stop previous watcher if already running
        stopObservation();
        observedFile = newFile;

        // Start a new daemon thread for watching the file
        Thread watcherThread = new Thread(() -> observeFile(observedFile));
        watcherThread.setDaemon(true); // Mark the thread as a daemon
        watchTask = executorService.submit(watcherThread);
    }

    private void observeFile(File file) {
        Path filePath = file.toPath();
        Path directoryPath = filePath.getParent();

        if (directoryPath == null) {
            System.out.println("Invalid file path.");
            return;
        }

        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
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
    public void onRemoved() {
        // Stop the file watching task if it's running
        stopObservation();

        // Shutdown executor service
        executorService.shutdownNow();
        System.out.println("Executor service shut down.");
    }
    
   

    @Override
    public void revive() {
        if (executorService.isShutdown() || executorService.isTerminated()) {
            executorService = Executors.newSingleThreadExecutor();
        }
        super.revive();
    }

    @Override
    public BlockModel copy() {
        ObserveFileBlock fileBlock = new ObserveFileBlock();
//        ObserveFileBlock fileBlock = new ObserveFileBlock(workspace);
        return fileBlock;
    }
}

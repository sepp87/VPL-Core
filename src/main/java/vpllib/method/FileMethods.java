package vpllib.method;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import vplcore.graph.block.BlockMetadata;

/**
 *
 * @author JoostMeulenkamp
 */
public class FileMethods {

    @BlockMetadata(
            name = "exists",
            description = "Tests whether a file exists.",
            identifier = "File.exists",
            category = "Core")
    public static boolean exists(File file) {
        Path path = file.toPath();
        return Files.exists(path);
    }

    @BlockMetadata(
            name = "LastModifiedTime",
            description = "Returns a file's last modified time. The string is returned in the ISO 8601 format: YYYY-MM-DDThh:mm:ss[.s+]Z",
            identifier = "File.getLastModifiedTime",
            category = "Core")
    public static String getLastModifiedTime(File file) throws IOException {
        Path path = file.toPath();
        return Files.getLastModifiedTime(path).toString();
    }

}

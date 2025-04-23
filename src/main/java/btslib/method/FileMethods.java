package btslib.method;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import btscore.graph.block.BlockMetadata;
import btscore.utils.FileUtils;

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

    @BlockMetadata(
            name = "readAllLines",
            description = "Read all lines from a file. Bytes from the file are decoded into characters using the UTF-8 charset.",
            identifier = "File.readAllLines",
            category = "Core")
    public static List<String> readAllLines(File file) throws IOException {
        Path path = file.toPath();
        return Files.readAllLines(path);
    }

    @BlockMetadata(
            name = "size",
            description = "Returns the size of a file (in bytes). The size may differ from the actual size on the file system due to compression, support for sparse files, or other reasons. The size of files that are not regular files is implementation specific and therefore unspecified.",
            identifier = "File.size",
            category = "Core")
    public static long size(File file) throws IOException {
        Path path = file.toPath();
        return Files.size(path);
    }

    @BlockMetadata(
            name = "list",
            description = "Return a list of files, the elements of which are the entries in the directory.",
            identifier = "File.list",
            category = "Core")
    public static List<File> list(File dir) throws IOException {
        Path dirPath = dir.toPath();
        List<File> result = new ArrayList<>();
        try ( Stream<Path> stream = Files.list(dirPath)) {
            stream.map(Path::toFile).forEach(result::add);
        }
        return result;
    }

    @BlockMetadata(
            name = "isReadable",
            description = "Returns a file's last modified time. The string is returned in the ISO 8601 format: YYYY-MM-DDThh:mm:ss[.s+]Z",
            identifier = "File.isReadable",
            category = "Core")
    public static boolean isReadable(File file) {
        Path path = file.toPath();
        return Files.isReadable(path);
    }

    @BlockMetadata(
            name = "isWritable",
            description = "Returns a file's last modified time. The string is returned in the ISO 8601 format: YYYY-MM-DDThh:mm:ss[.s+]Z",
            identifier = "File.isWritable",
            category = "Core")
    public static boolean isWritable(File file) {
        Path path = file.toPath();
        return Files.isWritable(path);
    }

    @BlockMetadata(
            name = "Encoding",
            description = "Detect a file's encoding. If no match was found, it defaults to UTF-8.",
            identifier = "File.detectEncoding",
            category = "Core")
    public static String detectEncoding(File file) {
        return FileUtils.detectEncoding(file);
    }

}

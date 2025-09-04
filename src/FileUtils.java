import java.io.File;

/**
 * Utility for recursively deleting a directory structure.
 */
public class FileUtils {
    public static boolean deleteRecursively(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    // Short-circuit if any child deletion fails
                    if (!deleteRecursively(child)) {
                        return false;
                    }
                }
            }
        }
        return file.delete();
    }
}

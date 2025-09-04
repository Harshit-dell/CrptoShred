import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * Utility for calculating the SHA-256 hash of a file.
 */
public class HashUtil {
    public static String sha256OfFile(File file) {
        try (FileInputStream fis = new FileInputStream(file)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
            byte[] hash = digest.digest();
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "Error computing hash: " + e.getMessage();
        }
    }
}

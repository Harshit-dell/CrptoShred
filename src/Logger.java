import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A simple logger utility to write messages to both the console and a file.
 */
public class Logger {
    private final PrintWriter writer;
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    /**
     * Creates a new logger instance.
     * @param fileName The name of the log file. It will be appended to if it exists.
     * @throws IOException if the file cannot be opened for writing.
     */
    public Logger(String fileName) throws IOException {
        // 'true' for append mode
        FileWriter fileWriter = new FileWriter(fileName, true);
        this.writer = new PrintWriter(fileWriter);
    }

    /**
     * Logs an informational message.
     * @param message The message to log.
     */
    public void log(String message) {
        String timestampedMessage = dtf.format(LocalDateTime.now()) + " | INFO:  " + message;
        System.out.println(message); // Still print to console
        if (writer != null) {
            writer.println(timestampedMessage);
            writer.flush();
        }
    }

    /**
     * Logs an error message.
     * @param message The error message to log.
     */
    public void error(String message) {
        String timestampedMessage = dtf.format(LocalDateTime.now()) + " | ERROR: " + message;
        System.err.println(message); // Still print to console's error stream
        if (writer != null) {
            writer.println(timestampedMessage);
            writer.flush();
        }
    }

    /**
     * Closes the log file writer.
     */
    public void close() {
        if (writer != null) {
            String endMessage = dtf.format(LocalDateTime.now()) + " | INFO:  --- Log session ended ---\n";
            writer.println(endMessage);
            writer.close();
        }
    }
}

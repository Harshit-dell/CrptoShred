import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * The main entry point for the CryptoWipe secure shredder utility.
 * Parses command-line arguments and orchestrates the file wiping process.
 */
public class Main {

    // Enum to define the available shredding standards
    public enum ShredMethod {
        DEFAULT,        // Our custom 3-pattern cycle for a user-defined number of passes
        DOD_5220_22_M,  // A common 3-pass DoD standard
        SIMPLE_ZERO     // A single pass of all zeros for speed
    }

    public static void main(String[] args) {
        Logger logger = null;
        try {
            logger = new Logger("CryptoWipe_log.txt");
        } catch (IOException e) {
            System.err.println("FATAL: Could not create or open log file: " + e.getMessage());
            return;
        }

        logger.log("--- Log session started ---");

        if (args.length < 1) {
            printUsage(logger);
            logger.close();
            return;
        }

        String path = args[0];
        int passes = 3; // Default passes for the DEFAULT method
        boolean delete = false;
        boolean verify = false;
        ShredMethod method = ShredMethod.DEFAULT;

        // --- Argument Parsing ---
        for (int i = 1; i < args.length; i++) {
            String arg = args[i];

            if ("--method".equalsIgnoreCase(arg)) {
                if (i + 1 < args.length) {
                    i++; // Move to the next argument which should be the method name
                    try {
                        method = ShredMethod.valueOf(args[i].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        logger.error("Warning: Unknown method '" + args[i] + "'. Using DEFAULT.");
                    }
                } else {
                    logger.error("Warning: --method flag requires a method name. Using DEFAULT.");
                }
            } else if ("delete".equalsIgnoreCase(arg)) {
                delete = true;
            } else if ("verify".equalsIgnoreCase(arg)) {
                verify = true;
            } else {
                try {
                    passes = Integer.parseInt(arg);
                    if (passes < 1) {
                        logger.error("Warning: Number of passes must be at least 1. Setting to 1.");
                        passes = 1;
                    }
                } catch (NumberFormatException e) {
                    logger.error("Warning: Unknown option '" + arg + "'. Ignoring.");
                }
            }
        }

        logger.log("--- Configuration ---");
        logger.log("Target: " + path);
        logger.log("Method: " + method);
        if (method == ShredMethod.DEFAULT) {
            logger.log("Passes: " + passes);
        }
        logger.log("Delete after wipe: " + delete);
        logger.log("Verify with SHA-256: " + verify);
        logger.log("---------------------");

        File target = new File(path);
        if (!target.exists()) {
            logger.error("Error: Path does not exist: " + path);
            logger.close();
            return;
        }

        // --- Safety Confirmation Prompt ---
        // This is a critical safety feature to prevent accidental data loss.
        // It prints directly to the console, not the log file.
        System.out.println("\n\u001B[31;1mWARNING: You are about to securely wipe the path:\u001B[0m"); // Red bold text
        System.out.println("  " + target.getAbsolutePath());
        System.out.println("\u001B[31;1mThis action is irreversible and data cannot be recovered.\u001B[0m");
        System.out.print("Are you absolutely sure you want to proceed? (Type 'yes' to confirm): ");

        try (Scanner scanner = new Scanner(System.in)) {
            String confirmation = scanner.nextLine();
            if (!"yes".equalsIgnoreCase(confirmation.trim())) {
                logger.log("\nOperation cancelled by user.");
                System.out.println("\nOperation cancelled.");
                logger.close();
                return; // Exit the program safely
            }
        }
        logger.log("User confirmed the operation.");
        System.out.println("Confirmation received. Proceeding with shred...");


        // --- Start Wiping Operation ---
        boolean ok = FileWiper.wipePath(target, passes, method, verify, logger);
        if (!ok) {
            logger.error("The wipe operation failed to complete successfully.");
            logger.close();
            return;
        }

        logger.log("\n✅ Wipe operation completed.");

        if (delete) {
            logger.log("Attempting to delete the path...");
            boolean deleted = FileUtils.deleteRecursively(target);
            if (deleted) {
                logger.log("✅ Successfully deleted: " + target.getAbsolutePath());
            } else {
                logger.error("❌ Failed to delete: " + target.getAbsolutePath());
            }
        } else {
            logger.log("Path was kept as requested.");
        }

        logger.close();
    }

    private static void printUsage(Logger logger) {
        logger.log("Secure Shredder - A tool to securely delete files and folders.");
        logger.log("\nUsage: java Main <path> [options]");
        logger.log("\nArguments:");
        logger.log("  <path>    : The file or folder to be securely wiped.");
        logger.log("\nOptions:");
        logger.log("  [passes]  : Sets number of passes for the DEFAULT method. Defaults to 3.");
        logger.log("  --method <name> : Selects a shredding standard. Available methods:");
        logger.log("      DEFAULT       - Cycles through SHA-256, zeros, and ones for [passes] loops.");
        logger.log("      DOD_5220_22_M - A 3-pass U.S. Department of Defense standard.");
        logger.log("      SIMPLE_ZERO   - A single, fast pass of all zeros.");
        logger.log("  delete    : Permanently delete the file/folder after wiping.");
        logger.log("  verify    : Show the SHA-256 hash of the file(s) after the final wipe pass.");
        logger.log("\nExamples:");
        logger.log("  java Main secrets.txt --method DOD_5220_22_M delete");
    }
}


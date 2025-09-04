import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Handles the core logic of overwriting files based on selected methods.
 */
public class FileWiper {

    public static boolean wipePath(File target, int passes, Main.ShredMethod method, boolean verify, Logger logger) {
        if (!target.exists()) return false;

        if (target.isFile()) {
            boolean ok = wipeFile(target, passes, method, logger);
            if (ok && verify) {
                logger.log("\nVerification SHA-256 (" + target.getName() + "): " + HashUtil.sha256OfFile(target));
            }
            return ok;
        } else if (target.isDirectory()) {
            logger.log("\n--- Wiping Directory: " + target.getAbsolutePath() + " ---");
            File[] list = target.listFiles();
            boolean allOk = true;
            if (list != null) {
                for (File f : list) {
                    if (!wipePath(f, passes, method, verify, logger)) {
                        allOk = false;
                    }
                }
            }
            return allOk;
        }
        return false;
    }

    private static boolean wipeFile(File file, int passes, Main.ShredMethod method, Logger logger) {
        if (!file.exists() || !file.canWrite()) {
            logger.error("Error: Invalid or non-writable file: " + file.getAbsolutePath());
            return false;
        }
        if (file.length() == 0) {
            logger.log("Skipping empty file: " + file.getName());
            return true;
        }

        SecureRandom random = new SecureRandom();
        try (FileOutputStream out = new FileOutputStream(file)) {
            logger.log("\nShredding: " + file.getName() + " (" + file.length() + " bytes)");
            switch (method) {
                case DOD_5220_22_M:
                    performPass(out, file, 1, 3, "zeros (0x00)", random, (byte)0x00, false, logger);
                    performPass(out, file, 2, 3, "ones (0xFF)", random, (byte)0xFF, false, logger);
                    performPass(out, file, 3, 3, "random data", random, (byte)0x00, true, logger);
                    break;
                case SIMPLE_ZERO:
                    performPass(out, file, 1, 1, "zeros (0x00)", random, (byte)0x00, false, logger);
                    break;
                case DEFAULT:
                default:
                    for (int pass = 1; pass <= passes; pass++) {
                        switch ((pass - 1) % 3) {
                            case 0:
                                performPass(out, file, pass, passes, "SHA-256 pseudo-random", random, (byte)0x00, true, logger);
                                break;
                            case 1:
                                performPass(out, file, pass, passes, "zeros (0x00)", random, (byte)0x00, false, logger);
                                break;
                            case 2:
                                performPass(out, file, pass, passes, "ones (0xFF)", random, (byte)0xFF, false, logger);
                                break;
                        }
                    }
                    break;
            }
            return true;
        } catch (Exception e) {
            logger.error("\nError wiping file " + file.getName() + ": " + e.getMessage());
            return false;
        }
    }

    private static void performPass(FileOutputStream out, File file, int passNum, int totalPasses, String message, SecureRandom random, byte pattern, boolean useShaPattern, Logger logger) throws Exception {
        logger.log("Pass " + passNum + "/" + totalPasses + ": Writing with " + message);
        out.getChannel().position(0); // Seek to the beginning of the file for each pass
        long length = file.length();
        byte[] buffer = new byte[8192];

        if (useShaPattern) {
            fillWithSha256Pattern(out, length, buffer, random);
        } else {
            fillWithConstantPattern(out, length, buffer, pattern);
        }

        out.flush();
        out.getFD().sync();
    }

    private static void fillWithSha256Pattern(FileOutputStream out, long length, byte[] buffer, SecureRandom random) throws Exception {
        byte[] seed = new byte[32];
        random.nextBytes(seed);
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        fillLoop(out, length, buffer, (b, l, w) -> {
            byte[] currentSeed = (byte[]) b[0];
            currentSeed = sha256.digest(currentSeed);
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = currentSeed[i % currentSeed.length];
            }
            b[0] = currentSeed;
            return buffer;
        }, seed);
    }

    private static void fillWithConstantPattern(FileOutputStream out, long length, byte[] buffer, byte pattern) throws Exception {
        Arrays.fill(buffer, pattern);
        fillLoop(out, length, buffer, (b, l, w) -> buffer, null);
    }

    private static void fillLoop(FileOutputStream out, long length, byte[] buffer, BufferGenerator generator, Object state) throws Exception {
        long written = 0;
        int lastPercent = -1;
        Object[] stateHolder = {state};

        while (written < length) {
            byte[] generatedBuffer = generator.generate(stateHolder, length, written);
            int toWrite = (int) Math.min(generatedBuffer.length, length - written);
            out.write(generatedBuffer, 0, toWrite);
            written += toWrite;

            int percent = (int) ((written * 100) / length);
            if (percent != lastPercent) {
                ProgressBar.print(percent); // Progress bar still goes to console directly
                lastPercent = percent;
            }
        }
    }

    @FunctionalInterface
    interface BufferGenerator {
        byte[] generate(Object[] state, long totalLength, long totalWritten) throws Exception;
    }
}


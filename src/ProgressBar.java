/**
 * Utility for rendering a simple text-based progress bar.
 */
public class ProgressBar {
    public static void print(int percent) {
        int totalBars = 40;
        int filled = (percent * totalBars) / 100;
        StringBuilder bar = new StringBuilder("[");
        for (int i = 0; i < totalBars; i++) {
            bar.append(i < filled ? "█" : "░");
        }
        bar.append("] ");
        // Use \r (carriage return) to move the cursor to the beginning of the line
        System.out.print("\r" + bar + percent + "%");
    }
}

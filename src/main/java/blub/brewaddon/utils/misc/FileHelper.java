package blub.brewaddon.utils.misc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileHelper {
    // Root directory structure
    private static final File EGLI_DIR = new File("BlubSoftware");
    private static final File LOGS_DIR = new File(EGLI_DIR, "logs");
    private static final File CONFIG_DIR = new File(EGLI_DIR, "configs");

    // Initialize all directories when class is first loaded
    static {
        try {
            Files.createDirectories(LOGS_DIR.toPath());
            Files.createDirectories(CONFIG_DIR.toPath());
        } catch (IOException e) {
            System.err.println("[BrewAddon] Failed to create directories: " + e.getMessage());
        }
    }

    /* ===== Log File Methods ===== */
    /**
     * Gets the enemies config file
     * Format: OrionHack/configs/enemies.txt
     */
    public static File getBlacklistFile() {
        return new File(CONFIG_DIR, "blacklist.txt");
    }

    /**
     * Gets a general config file
     * Format: OrionHack/configs/[name].txt
     */
    public static File getConfigFile(String name) {
        return new File(CONFIG_DIR, name + ".txt");
    }

    /* ===== Utility Methods ===== */

    /**
     * Creates a new empty file if it doesn't exist
     * @throws IOException if file creation fails
     */
    public static void ensureFileExists(File file) throws IOException {
        if (!file.exists()) {
            // Create parent directories if needed
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            file.createNewFile();
        }
    }

    /**
     * Creates a file with header if it doesn't exist
     * @param header The header text to write for new files
     */
    public static void ensureFileWithHeader(File file, String header) throws IOException {
        if (!file.exists()) {
            ensureFileExists(file);
            Files.writeString(file.toPath(), header + System.lineSeparator());
        }
    }
}

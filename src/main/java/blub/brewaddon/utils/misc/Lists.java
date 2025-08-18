package blub.brewaddon.utils.misc;

import meteordevelopment.meteorclient.utils.player.ChatUtils;

import java.io.*;
import java.util.*;

public class Lists {
    private static final File BLACKLIST_FILE = FileHelper.getBlacklistFile();
    private static final Map<UUID, String> blacklistedPlayers = new HashMap<>();

    static {
        loadBlacklist();
    }

    private static void loadBlacklist() {
        blacklistedPlayers.clear();

        try {
            FileHelper.ensureFileExists(BLACKLIST_FILE);
            try (BufferedReader reader = new BufferedReader(new FileReader(BLACKLIST_FILE))) {
                reader.lines().forEach(line -> {
                    String[] parts = line.split("\\|", 2);
                    if (parts.length == 2) {
                        blacklistedPlayers.put(UUID.fromString(parts[0]), parts[1]);
                    }
                });
            }
        } catch (IOException e) {
            ChatUtils.error("Failed to load blacklist: " + e.getMessage());
        }
    }

    private static void saveBlacklist() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(BLACKLIST_FILE))) {
            for (Map.Entry<UUID, String> entry : blacklistedPlayers.entrySet()) {
                writer.write(entry.getKey() + "|" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            ChatUtils.error("Failed to save blacklist: " + e.getMessage());
        }
    }

    public static UUID addToBlacklist(String username) {
        UUID uuid = UUID.nameUUIDFromBytes(username.getBytes());
        blacklistedPlayers.put(uuid, username);
        saveBlacklist();
        return uuid;
    }

    public static boolean removeFromBlacklist(String username) {
        UUID uuid = UUID.nameUUIDFromBytes(username.getBytes());
        if (blacklistedPlayers.remove(uuid) != null) {
            saveBlacklist();
            return true;
        }
        return false;
    }

    public static boolean isBlacklisted(String username) {
        UUID uuid = UUID.nameUUIDFromBytes(username.getBytes());
        return blacklistedPlayers.containsKey(uuid);
    }

    public static Set<UUID> getBlacklist() {
        return new HashSet<>(blacklistedPlayers.keySet());
    }

    public static String getBlacklistName(UUID uuid) {
        return blacklistedPlayers.getOrDefault(uuid, uuid.toString());
    }
}

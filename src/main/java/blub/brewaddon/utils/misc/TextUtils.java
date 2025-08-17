package blub.brewaddon.utils.misc;

public class TextUtils {

    /**
     * Formats a Minecraft string (e.g., "minecraft:the_end") into a human-readable format (e.g., "The End").
     *
     * @param input The input string in the format "minecraft:item_name".
     * @return A formatted string with the item name capitalized and spaces between words.
     */
    public static String formatMinecraftString(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        int colonIndex = input.indexOf(':');
        String itemName;
        if (colonIndex != -1 && colonIndex < input.length() - 1) {
            itemName = input.substring(colonIndex + 1);
        } else {
            itemName = input;
        }

        String[] words = itemName.split("_");
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }

            if (result.length() > 0) {
                result.append(" ");
            }

            result.append(Character.toUpperCase(word.charAt(0)))
                .append(word.substring(1).toLowerCase());
        }

        return result.toString();
    }
}

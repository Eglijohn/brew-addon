package blub.brewaddon.utils.misc.chatUtils;

/**
 * Utility class for managing ChatUtils prefix customization.
 * This is just a wrapper around ChatPrefixConfig for convenience.
 */
public class ChatUtilsHelper {

    /**
     * Sets both the custom prefix text and color.
     * @param text The text to display in the prefix
     * @param color The color as an RGB integer
     */
    public static void setCustomPrefix(String text, int color) {
        ChatPrefixConfig.setCustomPrefix(text, color);
    }

    /**
     * Sets the custom module prefix color.
     * @param color The color as an RGB integer for module/class name prefixes
     */
    public static void setCustomModulePrefixColor(int color) {
        ChatPrefixConfig.setCustomModulePrefixColor(color);
    }

    /**
     * Sets both main prefix and module prefix colors.
     * @param mainColor The main prefix color
     * @param moduleColor The module prefix color
     */
    public static void setCustomColors(int mainColor, int moduleColor) {
        ChatPrefixConfig.setCustomColors(mainColor, moduleColor);
    }

    /**
     * Sets only the custom prefix text, keeping the current color.
     * @param text The text to display in the prefix
     */
    public static void setCustomPrefixText(String text) {
        ChatPrefixConfig.setCustomPrefixText(text);
    }

    /**
     * Sets only the custom prefix color, keeping the current text.
     * @param color The color as an RGB integer
     */
    public static void setCustomPrefixColor(int color) {
        ChatPrefixConfig.setCustomPrefixColor(color);
    }

    /**
     * Resets the prefix to the default Meteor settings.
     */
    public static void resetToDefault() {
        ChatPrefixConfig.resetToDefault();
    }

    /**
     * Gets the current custom prefix text.
     * @return The current prefix text
     */
    public static String getCurrentPrefixText() {
        return ChatPrefixConfig.getCurrentPrefixText();
    }

    /**
     * Gets the current custom prefix color.
     * @return The current prefix color as an RGB integer
     */
    public static int getCurrentPrefixColor() {
        return ChatPrefixConfig.getCurrentPrefixColor();
    }

    /**
     * Gets the current custom module prefix color.
     * @return The current module prefix color as an RGB integer
     */
    public static int getCurrentModulePrefixColor() {
        return ChatPrefixConfig.getCurrentModulePrefixColor();
    }

    /**
     * Checks if custom prefix is currently enabled.
     * @return true if using custom prefix, false if using default
     */
    public static boolean isUsingCustomPrefix() {
        return ChatPrefixConfig.isUsingCustomPrefix();
    }

    /**
     * Checks if custom module color is currently enabled.
     * @return true if using custom module color, false if using default
     */
    public static boolean isUsingCustomModuleColor() {
        return ChatPrefixConfig.isUsingCustomModuleColor();
    }

    // Convenience methods for common colors
    public static void setRedModuleColor() {
        setCustomModulePrefixColor(0xFF0000);
    }

    public static void setGreenModuleColor() {
        setCustomModulePrefixColor(0x00FF00);
    }

    public static void setBlueModuleColor() {
        setCustomModulePrefixColor(0x0000FF);
    }

    public static void setYellowModuleColor() {
        setCustomModulePrefixColor(0xFFFF00);
    }

    public static void setPurpleModuleColor() {
        setCustomModulePrefixColor(0xAA00FF);
    }
}

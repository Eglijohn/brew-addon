package blub.brewaddon.utils.misc.chatUtils;

import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import net.minecraft.util.Formatting;

/**
 * Configuration class for managing ChatUtils prefix customization.
 * This class stores the configuration and provides methods to update it.
 */
public class ChatPrefixConfig {

    private static String customPrefixText = "Meteor";
    private static int customPrefixColor = MeteorClient.ADDON.color.getPacked();
    private static int customModulePrefixColor = 0xAA00FF; // Default purple color for module prefixes
    private static boolean useCustomPrefix = false;
    private static boolean useCustomModuleColor = false;

    /**
     * Sets both the custom prefix text and color.
     * @param text The text to display in the prefix
     * @param color The color as an RGB integer
     */
    public static void setCustomPrefix(String text, int color) {
        customPrefixText = text;
        customPrefixColor = color;
        useCustomPrefix = true;
        // Reinitialize ChatUtils to apply changes
        ChatUtils.init();
    }

    /**
     * Sets the custom module prefix color.
     * @param color The color as an RGB integer for module/class name prefixes
     */
    public static void setCustomModulePrefixColor(int color) {
        customModulePrefixColor = color;
        useCustomModuleColor = true;
    }

    /**
     * Sets both main prefix and module prefix colors.
     * @param mainColor The main prefix color
     * @param moduleColor The module prefix color
     */
    public static void setCustomColors(int mainColor, int moduleColor) {
        customPrefixColor = mainColor;
        customModulePrefixColor = moduleColor;
        useCustomPrefix = true;
        useCustomModuleColor = true;
        ChatUtils.init();
    }

    /**
     * Sets only the custom prefix text, keeping the current color.
     * @param text The text to display in the prefix
     */
    public static void setCustomPrefixText(String text) {
        customPrefixText = text;
        useCustomPrefix = true;
        ChatUtils.init();
    }

    /**
     * Sets only the custom prefix color, keeping the current text.
     * @param color The color as an RGB integer
     */
    public static void setCustomPrefixColor(int color) {
        customPrefixColor = color;
        useCustomPrefix = true;
        ChatUtils.init();
    }

    /**
     * Resets the prefix to the default Meteor settings.
     */
    public static void resetToDefault() {
        customPrefixText = "Meteor";
        customPrefixColor = MeteorClient.ADDON.color.getPacked();
        useCustomPrefix = false;
        useCustomModuleColor = false;
        ChatUtils.init();
    }

    /**
     * Gets the current custom prefix text.
     * @return The current prefix text
     */
    public static String getCurrentPrefixText() {
        return customPrefixText;
    }

    /**
     * Gets the current custom prefix color.
     * @return The current prefix color as an RGB integer
     */
    public static int getCurrentPrefixColor() {
        return customPrefixColor;
    }

    /**
     * Gets the current custom module prefix color.
     * @return The current module prefix color as an RGB integer
     */
    public static int getCurrentModulePrefixColor() {
        return customModulePrefixColor;
    }

    /**
     * Checks if custom prefix is currently enabled.
     * @return true if using custom prefix, false if using default
     */
    public static boolean isUsingCustomPrefix() {
        return useCustomPrefix;
    }

    /**
     * Checks if custom module color is currently enabled.
     * @return true if using custom module color, false if using default
     */
    public static boolean isUsingCustomModuleColor() {
        return useCustomModuleColor;
    }

    // Public getters for mixin access (since mixin is in different package)
    public static String getPrefixText() {
        return useCustomPrefix ? customPrefixText : "Meteor";
    }

    public static int getPrefixColor() {
        return useCustomPrefix ? customPrefixColor : MeteorClient.ADDON.color.getPacked();
    }

    public static int getModulePrefixColor() {
        if (useCustomModuleColor) {
            return customModulePrefixColor;
        }
        // Default to LIGHT_PURPLE formatting color if no custom color is set
        return Formatting.LIGHT_PURPLE.getColorValue() != null ?
            Formatting.LIGHT_PURPLE.getColorValue() : 0xAA00FF;
    }
}

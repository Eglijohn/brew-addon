package blub.brewaddon.modules;

import blub.brewaddon.BrewAddon;
import blub.brewaddon.utils.misc.chatUtils.ChatUtilsHelper;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

public class ChatPrefix extends Module {
    public ChatPrefix() {
        super(BrewAddon.CATEGORY, "chat-prefix", "Customize the [Meteor] chat prefix and module colors.");
    }

    private final SettingGroup sgMain = settings.createGroup("Main Prefix");
    private final SettingGroup sgModule = settings.createGroup("Module Prefix");

    private final Setting<String> prefix = sgMain.add(new StringSetting.Builder()
        .name("prefix-text")
        .description("The main prefix text (e.g., 'Meteor', 'MyAddon')")
        .defaultValue("Motor")
        .onChanged(this::updateMainPrefix)
        .build()
    );

    private final Setting<SettingColor> mainColor = sgMain.add(new ColorSetting.Builder()
        .name("prefix-color")
        .description("Color of the main prefix")
        .defaultValue(new SettingColor(170, 0, 170))
        .onChanged(this::updateMainPrefix)
        .build()
    );

    private final Setting<SettingColor> moduleColor = sgModule.add(new ColorSetting.Builder()
        .name("module-color")
        .description("Color of module/class name prefixes (e.g., [AutoTotem], [Flight])")
        .defaultValue(new SettingColor(170, 0, 255))
        .onChanged(this::updateModuleColor)
        .build()
    );

    @Override
    public void onActivate() {
        super.onActivate();
        updateMainPrefix();
        updateModuleColor();
    }

    @Override
    public void onDeactivate() {
        super.onDeactivate();
        ChatUtilsHelper.resetToDefault();
    }

    private void updateMainPrefix() {
        if (isActive()) {
            ChatUtilsHelper.setCustomPrefix(prefix.get(), mainColor.get().getPacked());
        }
    }

    private void updateMainPrefix(String newValue) {
        updateMainPrefix();
    }

    private void updateMainPrefix(SettingColor newValue) {
        updateMainPrefix();
    }

    private void updateModuleColor() {
        if (isActive()) {
            ChatUtilsHelper.setCustomModulePrefixColor(moduleColor.get().getPacked());
        }
    }

    private void updateModuleColor(SettingColor newValue) {
        updateModuleColor();
    }
}

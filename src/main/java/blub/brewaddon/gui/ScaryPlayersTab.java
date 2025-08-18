package blub.brewaddon.gui;

import blub.brewaddon.utils.misc.Lists;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.tabs.Tab;
import meteordevelopment.meteorclient.gui.tabs.TabScreen;
import meteordevelopment.meteorclient.gui.tabs.WindowTabScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.input.WTextBox;
import meteordevelopment.meteorclient.gui.widgets.pressable.WMinus;
import meteordevelopment.meteorclient.gui.widgets.pressable.WPlus;
import net.minecraft.client.gui.screen.Screen;

import java.util.UUID;

public class ScaryPlayersTab extends Tab {
    public ScaryPlayersTab() {
        super("Scary Players");
    }

    @Override
    public TabScreen createScreen(GuiTheme theme) {
        return new BlacklistScreen(theme, this);
    }

    @Override
    public boolean isScreen(Screen screen) {
        return screen instanceof BlacklistScreen;
    }

    private static class BlacklistScreen extends WindowTabScreen {
        public BlacklistScreen(GuiTheme theme, Tab tab) {
            super(theme, tab);
        }

        @Override
        public void initWidgets() {
            WTable blacklistTable = add(theme.table()).expandX().widget();

            for (UUID uuid : Lists.getBlacklist()) {
                String name = Lists.getBlacklistName(uuid);
                blacklistTable.add(theme.label(name)).expandCellX();
                WMinus remove = blacklistTable.add(theme.minus()).right().widget();
                remove.action = () -> {
                    Lists.removeFromBlacklist(name);
                    reload();
                };
                blacklistTable.row();
            }

            add(theme.horizontalSeparator()).expandX();

            WTable addTable = add(theme.table()).expandX().widget();
            WTextBox nameInput = addTable.add(theme.textBox("", "Username")).minWidth(400).expandX().widget();
            WPlus addButton = addTable.add(theme.plus()).widget();
            addButton.action = () -> {
                String name = nameInput.get().trim();
                if (!name.isEmpty()) {
                    Lists.addToBlacklist(name);
                    nameInput.set("");
                    reload();
                }
            };

            addTable.row();
        }

        @Override
        public void reload() {
            clear();
            initWidgets();
        }
    }
}

// Mostly much vibe coded by egli the idiot
package blub.brewaddon.hud;

import blub.brewaddon.BrewAddon;

import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.systems.hud.HudElementInfo;
import meteordevelopment.meteorclient.systems.hud.HudRenderer;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;

import net.minecraft.network.packet.s2c.play.StatisticsS2CPacket;
import net.minecraft.network.packet.c2s.play.ClientStatusC2SPacket;
import net.minecraft.stat.Stats;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class StatisticsHud extends HudElement {
    public static final HudElementInfo<StatisticsHud> INFO = new HudElementInfo<>(
        BrewAddon.HUD_GROUP,
        "statistics",
        "Displays information about your stats.",
        StatisticsHud::new
    );

    public StatisticsHud() {
        super(INFO);
    }

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgDisplay = settings.createGroup("Display");

    private final Setting<SettingColor> textColor = sgGeneral.add(new ColorSetting.Builder()
        .name("text-color")
        .description("Text color for labels.")
        .defaultValue(new SettingColor(255, 255, 0, 255))
        .build());

    private final Setting<SettingColor> valueColor = sgGeneral.add(new ColorSetting.Builder()
        .name("value-color")
        .description("Color for the values.")
        .defaultValue(new SettingColor(255, 255, 255, 255))
        .build());

    private final Setting<Boolean> shadow = sgGeneral.add(new BoolSetting.Builder()
        .name("text-shadow")
        .defaultValue(true)
        .build());

    private final Setting<Boolean> showPlayTime = sgDisplay.add(new BoolSetting.Builder()
        .name("show-play-time")
        .description("Display play time.")
        .defaultValue(true)
        .build());

    private final Setting<Boolean> showJumps = sgDisplay.add(new BoolSetting.Builder()
        .name("show-jumps")
        .description("Display jumps.")
        .defaultValue(true)
        .build());

    private final Setting<Boolean> showDistanceWalked = sgDisplay.add(new BoolSetting.Builder()
        .name("show-distance-walked")
        .description("Display distance walked.")
        .defaultValue(true)
        .build());

    private final Setting<Boolean> showPlayerKills = sgDisplay.add(new BoolSetting.Builder()
        .name("show-player-kills")
        .description("Display player kills.")
        .defaultValue(true)
        .build());

    private final Setting<Boolean> showMobKills = sgDisplay.add(new BoolSetting.Builder()
        .name("show-mob-kills")
        .description("Display mob kills.")
        .defaultValue(true)
        .build());

    private final Setting<Boolean> showDeaths = sgDisplay.add(new BoolSetting.Builder()
        .name("show-deaths")
        .description("Display deaths.")
        .defaultValue(true)
        .build());

    private final Setting<Boolean> showDamageDealt = sgDisplay.add(new BoolSetting.Builder()
        .name("show-damage-dealt")
        .description("Display damage dealt.")
        .defaultValue(true)
        .build());

    private double maxWidth = 0;
    private double lastHeight = 0;
    private long lastUpdateTime = 0;

    @EventHandler
    private void onPacketReceive(PacketEvent.Receive event) {
        if (event.packet instanceof StatisticsS2CPacket) {
            lastUpdateTime = System.currentTimeMillis();
        }
    }

    private void renderText(HudRenderer renderer, String text, double textX, double textY, SettingColor color) {
        renderer.text(text, textX, textY, color, shadow.get());
        maxWidth = Math.max(maxWidth, renderer.textWidth(text));
    }

    private void renderStatistic(HudRenderer renderer, String label, String value, double x, double y) {
        renderer.text(label + ": ", x, y, textColor.get(), shadow.get());
        double labelWidth = renderer.textWidth(label + ": ");

        renderer.text(value, x + labelWidth, y, valueColor.get(), shadow.get());

        double totalWidth = labelWidth + renderer.textWidth(value);
        maxWidth = Math.max(maxWidth, totalWidth);
    }

    private String formatPlayTime(int ticks) {
        long totalSeconds = ticks / 20L;
        long days = totalSeconds / 86400;
        long hours = (totalSeconds % 86400) / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (days > 0) {
            return String.format("%dd %02dh %02dm %02ds", days, hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%02dm %02ds", minutes, seconds);
        } else {
            return seconds + "s";
        }
    }

    private String formatDistance(long blocks) {
        if (blocks >= 1000) {
            return String.format("%.2f km", blocks / 1000.0);
        } else {
            return blocks + " m";
        }
    }

    private String formatNumber(long value) {
        if (value >= 1_000_000_000) return String.format("%.2fB", value / 1_000_000_000.0);
        else if (value >= 1_000_000) return String.format("%.2fM", value / 1_000_000.0);
        else if (value >= 1_000) return String.format("%.2fK", value / 1_000.0);
        else return String.valueOf(value);
    }

    private String formatDamage(int damageInHalfHearts) {
        return String.format("%.1f", damageInHalfHearts / 2.0f);
    }

    @Override
    public void render(HudRenderer renderer) {
        if (mc.player == null || mc.player.getStatHandler() == null) {
            renderText(renderer, "No player data available", this.x, this.y, textColor.get());
            return;
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastUpdateTime > 1000) {
            if (mc.getNetworkHandler() != null) {
                mc.getNetworkHandler().sendPacket(
                    new ClientStatusC2SPacket(ClientStatusC2SPacket.Mode.REQUEST_STATS)
                );
            }
            lastUpdateTime = currentTime;
        }

        maxWidth = 0;
        double x = this.x;
        double y = this.y;
        double startY = y;

        var statHandler = mc.player.getStatHandler();

        try {
            int playerKills = statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAYER_KILLS));
            int playTimeTicks = statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.PLAY_TIME));
            int damageDealt = statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.DAMAGE_DEALT));
            int mobKills = statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.MOB_KILLS));
            int deaths = statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.DEATHS));
            long blocksWalked = statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.WALK_ONE_CM)) / 100; // cm → meters
            int jumps = statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.JUMP));

            if (showPlayTime.get()) {
                renderStatistic(renderer, "Play Time", formatPlayTime(playTimeTicks), x, y);
                y += renderer.textHeight();
            }

            if (showJumps.get()) {
                renderStatistic(renderer, "Jumps", formatNumber(jumps), x, y);
                y += renderer.textHeight();
            }

            if (showDistanceWalked.get()) {
                renderStatistic(renderer, "Distance Walked", formatDistance(blocksWalked), x, y);
                y += renderer.textHeight();
            }

            if (showPlayerKills.get()) {
                renderStatistic(renderer, "Player Kills", formatNumber(playerKills), x, y);
                y += renderer.textHeight();
            }

            if (showMobKills.get()) {
                renderStatistic(renderer, "Mob Kills", formatNumber(mobKills), x, y);
                y += renderer.textHeight();
            }

            if (showDeaths.get()) {
                renderStatistic(renderer, "Deaths", formatNumber(deaths), x, y);
                y += renderer.textHeight();
            }

            if (showDamageDealt.get()) {
                renderStatistic(renderer, "Damage Dealt", formatDamage(damageDealt) + " hearts", x, y);
                y += renderer.textHeight();
            }

            if (y == startY) {
                renderText(renderer, "No statistics enabled", x, y, textColor.get());
                y += renderer.textHeight();
            }

        } catch (Exception e) {
            renderText(renderer, "Error loading stats: " + e.getMessage(), x, y, textColor.get());
            y += renderer.textHeight();
        }

        lastHeight = y - startY;
    }

    @Override
    public int getWidth() {
        return (int) Math.ceil(maxWidth);
    }

    @Override
    public int getHeight() {
        return (int) Math.ceil(lastHeight);
    }
}

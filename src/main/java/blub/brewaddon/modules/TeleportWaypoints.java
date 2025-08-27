package blub.brewaddon.modules;

import blub.brewaddon.BrewAddon;
import blub.brewaddon.utils.movement.Movement;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import blub.brewaddon.utils.misc.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class TeleportWaypoints extends Module {

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");
    private final SettingGroup sgKeybinds = settings.createGroup("Keybinds");

    // General Settings
    private final Setting<Boolean> autoTeleport = sgGeneral.add(new BoolSetting.Builder()
        .name("auto-teleport")
        .description("Automatically teleport when adding waypoints.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Double> teleportDelay = sgGeneral.add(new DoubleSetting.Builder()
        .name("teleport-delay")
        .description("Delay in seconds between teleports when auto-teleporting.")
        .defaultValue(0.5)
        .min(0)
        .sliderMax(5)
        .build()
    );

    // Render Settings
    private final Setting<Boolean> render = sgRender.add(new BoolSetting.Builder()
        .name("render")
        .description("Render waypoint cubes.")
        .defaultValue(true)
        .build()
    );

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The side color of waypoint cubes.")
        .defaultValue(new SettingColor(255, 255, 255, 50))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The line color of waypoint cubes.")
        .defaultValue(new SettingColor(255, 255, 255, 255))
        .build()
    );

    // Keybind Settings
    private final Setting<Keybind> addWaypointKey = sgKeybinds.add(new KeybindSetting.Builder()
        .name("add-waypoint")
        .description("Key to add a waypoint at current position.")
        .defaultValue(Keybind.none())
        .action(() -> addWaypoint())
        .build()
    );

    private final Setting<Keybind> teleportKey = sgKeybinds.add(new KeybindSetting.Builder()
        .name("teleport")
        .description("Key to teleport through all waypoints.")
        .defaultValue(Keybind.none())
        .action(() -> startTeleporting())
        .build()
    );

    private final Setting<Keybind> clearWaypointsKey = sgKeybinds.add(new KeybindSetting.Builder()
        .name("clear-waypoints")
        .description("Key to clear all waypoints.")
        .defaultValue(Keybind.none())
        .action(() -> clearWaypoints())
        .build()
    );

    private final List<Vec3d> waypoints = new ArrayList<>();
    private boolean isTeleporting = false;
    private int teleportTicks = 0;

    public TeleportWaypoints() {
        super(BrewAddon.CATEGORY, "teleport-waypoints", "Create waypoints and teleport through them.");
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (!render.get() || waypoints.isEmpty()) return;

        for (Vec3d waypoint : waypoints) {
            BlockPos blockPos = new BlockPos((int) waypoint.x, (int) waypoint.y, (int) waypoint.z);

            RenderUtils.renderBlock(
                event,
                blockPos,
                sideColor.get(),
                lineColor.get(),
                shapeMode.get()
            );
        }
    }

    private void addWaypoint() {
        if (mc.player == null) return;

        Vec3d playerPos = mc.player.getPos();
        Vec3d waypoint = new Vec3d(
            Math.floor(playerPos.x) + 0.5,
            Math.floor(playerPos.y),
            Math.floor(playerPos.z) + 0.5
        );

        waypoints.add(waypoint);
        info("Added waypoint at: " + String.format("%.1f, %.1f, %.1f", waypoint.x, waypoint.y, waypoint.z));
        info("Total waypoints: " + waypoints.size());

        if (autoTeleport.get()) {
            startTeleporting();
        }
    }

    private void startTeleporting() {
        if (waypoints.isEmpty()) {
            error("No waypoints to teleport to!");
            return;
        }

        try {
            Movement.teleport(new ArrayList<>(waypoints), true, false, (int) (teleportDelay.get() * 1000));
            info("Teleporting through " + waypoints.size() + " waypoints");
        } catch (Exception e) {
            error("Failed to teleport: " + e.getMessage());
        }
    }

    private void clearWaypoints() {
        int count = waypoints.size();
        waypoints.clear();
        isTeleporting = false;
        teleportTicks = 0;
        info("Cleared " + count + " waypoints");
    }

    @Override
    public void onActivate() {
        waypoints.clear();
        isTeleporting = false;
        teleportTicks = 0;
    }

    @Override
    public void onDeactivate() {
        waypoints.clear();
        isTeleporting = false;
        teleportTicks = 0;
    }

    // Utility methods for external access
    public List<Vec3d> getWaypoints() {
        return new ArrayList<>(waypoints);
    }

    public void addWaypointAt(Vec3d position) {
        waypoints.add(position);
        info("Added waypoint at: " + String.format("%.1f, %.1f, %.1f", position.x, position.y, position.z));
    }

    public void addWaypointAt(double x, double y, double z) {
        addWaypointAt(new Vec3d(x, y, z));
    }

    public int getWaypointCount() {
        return waypoints.size();
    }
}

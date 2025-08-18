package blub.brewaddon.modules;

import blub.brewaddon.BrewAddon;
import blub.brewaddon.utils.misc.HitResults;
import blub.brewaddon.utils.misc.RenderUtils;
import blub.brewaddon.utils.movement.Movement;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class ClickTp extends Module {

    private final SettingGroup sgGeneral;
    private final SettingGroup sgRender;
    private final Setting<Double> maxTpRange;
    private final Setting<Double> minTpRange;
    private final Setting<Boolean> debug;

    private final Setting<Boolean> render;
    private final Setting<ShapeMode> shapeMode;
    private final Setting<SettingColor> sideColor;
    private final Setting<SettingColor> lineColor;

    private final Setting<Keybind> clickTP;

    public ClickTp() {
        super(BrewAddon.CATEGORY, "b-click-tp", "teleport to the block you are looking at.");

        this.sgGeneral = this.settings.getDefaultGroup();
        this.sgRender = this.settings.createGroup("Render");

        this.render = this.sgRender.add(new BoolSetting.Builder()
            .name("render")
            .description("Renders a block overlay where you will be teleported.")
            .defaultValue(true)
            .build());

        this.shapeMode = this.sgRender.add(new EnumSetting.Builder<ShapeMode>()
            .name("shape-mode")
            .description("How the shapes are rendered.")
            .defaultValue(ShapeMode.Both)
            .visible(this.render::get)
            .build());

        this.sideColor = this.sgRender.add(new ColorSetting.Builder()
            .name("side-color-solid-block")
            .description("The color of the sides of the blocks being rendered.")
            .defaultValue(new SettingColor(255, 0, 255, 15))
            .visible(this.render::get)
            .build());

        this.lineColor = this.sgRender.add(new ColorSetting.Builder()
            .name("line-color-solid-block")
            .description("The color of the lines of the blocks being rendered.")
            .defaultValue(new SettingColor(255, 0, 255, 255))
            .visible(this.render::get)
            .build());

        this.maxTpRange = this.sgGeneral.add(new DoubleSetting.Builder()
            .name("max-tp-range")
            .description("Maximum teleportation range.")
            .defaultValue(210)
            .sliderMin(1)
            .sliderMax(5000)
            .build()
        );

        this.minTpRange = this.sgGeneral.add(new DoubleSetting.Builder()
            .name("min-tp-range")
            .description("Minimum teleportation range.")
            .defaultValue(5)
            .sliderMin(0)
            .max(maxTpRange.get() - 1)
            .build()
        );

        this.debug = this.sgGeneral.add(new BoolSetting.Builder()
            .name("debug")
            .description("Skibidi skid utils.")
            .defaultValue(false)
            .build());

        this.clickTP = this.sgGeneral.add(new KeybindSetting.Builder()
            .name("teleport-keybind")
            .description("Teleports you to where you are looking.")
            .defaultValue(Keybind.none())
            .action(() -> {
                BlockPos targetBlock = HitResults.getStaredBlock(this.maxTpRange.get());
                Vec3d targetPos = new Vec3d(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());

                if (targetPos.distanceTo(mc.player.getPos()) < minTpRange.get()) return;
                if (targetBlock != null) {
                    Vec3d teleportPosition = new Vec3d(targetBlock.getX() + 0.5, targetBlock.getY(), targetBlock.getZ() + 0.5);

                    Movement.teleport(teleportPosition, true, false);
                }
            })
            .build());
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (this.render.get()) {
            BlockPos targetBlock = HitResults.getStaredBlock(this.maxTpRange.get()).add(0, -1, 0);
            Vec3d targetPos = new Vec3d(targetBlock.getX(), targetBlock.getY(), targetBlock.getZ());

            if (targetPos.distanceTo(mc.player.getPos()) < minTpRange.get()) return;

            if (targetBlock != null) {
                RenderUtils.renderBlock(
                    event,
                    targetBlock,
                    this.sideColor.get(),
                    this.lineColor.get(),
                    this.shapeMode.get()
                );
            }
        }
    }
}

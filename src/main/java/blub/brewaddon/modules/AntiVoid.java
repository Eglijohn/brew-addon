package blub.brewaddon.modules;

import blub.brewaddon.BrewAddon;
import blub.brewaddon.utils.movement.Movement;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.movement.Flight;
import meteordevelopment.orbit.EventHandler;

import net.minecraft.util.math.Vec3d;

public class AntiVoid extends Module {
    public AntiVoid() {
        super(BrewAddon.CATEGORY, "anti-void", "Prevents you from falling into the void by teleporting you back.");
    }

    private final SettingGroup sgOverworld = settings.createGroup("Overworld");
    private final SettingGroup sgEnd = settings.createGroup("The End");

    private final Setting<Integer> triggerHeightOverworld = sgOverworld.add(new IntSetting.Builder()
        .name("trigger-height")
        .description("The height at you will be teleported back.")
        .defaultValue(-120)
        .sliderMax(-64)
        .sliderMin(-120)
        .build()
    );

    private final Setting<Integer> tpHeightOverworld = sgOverworld.add(new IntSetting.Builder()
        .name("tp-height")
        .description("The height to teleport to when falling into the void.")
        .defaultValue(-100)
        .sliderMax(0)
        .sliderMin(-120)
        .build()
    );

    private final Setting<Integer> triggerHeightEnd = sgEnd.add(new IntSetting.Builder()
        .name("trigger-height")
        .description("The height at you will be teleported back in The End.")
        .defaultValue(-60)
        .sliderMax(80)
        .sliderMin(0)
        .build()
    );

    private final Setting<Integer> tpHeightEnd = sgEnd.add(new IntSetting.Builder()
        .name("tp-height")
        .description("The height to teleport to when falling into the void in The End.")
        .defaultValue(40)
        .sliderMax(80)
        .sliderMin(-60)
        .build()
    );

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        String dimension = mc.world.getDimensionEntry().getIdAsString();

        if (mc.player.getY() < triggerHeightEnd.get() && dimension.equals("minecraft:the_end")) {
            Vec3d tpPos = new Vec3d(mc.player.getX(), tpHeightEnd.get(), mc.player.getZ());
            Flight flight = Modules.get().get(Flight.class);

            Movement.teleport(tpPos, true, false);

            if (!flight.isActive()) {
                flight.toggle();
            }

            info("Saving you from the void!");
        } else if (mc.player.getY() < triggerHeightOverworld.get()) {
            Vec3d tpPos = new Vec3d(mc.player.getX(), tpHeightOverworld.get(), mc.player.getZ());
            Flight flight = Modules.get().get(Flight.class);

            Movement.teleport(tpPos, true, false);

            if (!flight.isActive()) {
                flight.toggle();
            }

            info("Saving you from the void!");
        }
    }
}

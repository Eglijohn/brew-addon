package blub.brewaddon.modules;

import blub.brewaddon.BrewAddon;
import blub.brewaddon.utils.misc.HitResults;
import blub.brewaddon.utils.movement.Movement;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class InfiniteReach extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Boolean> useMace = sgGeneral.add(new BoolSetting.Builder()
        .name("use-mace")
        .description("Mace Attack")
        .build()
    );

    private final Setting<Integer> attackHeight = sgGeneral.add(new IntSetting.Builder()
        .name("attack-height")
        .description("The height at which the mace attack will be executed.")
        .defaultValue(35)
        .min(0)
        .max(170)
        .visible(useMace::get)
        .build()
    );

    private final Setting<Integer> maxReach = sgGeneral.add(new IntSetting.Builder()
        .name("max-reach")
        .description("The maximum reach distance.")
        .defaultValue(210)
        .min(5)
        .sliderMax(170)
        .build()
    );

    private boolean attackKeyWasPressed = false;

    public InfiniteReach() {
        super(BrewAddon.CATEGORY, "infinite-reach", "Looong arm");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        boolean attackKeyIsPressed = mc.options.attackKey.isPressed();

        if (attackKeyIsPressed && !attackKeyWasPressed) {
            HitResult hitResult = HitResults.getCrosshairTarget(mc.player, maxReach.get(), false, entity -> true);
            if (hitResult instanceof EntityHitResult entityHit) {
                attack(entityHit.getEntity());
            }
        }

        attackKeyWasPressed = attackKeyIsPressed;
    }

    private boolean isHoldingMace() {
        ItemStack mainHand = mc.player.getMainHandStack();
        ItemStack offHand = mc.player.getOffHandStack();
        return mainHand.getItem() == Items.MACE || offHand.getItem() == Items.MACE;
    }

    private void attack(Entity target) {
        if (target == null || !target.isAlive()) return;

        Vec3d originalPos = mc.player.getPos();
        Vec3d targetPos = target.getPos().add(0, 0.2, 0);

        if (useMace.get() && isHoldingMace()) {
            Movement.teleport(targetPos, false, false);
            Movement.teleport(originalPos.add(0, attackHeight.get(), 0), false, false);
            Movement.teleport(targetPos, false, false);
            mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(target, mc.player.isSneaking()));
            Movement.teleport(originalPos, false, false);
        } else {
            Movement.teleport(targetPos, false, false);
            mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(target, mc.player.isSneaking()));
            Movement.teleport(originalPos, false, false);
        }
    }
}

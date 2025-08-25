package blub.brewaddon.modules;

import blub.brewaddon.BrewAddon;
import blub.brewaddon.utils.attack.Attack;
import blub.brewaddon.utils.misc.HitResults;
import blub.brewaddon.utils.movement.Movement;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class InfiniteReach extends Module {
    private boolean attackKeyWasPressed = false;
    private boolean interactKeyWasPressed = false;
    private static final double VANILLA_REACH = 4.5;

    private BlockPos currentBreakingBlock = null;
    private Direction currentBreakingDirection = null;
    private BlockState currentBlockState = null;
    private double breakProgress = 0.0;
    private boolean hasStartedBreaking = false;

    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> maxReach = sgGeneral.add(new IntSetting.Builder()
        .name("max-reach")
        .description("The maximum reach distance.")
        .defaultValue(210)
        .min(5)
        .sliderMax(210)
        .build()
    );

    private final Setting<Boolean> maceAttack = sgGeneral.add(new BoolSetting.Builder()
        .name("mace-attack")
        .description("Maceattack entities.")
        .defaultValue(false)
        .build()
    );

    private final Setting<Integer> attackHeight = sgGeneral.add(new IntSetting.Builder()
        .name("attack-height")
        .description("Maceattack height.")
        .defaultValue(35)
        .min(1)
        .max(170)
        .visible(maceAttack::get)
        .build()
    );

    private final Setting<Boolean> debug = sgGeneral.add(new BoolSetting.Builder()
        .name("Debug")
        .description("Debugging shit")
        .defaultValue(false)
        .build()
    );

    public InfiniteReach() {
        super(BrewAddon.CATEGORY, "b-reach", "Lets you interact with blocks and entities far away.");
    }

    private double getTargetDistance(Vec3d target) {
        if (mc.player == null) return 0;
        return mc.player.getEyePos().distanceTo(target);
    }

    private void attack() {
        if (mc.player == null) return;

        HitResult hitResult = HitResults.getCrosshairTarget(mc.player, maxReach.get(), true, entity -> true);
        if (hitResult instanceof EntityHitResult entityHit) {
            Entity target = entityHit.getEntity();
            if (target != null && getTargetDistance(target.getPos()) > VANILLA_REACH && !(target instanceof ItemEntity)) {
                if (debug.get()) { info("Attacking " + target.getName().getString() + " at distance " + getTargetDistance(target.getPos())); }
                Attack.attack(target, maceAttack.get(), attackHeight.get(), false);
            }
        }
    }

    private void interact() {
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        HitResult hitResult = HitResults.getCrosshairTarget(mc.player, maxReach.get(), false, entity -> true);
        if (hitResult instanceof BlockHitResult blockHit) {
            BlockPos target = blockHit.getBlockPos();
            if (getTargetDistance(Vec3d.ofCenter(target)) <= VANILLA_REACH) return;

            Vec3d hitPos = Vec3d.ofCenter(target);
            BlockHitResult blockHitResult = new BlockHitResult(hitPos, blockHit.getSide(), target, false);
            Block block = mc.world.getBlockState(blockHitResult.getBlockPos()).getBlock();

            Vec3d originalPos = mc.player.getPos();

            if (debug.get()) {
                info("Interacting with " + block.getName().getString() + " at distance " + getTargetDistance(hitPos));
            }
            Movement.teleport(target.toCenterPos().add(0, 2, 0), false, false);
            mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, blockHitResult, 0));
            if (isContainerBlock(block)) {
                mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, blockHitResult, 0));
                mc.getNetworkHandler().sendPacket(new PlayerInteractBlockC2SPacket(Hand.MAIN_HAND, blockHitResult, 0));
            }
            Movement.teleport(originalPos, true, false);
        }
    }

    private void breakBlock() {
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        HitResult hitResult = HitResults.getCrosshairTarget(mc.player, maxReach.get(), false, entity -> true);
        if (hitResult instanceof BlockHitResult blockHit) {
            BlockPos target = blockHit.getBlockPos();
            if (getTargetDistance(Vec3d.ofCenter(target)) <= VANILLA_REACH) return;

            if (currentBreakingBlock == null || !currentBreakingBlock.equals(target)) {
                resetBreaking();
                currentBreakingBlock = target;
                currentBreakingDirection = blockHit.getSide();
                currentBlockState = mc.world.getBlockState(target);
                breakProgress = 0.0;
                hasStartedBreaking = false;
            }

            if (!hasStartedBreaking) {
                Vec3d originalPos = mc.player.getPos();

                Movement.teleport(target.toCenterPos().add(0, 2, 0), false, false);
                mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
                    PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, target, currentBreakingDirection));
                Movement.teleport(originalPos, true, true);
                if (debug.get()) {
                    info("Sent startBreaking packet for " + mc.world.getBlockState(target).getBlock().getName().getString() + " at distance " + getTargetDistance(Vec3d.ofCenter(target)));
                }

                hasStartedBreaking = true;
            }
        }
    }

    private void updateBreakProgress() {
        if (currentBreakingBlock == null || currentBlockState == null || mc.player == null) return;

        double bestScore = -1.0;
        int bestSlot = -1;

        for (int i = 0; i < 9; i++) {
            double score = (double) mc.player.getInventory().getStack(i).getMiningSpeedMultiplier(currentBlockState);
            if (score > bestScore) {
                bestScore = score;
                bestSlot = i;
            }
        }

        int slot = bestSlot != -1 ? bestSlot : mc.player.getInventory().getSelectedSlot();
        double breakDelta = BlockUtils.getBreakDelta(slot, currentBlockState);
        breakProgress += breakDelta;

        if (breakProgress >= 1.0) {
            finishBreaking();
        }
    }

    private void finishBreaking() {
        if (currentBreakingBlock == null || mc.getNetworkHandler() == null) return;

        Vec3d originalPos = mc.player.getPos();
        float originalYaw = mc.player.getYaw();
        float originalPitch = mc.player.getPitch();

        Movement.teleport(currentBreakingBlock.toCenterPos().add(0, 2, 0), false, false);
        mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(
            PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, currentBreakingBlock, currentBreakingDirection));
        Movement.teleport(originalPos, true, false);
        if (debug.get()) {
            info("Sent stopBreaking packet for " + mc.world.getBlockState(currentBreakingBlock).getBlock().getName().getString() + " at distance " + getTargetDistance(Vec3d.ofCenter(currentBreakingBlock)));
        }

        resetBreaking();
    }

    private void resetBreaking() {
        currentBreakingBlock = null;
        currentBreakingDirection = null;
        currentBlockState = null;
        breakProgress = 0.0;
        hasStartedBreaking = false;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null) return;

        boolean attackKeyIsPressed = mc.options.attackKey.isPressed();
        boolean interactKeyIsPressed = mc.options.useKey.isPressed();

        if (attackKeyIsPressed && !attackKeyWasPressed) {
            HitResult hitResult = HitResults.getCrosshairTarget(mc.player, maxReach.get(), true, entity -> true);
            if (hitResult instanceof EntityHitResult) {
                attack();
            } else {
                breakBlock();
            }
        }

        if (attackKeyIsPressed && hasStartedBreaking) {
            updateBreakProgress();
        }

        if (!attackKeyIsPressed && hasStartedBreaking) {
            resetBreaking();
        }

        if (interactKeyIsPressed && !interactKeyWasPressed) {
            interact();
        }

        attackKeyWasPressed = attackKeyIsPressed;
        interactKeyWasPressed = interactKeyIsPressed;
    }

    private boolean isContainerBlock(Block block) {
        return block == Blocks.CHEST ||
            block == Blocks.ENDER_CHEST ||
            block == Blocks.BARREL;
    }

    @Override
    public void onDeactivate() {
        resetBreaking();
        super.onDeactivate();
    }
}

package blub.brewaddon.utils.attack;

import blub.brewaddon.utils.misc.AsyncTaskQueue;
import blub.brewaddon.utils.movement.Movement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Attack {
    public static void attack(Entity target, Boolean useMace, Integer attackHeight, Boolean autoEquipMace) {
        if (target == null || !target.isAlive() || mc.player == null) return;

        Vec3d originalPos = mc.player.getPos();
        Vec3d targetPos = target.getPos().add(0, 0.2, 0);

        // Integer originalSlot = mc.player.getInventory().selectedSlot;

        List<Vec3d> positions = List.of(
            targetPos,
            targetPos.add(0, attackHeight, 0),
            targetPos,
            originalPos
        );

        List<Runnable> tasks = new ArrayList<>();

        if (autoEquipMace) {
            equipMace();
        }

        if (useMace && isHoldingMace()) {
            tasks.add(() -> Movement.execute(positions, 0, 2, true, false));
            tasks.add(() -> mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(target, mc.player.isSneaking())));
            tasks.add(() -> Movement.execute(positions, 3, true, false));
        } else {
            tasks.add(() -> Movement.execute(positions, 0, true, false));
            tasks.add(() -> mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(target, mc.player.isSneaking())));
            tasks.add(() -> Movement.execute(positions, 3, true, false));
        }
        AsyncTaskQueue.execute(tasks);
    }

    private static void equipMace() {
        if (!isHoldingMace()) {
            assert mc.player != null;
            PlayerInventory inventory = mc.player.getInventory();

            for (int i = 0; i < 9; i++) {
                ItemStack stack = inventory.getStack(i);
                if (stack.getItem() == Items.MACE) {
                    inventory.selectedSlot = i;
                    return;
                }
            }
        }
    }

    private static boolean isHoldingMace() {
        if (mc.player == null) return false;
        ItemStack mainHand = mc.player.getMainHandStack();
        ItemStack offHand = mc.player.getOffHandStack();
        return mainHand.getItem() == Items.MACE || offHand.getItem() == Items.MACE;
    }
}

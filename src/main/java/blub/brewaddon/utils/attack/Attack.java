package blub.brewaddon.utils.attack;

import blub.brewaddon.utils.movement.Movement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.player.ChatUtils.info;

public class Attack {
    public static void attack(Entity target, Boolean useMace, Integer attackHeight, Boolean autoEquipMace) {
        if (target == null || !target.isAlive()) return;

        Vec3d originalPos = mc.player.getPos();
        Vec3d targetPos = target.getPos().add(0, 0.2, 0);

        Integer originalSlot = mc.player.getInventory().selectedSlot;

        if (autoEquipMace) {
            equipMace();
        }

        List<Vec3d> positions = List.of(
            targetPos,
            targetPos.add(0, attackHeight, 0),
            targetPos,
            originalPos
        );

        if (useMace && isHoldingMace()) {
            info("Using mace to attack " + target.getName().getString());
            Movement.execute(positions, 0, 2, false, false);
            mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(target, mc.player.isSneaking()));
            Movement.execute(positions, 3, false, false);
        } else {
            Movement.execute(positions, 0, false, false);
            mc.player.networkHandler.sendPacket(PlayerInteractEntityC2SPacket.attack(target, mc.player.isSneaking()));
            Movement.execute(positions, 3, false, false);
        }

        if (autoEquipMace) {
            //mc.player.getInventory().selectedSlot = originalSlot;
        }
    }

    private static void equipMace() {
        if (!isHoldingMace()) {
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
        ItemStack mainHand = mc.player.getMainHandStack();
        ItemStack offHand = mc.player.getOffHandStack();
        return mainHand.getItem() == Items.MACE || offHand.getItem() == Items.MACE;
    }
}

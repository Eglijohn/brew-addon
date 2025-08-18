package blub.brewaddon.modules;

import blub.brewaddon.BrewAddon;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class TridentDupe extends Module {

    private static boolean allowActions = true;
    private static int state = -1;
    private static int tickCounter;
    private static boolean repeat = false;

    public TridentDupe() {
        super(BrewAddon.CATEGORY, "b-trident-dupe", "Trident Dupe by Superheld2006");
    }

    @Override
    public void onActivate() {
        repeat = true;
        state = 0;
        info("Starting dupe - will stop when you leave, die or don't hold a trident for 5s");
    }

    @Override
    public void onDeactivate() {
        info("Stopping");
        repeat = false;
    }

    @EventHandler
    private void onSendPacket(PacketEvent.Send event) {
        if (!checkPacket(event.packet)) {
            event.cancel();
        }
    }


    public static boolean checkPacket(Packet<?> packet) {
        return !(packet instanceof PlayerActionC2SPacket) ? true : allowActions;
    }



    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (!(mc.currentScreen instanceof DeathScreen) && mc.world != null) {
            switch (state) {
                case 0:
                    if (!mc.player.getInventory().getMainHandStack().isOf(Items.TRIDENT)) {
                        return;
                    }

                    tickCounter = 0;
                    mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                    allowActions = false;
                    state = 1;
                    break;
                case 1:
                    if (tickCounter++ > 10) {
                        state = 2;
                        tickCounter = 0;
                    }
                    break;
                case 2:
                    int slot = mc.player.getInventory().selectedSlot;
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 3, slot, SlotActionType.SWAP, mc.player);
                    allowActions = true;
                    PlayerActionC2SPacket packet2 = new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, Direction.DOWN, 0);
                    mc.getNetworkHandler().sendPacket(packet2);
                    mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, 3, 0, SlotActionType.THROW, mc.player);
                    state = repeat ? 0 : -1;
            }

        } else {
            repeat = false;
            tickCounter = 0;
            allowActions = true;
            state = -1;
        }
    }


}

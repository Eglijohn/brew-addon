package blub.brewaddon.utils.movement;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

import static meteordevelopment.meteorclient.MeteorClient.mc;
import static meteordevelopment.meteorclient.utils.player.ChatUtils.info;

public class Movement {
    public static void teleport(Vec3d pos, boolean setClientSided, Boolean onGround) {
        if (mc.player != null) {
            Integer distance = (int) mc.player.getPos().distanceTo(pos);
            Double packetsRequired = Math.ceil(distance / 10.0) - 1;
            if (packetsRequired > 20) return;

            // Spam packets
            for (int i = 0; i < packetsRequired; i++) {
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), onGround));
            }

            // Send final packet
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.x, pos.y, pos.z, onGround));
            if (setClientSided) mc.player.setPosition(pos); // Set client-sided position
        }
    }
}

package blub.brewaddon.utils.movement;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Movement {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * Teleport to multiple positions
     *
     * @param positions The list of positions to teleport to.
     * @param setClientSided Whether to set the client-sided position.
     * @param onGround Whether the player is on the ground.
     */
    public static void teleport(List<Vec3d> positions, boolean setClientSided, boolean onGround) {
        if (mc.player != null) {
            for (Vec3d pos : positions) {
                Integer packetsRequired = calculatePackets(pos);

                sendPackets(onGround, packetsRequired); // Spam packets
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(pos.x, pos.y, pos.z, onGround)); // Send final packet
                if (setClientSided) mc.player.setPosition(pos); // Set client-sided position
            }
        }
    }


    /**
     * Calculate the number of packets required to teleport to a position.
     * @param position The position to teleport to.
     * @return The number of packets required.
     */
    public static Integer calculatePackets(Vec3d position) {
        if (mc.player == null) return 0;

        Double distance = mc.player.getPos().distanceTo(position);
        Integer packetsRequired = (int) Math.ceil(distance / 10.0) - 1; // 10 blocks per packet

        return packetsRequired;
    }


    /**
     * Teleport to a position.
     * @param position The position to teleport to.
     * @param setClientSided Whether to set the client-sided position.
     * @param onGround ongRound value.
     */
    public static void teleport(Vec3d position, boolean setClientSided, boolean onGround) {
        List<Vec3d> positions = new ArrayList<>();
        positions.add(position);
        teleport(positions, setClientSided, onGround);
    }


    /**
     * Teleport to multiple positions with a delay between each teleport.
     *
     * @param positions The list of positions to teleport to.
     * @param setClientSided Whether to set the client-sided position.
     * @param onGround Whether the player is on the ground.
     * @param delay The delay between each teleport in milliseconds.
     */
    public static void teleport(List<Vec3d> positions, boolean setClientSided, boolean onGround, long delay) {
        if (mc.player == null) return;

        scheduler.execute(() -> {
            for (Vec3d pos : positions) {
                teleport(pos, setClientSided, onGround);

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
    }


    /**
     * Teleport to a selected position from a list.
     * @param positions The list of positions to teleport to.
     * @param stage Step in the list of positions.
     * @param setClientSided Whether to set the client-sided position.
     * @param onGround Whether the player is on the ground.
     */
    public static void execute(List<Vec3d> positions, int stage, boolean setClientSided, boolean onGround) {
        execute(positions, stage, stage, setClientSided, onGround);
    }

    /**
     * Teleport to selected positions from a list.
     * @param positions The list of positions to teleport to.
     * @param startStage Fist step in the list of positions.
     * @param endStage Last step in the list of positions.
     * @param setClientSided Whether to set the client-sided position.
     * @param onGround Whether the player is on the ground.
     */
    public static void execute(List<Vec3d> positions, int startStage, int endStage, boolean setClientSided, boolean onGround) {
        List<Vec3d> p = new ArrayList<>();

        for (int i = startStage; i <= endStage && i < positions.size(); i++) {
            p.add(positions.get(i));
        }

        teleport(p, setClientSided, onGround);
    }


    /**
     * Spam pakets before teleporting.
     *
     * @param onGround Whether the player is on the ground.
     * @param packetsRequired The number of packets to send.
     */
    public static void sendPackets(boolean onGround, int packetsRequired) {
        if (mc.player != null) {
            if (packetsRequired >= 20) return;
            for (int i = 0; i < packetsRequired; i++) {
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY(), mc.player.getZ(), onGround));
            }
        }
    }
}

package blub.brewaddon.utils.misc;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class RenderUtils {
    public static void renderBlock(Render3DEvent event, BlockPos pos, Color linecolor, Color sideColor, ShapeMode mode) {
        double x1 = pos.getX();
        double y1 = pos.getY();
        double z1 = pos.getZ();
        double x2 = x1 + 1.0;
        double y2 = y1 + 1.0;
        double z2 = z1 + 1.0;
        event.renderer.box(x1, y1, z1, x2, y2, z2, linecolor, sideColor, mode, 0);
    }

    public static void renderPos(Render3DEvent event, Vec3d pos, Color color, double size) {
        double x1 = pos.x - size;
        double y1 = pos.y - size;
        double z1 = pos.z - size;
        double x2 = pos.x + size;
        double y2 = pos.y + size;
        double z2 = pos.z + size;
        event.renderer.box(x1, y1, z1, x2, y2, z2, color, color, ShapeMode.Both, 0);
    }
}

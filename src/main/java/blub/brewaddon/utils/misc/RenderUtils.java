package blub.brewaddon.utils.misc;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.util.math.BlockPos;

public class RenderUtils {
    public static void renderBlock(Render3DEvent event, BlockPos pos, Color linecolor, Color sideColor, ShapeMode mode) {
        double x1 = (double)pos.getX();
        double y1 = (double)pos.getY();
        double z1 = (double)pos.getZ();
        double x2 = x1 + 1.0;
        double y2 = y1 + 1.0;
        double z2 = z1 + 1.0;
        event.renderer.box(x1, y1, z1, x2, y2, z2, linecolor, sideColor, mode, 0);
    }
}

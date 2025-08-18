package blub.brewaddon.utils.misc;

import meteordevelopment.meteorclient.MeteorClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.entity.projectile.ProjectileUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class HitResults {
    static MinecraftClient mc = MinecraftClient.getInstance();

    public static HitResult getCrosshairTarget(Entity entity, double range, boolean ignoreBlocks, Predicate<Entity> filter) {
        if (entity == null || mc.world == null) return null;

        Camera camera = mc.gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();

        Vec3d direction = Vec3d.fromPolar(camera.getPitch(), camera.getYaw()).multiply(210);
        Vec3d targetPos = cameraPos.add(direction);

        EntityHitResult entityHitResult = ProjectileUtil.raycast(entity, cameraPos, targetPos, entity.getBoundingBox().stretch(direction).expand(1), filter.and(targetEntity -> !targetEntity.isSpectator()), range * range);

        if (entityHitResult != null) {
            return entityHitResult;
        }

        if (!ignoreBlocks) {
            RaycastContext context = new RaycastContext(
                cameraPos,
                targetPos,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                entity
            );

            HitResult hitResult = mc.world.raycast(context);
            if (hitResult != null && hitResult.getType() != HitResult.Type.MISS) {
                return hitResult;
            }
        }

        return null;
    }

    public static BlockPos getStaredBlock(double range) {
        Camera camera = MeteorClient.mc.gameRenderer.getCamera();
        Vec3d cameraPos = camera.getPos();
        BlockPos pos = MeteorClient.mc.world.raycast(new RaycastContext(cameraPos, cameraPos.add(Vec3d.fromPolar(camera.getPitch(), camera.getYaw()).multiply(range)), RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, MeteorClient.mc.player)).getBlockPos();
        int maxSearchHeight = 256;
        if (!MeteorClient.mc.world.isChunkLoaded(pos.getX() >> 4, pos.getZ() >> 4)) {
            return null;
        } else {
            for(int y = pos.getY(); y < pos.getY() + maxSearchHeight; ++y) {
                BlockPos temppos = new BlockPos(pos.getX(), y, pos.getZ());
                if ((double)MeteorClient.mc.player.getHeight() < 0.9) {
                    return temppos.up();
                }

                if (!MeteorClient.mc.world.getBlockState(temppos).isSolid() && !MeteorClient.mc.world.getBlockState(temppos.up()).isSolid()) {
                    return temppos;
                }
            }

            return null;
        }
    }
}

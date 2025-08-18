package blub.brewaddon.commands;

import blub.brewaddon.utils.movement.Movement;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class TpTest extends Command {

    public TpTest() {
        super("tptest", "Teleport yourself (test for simpcraft.com)");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {

        builder.then(argument("x", DoubleArgumentType.doubleArg())
            .then(argument("y", DoubleArgumentType.doubleArg())
                .then(argument("z", DoubleArgumentType.doubleArg())
                    .executes(context -> {
                        double x = DoubleArgumentType.getDouble(context, "x");
                        double y = DoubleArgumentType.getDouble(context, "y");
                        double z = DoubleArgumentType.getDouble(context, "z");

                        List<Vec3d> positions = List.of(
                            new Vec3d(mc.player.getX(), 100, mc.player.getZ()),
                            new Vec3d(x, 100, z),
                            new Vec3d(x, y, z)
                        );
                        Movement.teleport(positions, true, false);
                        return SINGLE_SUCCESS;
                    })
                )
            )
        );
    }
}

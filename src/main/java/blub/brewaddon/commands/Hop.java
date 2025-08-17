package blub.brewaddon.commands;

import blub.brewaddon.utils.movement.Movement;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.fabricmc.loader.impl.lib.sat4j.core.Vec;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.Vec3d;

public class Hop extends Command {

    public Hop() {
        super("hop", "Teleport yourself");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {

        // .hop x y z
        builder.then(argument("x", DoubleArgumentType.doubleArg())
            .then(argument("y", DoubleArgumentType.doubleArg())
                .then(argument("z", DoubleArgumentType.doubleArg())
                    .executes(context -> {
                        double x = DoubleArgumentType.getDouble(context, "x");
                        double y = DoubleArgumentType.getDouble(context, "y");
                        double z = DoubleArgumentType.getDouble(context, "z");

                        Vec3d pos = new Vec3d(x, y, z);
                        Movement.teleport(pos, false, false);
                        return SINGLE_SUCCESS;
                    })
                    .then(argument("clientSided", BoolArgumentType.bool())
                        .executes(context -> {
                            double x = DoubleArgumentType.getDouble(context, "x");
                            double y = DoubleArgumentType.getDouble(context, "y");
                            double z = DoubleArgumentType.getDouble(context, "z");
                            boolean clientSided = BoolArgumentType.getBool(context, "clientSided");

                            Vec3d pos = new Vec3d(x, y, z);
                            Movement.teleport(pos, clientSided, false);
                            return SINGLE_SUCCESS;
                        })
                        // .hop x y z clientSided onGround
                        .then(argument("onGround", BoolArgumentType.bool())
                            .executes(context -> {
                                double x = DoubleArgumentType.getDouble(context, "x");
                                double y = DoubleArgumentType.getDouble(context, "y");
                                double z = DoubleArgumentType.getDouble(context, "z");
                                boolean clientSided = BoolArgumentType.getBool(context, "clientSided");
                                boolean onGround = BoolArgumentType.getBool(context, "onGround");

                                Vec3d pos = new Vec3d(x, y, z);
                                Movement.teleport(pos, clientSided, onGround);
                                return SINGLE_SUCCESS;
                            })
                        )
                    )
                )
            )
        );
    }
}

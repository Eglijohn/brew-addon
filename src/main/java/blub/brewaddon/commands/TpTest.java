package blub.brewaddon.commands;

import blub.brewaddon.utils.movement.Movement;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import net.minecraft.command.CommandSource;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class TpTest extends Command {

    public TpTest() {
        super("tptest", "Teleport yourself (test for simpcraft.com)");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(argument("coordinates", StringArgumentType.greedyString())
            .executes(context -> {
                String coordString = StringArgumentType.getString(context, "coordinates");

                try {
                    List<Vec3d> positions = parseCoordinates(coordString);
                    Movement.teleport(positions, true, false);
                    info("Teleporting through " + positions.size() + " positions");
                } catch (IllegalArgumentException e) {
                    error(e.getMessage());
                }

                return SINGLE_SUCCESS;
            })
        );
    }

    private List<Vec3d> parseCoordinates(String input) {
        List<Vec3d> positions = new ArrayList<>();

        String cleaned = input.replaceAll("[\\[\\]]", "").trim();
        String[] parts = cleaned.split(",");

        if (parts.length % 3 != 0) {
            throw new IllegalArgumentException("Coordinates must be in multiples of three (x,y,z)");
        }

        for (int i = 0; i < parts.length; i += 3) {
            try {
                double x = Double.parseDouble(parts[i].trim());
                double y = Double.parseDouble(parts[i + 1].trim());
                double z = Double.parseDouble(parts[i + 2].trim());
                positions.add(new Vec3d(x, y, z));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format in coordinates: " + parts[i] + "," + parts[i+1] + "," + parts[i+2]);
            }
        }

        return positions;
    }
}

package blub.brewaddon.modules;

import blub.brewaddon.BrewAddon;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static blub.brewaddon.utils.misc.TextUtils.formatMinecraftString;

public class DamageLogger extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<attackMode> attackModeSetting = sgGeneral.add(new EnumSetting.Builder<attackMode>()
        .name("trigger-mode")
        .description("When to react to when attacked.")
        .defaultValue(attackMode.FriendsAndSelf)
        .build());

    private final Setting<Boolean> includeMethod = sgGeneral.add(new BoolSetting.Builder()
        .name("include-method")
        .description("Include attack method.")
        .defaultValue(true)
        .build());

    private final Setting<Boolean> includeDistance = sgGeneral.add(new BoolSetting.Builder()
        .name("include-distance")
        .description("Include attack distance.")
        .defaultValue(true)
        .build());

    private final Setting<Boolean> includeItem = sgGeneral.add(new BoolSetting.Builder()
        .name("include-item")
        .description("Include item information.")
        .defaultValue(true)
        .build());

    public DamageLogger() {
        super(BrewAddon.CATEGORY, "b-damage-logger", "Logs damage events");
    }

    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        if (mc.world == null) return;
        if (!(event.packet instanceof EntityDamageS2CPacket packet)) return;

        Entity attacker = mc.world.getEntityById(packet.sourceCauseId());
        Entity target = mc.world.getEntityById(packet.entityId());

        if (attacker == null || target == null) return;

        String attackMethod = packet.sourceType().getIdAsString();
        String formattedAttackMethod = formatMinecraftString(attackMethod);

        int attackDistance = (int) attacker.getPos().distanceTo(target.getPos());

        boolean targetIsSelf = target == mc.player;
        boolean targetIsFriend = target instanceof PlayerEntity player && Friends.get().isFriend(player);

        boolean shouldReact = switch (attackModeSetting.get()) {
            case Self -> targetIsSelf;
            case FriendsAndSelf -> targetIsSelf || targetIsFriend;
            case Friends -> targetIsFriend;
            case All -> true;
        };

        if (!shouldReact) return;

        String attackerName = attacker.getName().getString();
        String targetName = target.getName().getString();

        Text attackerText = Text.literal(attackerName).formatted(Formatting.RED);
        Text targetText;

        if (targetIsSelf) {
            targetText = Text.literal(targetName).formatted(Formatting.GOLD);
        } else if (targetIsFriend) {
            targetText = Text.literal(targetName).formatted(Formatting.GREEN);
        } else {
            targetText = Text.literal(targetName).formatted(Formatting.AQUA);
        }

        ItemStack item = attacker instanceof LivingEntity ? ((LivingEntity) attacker).getMainHandStack() : null;
        Text message = attackerText.copy().append(Text.literal(" attacked ").formatted(Formatting.GRAY)).append(targetText);

        if (includeMethod.get()) {
            message = message.copy().append(Text.literal(" via ").formatted(Formatting.GRAY))
                .append(Text.literal(formattedAttackMethod).formatted(Formatting.YELLOW));
        }

        if (includeDistance.get()) {
            message = message.copy().append(Text.literal(" from ").formatted(Formatting.GRAY))
                .append(Text.literal(String.valueOf(attackDistance)).formatted(Formatting.LIGHT_PURPLE));
        }

        if (includeItem.get() && item != null) {
            message = message.copy().append(Text.literal(" with ").formatted(Formatting.GRAY))
                .append(Text.literal("[").formatted(Formatting.GRAY))
                .append((item.getCustomName() != null ? item.getCustomName() : item.getName()).copy().styled(style -> style.withHoverEvent(new HoverEvent.ShowItem(item))))
                .append(Text.literal("]").formatted(Formatting.GRAY));
        }

        info(message);
    }

    public enum attackMode {
        Self,
        FriendsAndSelf,
        Friends,
        All
    }
}

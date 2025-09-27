package blub.brewaddon.modules;

import blub.brewaddon.BrewAddon;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardScoreUpdateS2CPacket;

import java.util.List;
import java.util.regex.Pattern;

import static blub.brewaddon.utils.misc.TextUtils.formatMinecraftString;

public class DamageLogger extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<logMode> logModeSetting = sgGeneral.add(new EnumSetting.Builder<logMode>()
        .name("log-mode")
        .description("How to log the damage events")
        .defaultValue(logMode.NamesMethodDistance)
        .build());

    private final Setting<attackMode> attackModeSetting = sgGeneral.add(new EnumSetting.Builder<attackMode>()
        .name("trigger-mode")
        .description("When to react to when attacked.")
        .defaultValue(attackMode.FriendsAndSelf)
        .build());

    public DamageLogger() {
        super(BrewAddon.CATEGORY, "b-damage-logger", "Logs damage events");
    }

    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
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
            default -> false;
        };

        if (!shouldReact) return;

        String attackerName = attacker.getName().getString();
        String targetName = target.getName().getString();

        switch (logModeSetting.get()) {
            case Names -> info(attackerName + " attacked " + targetName);
            case NamesMethod -> info(attackerName + " attacked " + targetName + " via " + formattedAttackMethod);
            case NamesDistance -> info(attackerName + " attacked " + targetName + " from " + attackDistance + " blocks away");
            case NamesMethodDistance -> info(attackerName + " attacked " + targetName + " via " + formattedAttackMethod + " from " + attackDistance + " blocks away");
        }
    }

    public enum attackMode {
        Self,
        FriendsAndSelf,
        Friends,
        All
    }

    public enum logMode {
        Names,
        NamesMethod,
        NamesMethodDistance,
        NamesDistance
    }
}

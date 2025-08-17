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

    private final Setting<Boolean> enableGlobalMode = sgGeneral.add(new BoolSetting.Builder()
        .name("enable-global-mode")
        .description("Enable global health logging (for simpcraft.com)")
        .defaultValue(false)
        .build()
    );

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

    private final Setting<Boolean> ignoreList = sgGeneral.add(new BoolSetting.Builder()
        .name("ignoreList")
        .description("Ignores List")
        .defaultValue(false)
        .visible(enableGlobalMode::get)
        .build()
    );

    private final Setting<List<String>> players = sgGeneral.add(new StringListSetting.Builder()
        .name("players")
        .description("Players where the health is logged")
        .defaultValue(List.of("Sentoljaard"))
        .visible(() -> enableGlobalMode.get() && !ignoreList.get())
        .build()
    );

    private final Setting<Boolean> useRegex = sgGeneral.add(new BoolSetting.Builder()
        .name("regex")
        .description("Pain to write")
        .defaultValue(false)
        .visible(enableGlobalMode::get)
        .build()
    );

    private final Setting<String> regex = sgGeneral.add(new StringSetting.Builder()
        .name("players")
        .description("Players where the health is logged")
        .defaultValue("")
        .visible(() -> enableGlobalMode.get() && useRegex.get())
        .build()
    );

    private final Setting<Boolean> cantReadNumbers = sgGeneral.add(new BoolSetting.Builder()
        .name("no numbers")
        .description("if you don't know that -> 5")
        .defaultValue(true)
        .visible(enableGlobalMode::get)
        .build()
    );

    public DamageLogger() {
        super(BrewAddon.CATEGORY, "damage-logger", "Logs damage events");
    }

    @EventHandler
    private void onReceivePacket(PacketEvent.Receive event) {
        if (!(event.packet instanceof EntityDamageS2CPacket packet)) {
            if (enableGlobalMode.get()) {
                Packet packet2 = event.packet;
                if (packet2 instanceof ScoreboardScoreUpdateS2CPacket) {
                    ScoreboardScoreUpdateS2CPacket scoreboardPacket = (ScoreboardScoreUpdateS2CPacket) packet2;
                    if (scoreboardPacket.objectiveName().contains("TAB-BelowName")) {
                        String name = scoreboardPacket.scoreHolderName();
                        int health = scoreboardPacket.score();
                        Pattern pattern = Pattern.compile(regex.get(), Pattern.CASE_INSENSITIVE);
                        if (players.get().contains(name) || ignoreList.get() || (useRegex.get() && pattern.matcher(name).find())) {
                            if (!cantReadNumbers.get()) {
                                info("Player " + name + " has " + health + " health");
                            } else {
                                StringBuilder hpstring = new StringBuilder();
                                int fullHearts = health / 2;  // Number of full hearts
                                boolean hasHalfHeart = health % 2 != 0; // Check if there's a half heart
                                for (int i = 0; i < fullHearts; i++) {
                                    hpstring.append("❤");
                                }
                                if (hasHalfHeart) {
                                    hpstring.append("♡"); // Half heart
                                }
                                info(name + ": " + hpstring);
                            }
                        }
                    }
                }
            }
            return;
        }

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

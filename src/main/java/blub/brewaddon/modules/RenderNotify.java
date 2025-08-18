package blub.brewaddon.modules;

import blub.brewaddon.BrewAddon;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.entity.EntityRemovedEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.text.Text;

import java.util.List;

public class RenderNotify extends Module {
    public RenderNotify() {
        super(BrewAddon.CATEGORY, "b-render-notify", "Notify when a player is rendered.");
    }

    public enum logMode {
        Off,
        Vanilla,
        Meteor
    }

    SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<String>> playersToIgnore = sgGeneral.add(
        new StringListSetting.Builder()
            .name("ignore-list")
            .description("Players with this string in their name will be ignored.")
            .build()
    );

    private final Setting<logMode> logModeSetting = sgGeneral.add(
        new EnumSetting.Builder<logMode>()
            .name("log-mode")
            .defaultValue(logMode.Vanilla)
            .description("The log mode to use.")
            .build()
    );

    private final Setting<Boolean> scawyPlayers = sgGeneral.add(
        new BoolSetting.Builder()
            .name("scawy-players")
            .description("Leave when they enter ur range.")
            .build()
    );

    private final Setting<List<String>> scawyPlayersList = sgGeneral.add(
        new StringListSetting.Builder()
            .name("scawy-players-list")
            .description("Help :(.")
            .visible(() -> scawyPlayers.get())
            .build()
    );

    @EventHandler
    private void onEntityAdded(EntityAddedEvent event) {
        if (event.entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.entity;
            if (playersToIgnore.get().stream().anyMatch(player.getName().getString()::contains)) {
                return;
            }

            if (logModeSetting.get() == logMode.Off) {
                return;
            } else if (logModeSetting.get() == logMode.Vanilla) {
                mc.inGameHud.getChatHud().addMessage(Text.literal("§7[§a+§7]§7 " + player.getName().getString() + " entered your render distance (" + Math.floor(mc.player.getPos().distanceTo(player.getPos())) + ")"));
            } else if (logModeSetting.get() == logMode.Meteor) {
                info("§7[§2+§7]§7 " + player.getName().getString() + " (" + Math.floor(mc.player.getPos().distanceTo(player.getPos())) + ")");
            }

            if (scawyPlayers.get() && scawyPlayersList.get().stream().anyMatch(player.getName().getString()::contains)) {
                mc.getNetworkHandler().sendPacket((PlayerInteractEntityC2SPacket.attack(this.mc.player, false)));
                return;
            }
        }
    }

    @EventHandler
    private void onEntityRemoved(EntityRemovedEvent event) {
        if (event.entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) event.entity;
            if (playersToIgnore.get().stream().anyMatch(player.getName().getString()::contains)) {
                return;
            }

            if (logModeSetting.get() == logMode.Off) {
                return;
            } else if (logModeSetting.get() == logMode.Vanilla) {
                mc.inGameHud.getChatHud().addMessage(Text.literal("§7[§c-§7]§7 " + player.getName().getString() + " left your render distance (" + Math.floor(mc.player.getPos().distanceTo(player.getPos())) + ")"));
            } else if (logModeSetting.get() == logMode.Meteor) {
                info("§7[§c-§7]§7 " + player.getName().getString() + " (" + Math.floor(mc.player.getPos().distanceTo(player.getPos())) + ")");
            }

            if (scawyPlayers.get() && scawyPlayersList.get().stream().anyMatch(player.getName().getString()::contains)) {
                mc.getNetworkHandler().sendPacket(PlayerInteractEntityC2SPacket.attack(this.mc.player, false));
                return;
            }
        }
    }
}

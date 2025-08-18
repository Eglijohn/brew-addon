package blub.brewaddon.modules;

import blub.brewaddon.BrewAddon;
import blub.brewaddon.utils.attack.Attack;
import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.mob.CreeperEntity;


public class AntiCreeper extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public enum KillMode {
        OnExplode("onExplode"),
        OnSpawn("onSpawn");

        private final String description;

        KillMode(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return this.description;
        }
    }

    private final Setting<KillMode> mode = sgGeneral.add(new EnumSetting.Builder<KillMode>()
        .name("kill-mode")
        .description("When to kill creepers.")
        .defaultValue(KillMode.OnExplode)
        .build()
    );

    public AntiCreeper() {
        super(BrewAddon.CATEGORY, "b-anti-creeper", "Automatically kills creepers.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mode.get() == KillMode.OnSpawn) return;

        if (mc.world == null) return;

        mc.world.getEntities().forEach(entity -> {
            if (entity instanceof CreeperEntity creeper) {
                if (creeper.getFuseSpeed() > 0) {
                    Attack.attack(creeper, true, 10, true);
                }
            }
        });
    }

    @EventHandler
    private void onEntityAdded(EntityAddedEvent event) {
        if (mode.get() == KillMode.OnExplode) return;

        if (event.entity instanceof CreeperEntity) {
            Attack.attack(event.entity, true, 10, true);
        }
    }
}

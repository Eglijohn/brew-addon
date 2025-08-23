package blub.brewaddon.mixins;

import meteordevelopment.meteorclient.systems.config.Config;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;

@Mixin(SplashTextResourceSupplier.class)
public class SplashTextMixin {
    @Unique
    private boolean override = true;
    @Unique
    private int currentIndex = 0;
    @Unique
    private final List<String> splashes = getSplashes();

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void onApply(CallbackInfoReturnable<SplashTextRenderer> cir) {
        if (Config.get() == null || !Config.get().titleScreenSplashes.get()) return;

        if (override) {
            currentIndex = new Random().nextInt(splashes.size());
            cir.setReturnValue(new SplashTextRenderer(splashes.get(currentIndex)));
        }
        override = !override;
    }

    @Unique
    private static List<String> getSplashes() {
        return List.of(
            "Fr3akOn3_ ❤️ Femboys",
            "Nerds",
            "Brew Addon on top!",
            "Yo: gurt, Gurt: sybau 😔🥀",
            "Become a fish now!",
            "Blub made product",
            "When life gives you beer, make a beercicle",
            "Capy worshippers",
            "Fermented with love and exploits",
            "Buggy? Blame on lack of beer.",
            "404: Sobriety not found"
        );
    }
}

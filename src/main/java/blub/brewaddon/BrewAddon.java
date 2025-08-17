package blub.brewaddon;

import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

import blub.brewaddon.commands.*;
import blub.brewaddon.modules.*;

public class BrewAddon extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Brew Addon");

    @Override
    public void onInitialize() {
        LOG.info("[BrewAddon] initializing...");


        // Register Modules
        Modules.get().add(new InfiniteReach());

        // Register Commandds
        Commands.add(new Hop());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "blub.brewaddon";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("Eglijohn", "brew-addon");
    }
}

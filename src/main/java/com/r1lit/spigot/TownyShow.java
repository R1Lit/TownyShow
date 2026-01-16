package com.r1lit.spigot;

import com.r1lit.spigot.command.tshow;
import com.r1lit.spigot.listener.townenter;
import com.r1lit.spigot.placeholder.PBPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class TownyShow extends JavaPlugin {
    private static TownyShow instance;

    public static TownyShow getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        getCommand("tshow").setExecutor(new tshow(this));
        Bukkit.getPluginManager().registerEvents(
                new townenter(this),
                this
        );
        instance = this;
        saveDefaultConfig();
        getLogger().info("ENABLED");
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PBPlaceholders().register();
            getLogger().info("papi зареган");
        } else {
            getLogger().warning("papi не найден,");
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

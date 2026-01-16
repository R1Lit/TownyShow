package com.r1lit.spigot.placeholder;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import com.r1lit.spigot.TownyShow;

public class PBPlaceholders extends PlaceholderExpansion {
    private String wrap(String path, String value) {
        if (value == null) {
            return "";
        }

        // враппер!!
        String wrapper = cfg(path + ".wrapper");

        if (wrapper == null || wrapper.isEmpty()) {
            return value;
        }
        if (value.trim().isEmpty()) {
            return "";
        }

        return wrapper.replace("%s", value);
    }
    private String formatPrice(double price) {
        if (price == (long) price) {
            return String.valueOf((long) price);
        }
        return String.valueOf(price);
    }


    private final TownyShow plugin = TownyShow.getInstance();
    private final TownyAPI api = TownyAPI.getInstance();

    @Override public String getIdentifier() { return "townyshow"; }
    @Override public String getAuthor() { return "r1lit"; }
    @Override public String getVersion() { return "1.0"; }
    @Override public boolean persist() { return true; }
    @Override
    public String onPlaceholderRequest(Player player, String id) {
        if (player == null) return "";
        Town town = api.getTown(player.getLocation());
        TownBlock block = api.getTownBlock(player.getLocation());
        switch (id.toLowerCase()) {

            case "town_name":
                String townName = town != null ? town.getName() : "";
                return wrap("placeholders.town.name", townName);

            case "town_nation":
                String nation = (town != null && town.hasNation())
                        ? town.getNationOrNull().getName()
                        : "";
                return wrap("placeholders.town.nation", nation);

            case "plot_name":
                String plotName = (block != null) ? block.getName() : "";
                if (plotName == null || plotName.trim().isEmpty()) {
                    plotName = cfg("placeholders.plot.name.empty");
                }
                return wrap("placeholders.plot.name", plotName);

            case "plot_owner":
                String owner = "";
                try {
                    if (block != null && block.hasResident()) {
                        owner = block.getResident().getName();
                    }
                } catch (NotRegisteredException ignored) {}

                return wrap("placeholders.plot.owner", owner);

            case "plot_price":
                if (block == null) return "";

                double price = block.getPlotPrice();
                String value;

                if (price < 0) {
                    value = cfg("placeholders.plot.price.not-for-sale");
                } else if (price == 0) {
                    value = cfg("placeholders.plot.price.free");
                } else {
                    value = cfg("placeholders.plot.price.format")
                            .replace("%price%", formatPrice(price))
                            .replace("%currency%", cfg("placeholders.defaults.currency"));
                }

                return wrap("placeholders.plot.price", value);

            case "plot_home":
                if (town == null || block == null) return "";

                try {
                    return town.getHomeBlock().equals(block)
                            ? cfg("placeholders.plot.home.value")
                            : cfg("placeholders.plot.home.empty");
                } catch (Exception e) {
                    return "";
                }
// todo
            case "pvp": {

            }
        }
        return "";
    }
    private String cfg(String path) {
        String val = plugin.getConfig().getString(path);
        return val != null ? val : "";
    }



}

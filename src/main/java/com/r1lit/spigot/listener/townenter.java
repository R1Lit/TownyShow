package com.r1lit.spigot.listener;
import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.r1lit.spigot.TownyShow;

public class townenter implements Listener {
    private final Map<UUID, TownBlock> lastBlock = new HashMap<>();
    private final TownyAPI api = TownyAPI.getInstance();
    private final TownyShow plugin;
    private final Map<UUID, String> lastChunk = new HashMap<>();

    public townenter(TownyShow plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        String chunkKey = event.getTo().getChunk().getX() + ":" + event.getTo().getChunk().getZ();
        Player player = event.getPlayer();
        TownBlock currentBlock = api.getTownBlock(event.getTo());
        TownBlock previousBlock = lastBlock.get(player.getUniqueId());

        if (currentBlock != null && currentBlock.equals(previousBlock)) {
            return;
        }

        lastBlock.put(player.getUniqueId(), currentBlock);

        if (event.getTo() == null) return;



        if (chunkKey.equals(lastChunk.get(player.getUniqueId()))) return;
        lastChunk.put(player.getUniqueId(), chunkKey);

        boolean toWild = api.isWilderness(event.getTo());
        boolean fromWild = api.isWilderness(event.getFrom());

        int fadeIn = plugin.getConfig().getInt("titles.fade-in");
        int stay = plugin.getConfig().getInt("titles.stay");
        int fadeOut = plugin.getConfig().getInt("titles.fade-out");

        // вилдернес
        if (!fromWild && toWild) {
            lastBlock.remove(player.getUniqueId());
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(""));
            sendTitle(
                    player,
                    plugin.getConfig().getString("messages.wilderness.title"),
                    plugin.getConfig().getString("messages.wilderness.subtitle"),
                    fadeIn, stay, fadeOut
            );
            return;
        }

        // город
        if (!toWild) {
            Town town = api.getTown(event.getTo());
            TownBlock block = api.getTownBlock(event.getTo());
            if (town == null || block == null) return;
            if (fromWild) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    sendTitle(
                            player,
                            plugin.getConfig().getString("messages.town.title"),
                            plugin.getConfig().getString("messages.town.subtitle"),
                            fadeIn, stay, fadeOut
                    );
                });
            }

            // actionbar тоже лучше через тик
            if (plugin.getConfig().getBoolean("messages.actionbar.enabled")) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    sendActionBar(
                            player,
                            plugin.getConfig().getString("messages.actionbar.text")
                    );
                });
            }
        }

    }

    private void sendTitle(Player player, String title, String subtitle,
                           int fadeIn, int stay, int fadeOut) {

        title = color(applyPAPI(player, title));
        subtitle = color(applyPAPI(player, subtitle));

        player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    private void sendActionBar(Player player, String text) {
        if (text == null || text.isEmpty()) return;

        text = color(applyPAPI(player, text));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
    }

    private String applyPAPI(Player player, String text) {
        if (text == null) return "";
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")
                ? PlaceholderAPI.setPlaceholders(player, text)
                : text;
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}

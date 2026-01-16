package com.r1lit.spigot.command;

import com.r1lit.spigot.TownyShow;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class tshow implements CommandExecutor {

    private final TownyShow plugin;

    public tshow(TownyShow plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("townyshow.admin")) {
            sender.sendMessage(ChatColor.RED + "no perms.");
            return true;
        }

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            plugin.reloadConfig();

            sender.sendMessage(ChatColor.GREEN + "ts reloaded.");
            return true;
        }

        sender.sendMessage(ChatColor.YELLOW + "/tshow reload");
        return true;
    }
}

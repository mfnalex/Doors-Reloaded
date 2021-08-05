package de.jeff_media.doorsreloaded.commands;

import de.jeff_media.doorsreloaded.Main;
import de.jeff_media.doorsreloaded.config.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    private final Main main;

    public ReloadCommand() {
        main = Main.getInstance();
    }

    @Override
    public boolean onCommand( CommandSender sender,  Command command,  String alias,  String[] args) {

        if (!sender.hasPermission(Permissions.RELOAD)) {
            sender.sendMessage(command.getPermissionMessage());
            return true;
        }

        main.reload();

        if (Main.getInstance().isDebug()) {
            for (String key : main.getConfig().getKeys(true)) {
                Main.getInstance().getLogger().info(key + " -> " + main.getConfig().getString(key));
            }
        }

        sender.sendMessage(ChatColor.GREEN + "DoorsReloaded configuration has been reloaded.");

        return true;
    }
}

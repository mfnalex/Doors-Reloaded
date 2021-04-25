package de.jeff_media.doubledoors.commands;

import de.jeff_media.doubledoors.Main;
import de.jeff_media.doubledoors.config.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {

    private final Main main;

    public ReloadCommand() {
        main = Main.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

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

        sender.sendMessage(ChatColor.GREEN + "DoubleDoors configuration has been reloaded.");

        return true;
    }
}

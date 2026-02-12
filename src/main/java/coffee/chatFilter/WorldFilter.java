package coffee.chatFilter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WorldFilter implements CommandExecutor {

    private final FilterManager manager;

    public WorldFilter(FilterManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("filter.admin")) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this!");
            return true;
        }

        if (args.length >= 1) {
            boolean active = args[0].equalsIgnoreCase("true");

            manager.setGlobalFilter(active);
            manager.setCommandLocked(active);
            manager.saveConfigSettings();

            String status = active ? ChatColor.RED + "ENABLED" : ChatColor.GREEN + "DISABLED";
            Bukkit.broadcastMessage(ChatColor.GOLD + "Global Filter is now " + status);
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /globalfilter <true/false>");
        }

        return true;
    }
}
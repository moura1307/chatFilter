package coffee.chatFilter;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.regex.Pattern;

public class PlayerFilter implements CommandExecutor, Listener {

    private final FilterManager manager;

    public PlayerFilter(FilterManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Locked check via manager
        if (manager.isCommandLocked() && !sender.hasPermission("filter.admin")) {
            sender.sendMessage(ChatColor.RED + "This command is currently disabled by an administrator.");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("You cannot use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("true")) {
                manager.togglePlayer(player.getUniqueId(), true);
                player.sendMessage(ChatColor.GREEN + "Filter is now on!");
            } else if (args[0].equalsIgnoreCase("false")) {
                manager.togglePlayer(player.getUniqueId(), false);
                player.sendMessage(ChatColor.RED + "Filter is now off!");
            }
        }
        return true;
    }

    @EventHandler
    public void onSendMessage(AsyncPlayerChatEvent e) {
        String originalMessage = e.getMessage();
        String censoredMessage = originalMessage;
        String lowerMsg = originalMessage.toLowerCase();
        String normalizedMsg = lowerMsg.replaceAll("[._\\-]", "");

        // We pull the badWords and whiteList from the manager now!
        for (String badWord : manager.getBadWords()) {

            // Skip if not present
            if (!lowerMsg.contains(badWord) && !normalizedMsg.contains(badWord)) continue;

            // Check Whitelist
            boolean isSafe = false;
            for (String safeWord : manager.getWhiteList()) {
                if (lowerMsg.contains(safeWord) && safeWord.contains(badWord)) {
                    isSafe = true;
                    break;
                }
            }
            if (isSafe) continue;

            // Apply Stars
            String stars = "*".repeat(badWord.length());

            // 1. Standard replacement
            censoredMessage = censoredMessage.replaceAll("(?i)" + Pattern.quote(badWord), stars);

            // 2. Bypass replacement (f.u.c.k)
            StringBuilder bypassRegex = new StringBuilder("(?i)");
            for (int i = 0; i < badWord.length(); i++) {
                bypassRegex.append(Pattern.quote(String.valueOf(badWord.charAt(i))));
                if (i < badWord.length() - 1) {
                    bypassRegex.append("[._\\-]*");
                }
            }
            censoredMessage = censoredMessage.replaceAll(bypassRegex.toString(), stars);
        }

        if (originalMessage.equals(censoredMessage)) return;

        // Custom delivery logic
        String finalCensoredMessage = censoredMessage;
        e.getRecipients().removeIf(recipient -> {
            if (manager.isGlobalFilter() || manager.getSensitivePlayers().contains(recipient.getUniqueId())) {
                String format = String.format(e.getFormat(), e.getPlayer().getDisplayName(), finalCensoredMessage);
                recipient.sendMessage(format);
                return true;
            }
            return false;
        });
    }
}
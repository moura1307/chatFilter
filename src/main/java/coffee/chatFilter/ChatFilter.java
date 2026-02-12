package coffee.chatFilter;

import org.bukkit.plugin.java.JavaPlugin;

public final class ChatFilter extends JavaPlugin {

    @Override
    public void onEnable() {
        // Create the Manager first - it handles all loading/downloading now
        FilterManager manager = new FilterManager(this);
        manager.loadAll();

        // Create one instance of your command/listener and pass it the manager
        PlayerFilter filterInstance = new PlayerFilter(manager);

        // Register Commands
        getCommand("togglefilter").setExecutor(filterInstance);
        // Pass the manager to WorldFilter so it can flip the global switches
        getCommand("globalfilter").setExecutor(new WorldFilter(manager));

        // Register Events
        getServer().getPluginManager().registerEvents(filterInstance, this);

        getLogger().info("Chat Filter Working");
    }
}
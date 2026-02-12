package coffee.chatFilter;

import org.bukkit.plugin.java.JavaPlugin;

public final class ChatFilter extends JavaPlugin {

    @Override
    public void onEnable() {
        FilterManager manager = new FilterManager(this);
        manager.loadAll();

        PlayerFilter filterInstance = new PlayerFilter(manager);

        getCommand("togglefilter").setExecutor(filterInstance);
        getCommand("globalfilter").setExecutor(new WorldFilter(manager));

        // Register Events
        getServer().getPluginManager().registerEvents(filterInstance, this);

        getLogger().info("Chat Filter Working");
    }
}
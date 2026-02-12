package coffee.chatFilter;

import org.bukkit.Bukkit;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class FilterManager {
    private final ChatFilter plugin;
    private final Set<String> badWords = new HashSet<>();
    private final Set<String> whiteList = new HashSet<>();
    private final Set<UUID> sensitivePlayers = new HashSet<>();
    private boolean globalFilter = false;
    private boolean commandLocked = false;

    public FilterManager(ChatFilter plugin) { this.plugin = plugin; }

    public void loadAll() {
        plugin.saveDefaultConfig();
        loadConfigSettings();
        loadSafeWords();
        loadSensitivePlayers();
        loadBadWordsFromWeb("https://www.cs.cmu.edu/~biglou/resources/bad-words.txt");
    }

    private void loadBadWordsFromWeb(String urlString) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            File file = new File(plugin.getDataFolder(), "wordlist.txt");
            if (!file.exists()) {
                try {
                    HttpURLConnection conn = (HttpURLConnection) new URL(urlString).openConnection();
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    try (InputStream in = conn.getInputStream(); FileOutputStream out = new FileOutputStream(file)) {
                        in.transferTo(out);
                    }
                } catch (Exception e) { plugin.getLogger().severe("Failed to download words!"); }
            }
            fillSetFromFile(file, badWords);
        });
    }

    private void loadSafeWords() {
        File file = new File(plugin.getDataFolder(), "safewords.txt");
        if (!file.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
                    out.println("hello\nglass\ngrass\nshell");
                }
            } catch (IOException e) { e.printStackTrace(); }
        }
        fillSetFromFile(file, whiteList);
    }

    private void fillSetFromFile(File file, Set<String> set) {
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = r.readLine()) != null) {
                if (!line.trim().isEmpty()) set.add(line.trim().toLowerCase());
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void togglePlayer(UUID uuid, boolean add) {
        if (add) sensitivePlayers.add(uuid); else sensitivePlayers.remove(uuid);
        saveSensitivePlayers();
    }

    private void saveSensitivePlayers() {
        File file = new File(plugin.getDataFolder(), "players.txt");
        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            for (UUID id : sensitivePlayers) out.println(id.toString());
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadSensitivePlayers() {
        File file = new File(plugin.getDataFolder(), "players.txt");
        if (!file.exists()) return;
        try (BufferedReader r = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = r.readLine()) != null) sensitivePlayers.add(UUID.fromString(line.trim()));
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void saveConfigSettings() {
        plugin.getConfig().set("global-filter-active", globalFilter);
        plugin.getConfig().set("commands-locked", commandLocked);
        plugin.saveConfig();
    }

    public void loadConfigSettings() {
        plugin.reloadConfig();
        this.globalFilter = plugin.getConfig().getBoolean("global-filter-active", false);
        this.commandLocked = plugin.getConfig().getBoolean("commands-locked", false);
    }

    // Getters and Setters
    public Set<String> getBadWords() { return badWords; }
    public Set<String> getWhiteList() { return whiteList; }
    public Set<UUID> getSensitivePlayers() { return sensitivePlayers; }
    public boolean isGlobalFilter() { return globalFilter; }
    public void setGlobalFilter(boolean active) { this.globalFilter = active; }
    public boolean isCommandLocked() { return commandLocked; }
    public void setCommandLocked(boolean locked) { this.commandLocked = locked; }
}
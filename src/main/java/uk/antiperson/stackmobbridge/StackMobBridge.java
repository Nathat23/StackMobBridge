package uk.antiperson.stackmobbridge;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import uk.antiperson.stackmob.StackMob;
import uk.antiperson.stackmobbridge.cache.StorageManager;
import uk.antiperson.stackmobbridge.config.MainConfig;

public class StackMobBridge extends JavaPlugin {

    private StackMob stackMob;
    private MainConfig config;
    private StorageManager storageManager;

    @Override
    public void onEnable() {
        stackMob = (StackMob) getServer().getPluginManager().getPlugin("StackMob");
        if (stackMob == null || !stackMob.isEnabled()) {
            getLogger().info("StackMob has not been found. Plugin will now disable!");
            getServer().getPluginManager().disablePlugin(this);
        }
        if (!stackMob.getDescription().getVersion().startsWith("5")) {
            getLogger().info("Incorrect StackMob version found. Plugin will now disable!");
            getServer().getPluginManager().disablePlugin(this);
        }
        config = new MainConfig(this);
        storageManager = new StorageManager(this);
        storageManager.onServerEnable();
        getServer().getPluginManager().registerEvents(new ChunkLoad(this), this);
    }

    @Override
    public void onDisable() {
        storageManager.onServerDisable();
    }

    public StackMob getStackMob() {
        return stackMob;
    }

    public FileConfiguration getMainConfig() {
        return config.getConfig();
    }

    public StorageManager getStorageManager() {
        return storageManager;
    }
}

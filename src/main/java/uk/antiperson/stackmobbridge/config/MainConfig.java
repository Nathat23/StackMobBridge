package uk.antiperson.stackmobbridge.config;

import org.bukkit.configuration.file.FileConfiguration;
import uk.antiperson.stackmobbridge.StackMobBridge;

import java.io.IOException;

public class MainConfig {

    private Config config;
    private Config smConfig;
    public MainConfig(StackMobBridge smb) {
        config = new Config(smb, smb.getDataFolder(),"config.yml");
        smConfig = new Config(smb, smb.getStackMob().getDataFolder(), "config.old");
    }

    public FileConfiguration getConfig() {
        if (!config.getFile().exists()){
            try {
                config.createFile();
                copyValues();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return config.getConfig();
    }

    private void copyValues() throws IOException {
        for (String key : smConfig.getConfig().getConfigurationSection("storage").getKeys(true)) {
            config.getConfig().set(key, smConfig.getConfig().get("storage." + key));
        }
        config.getConfig().save(config.getFile());
    }



}

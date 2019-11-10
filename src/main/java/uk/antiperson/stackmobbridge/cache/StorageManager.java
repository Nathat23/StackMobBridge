package uk.antiperson.stackmobbridge.cache;

import org.bukkit.entity.Entity;
import uk.antiperson.stackmobbridge.StackMobBridge;
import uk.antiperson.stackmobbridge.cache.storage.FlatFile;
import uk.antiperson.stackmobbridge.cache.storage.MySQL;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class StorageManager {

    private StackStorage stackStorage;
    private Map<UUID, Integer> amountCache = new HashMap<>();
    private StackMobBridge smb;
    public StorageManager(StackMobBridge smb){
        this.smb = smb;
    }

    public void onServerEnable(){
        StorageType cacheType = StorageType.valueOf(smb.getMainConfig().getString("type", "FLATFILE").toUpperCase());
        smb.getLogger().info("Using " + cacheType.toString() + " storage method.");
        switch (cacheType){
            case MYSQL:
                stackStorage = new MySQL(smb, this);
                break;
            case FLATFILE:
                stackStorage = new FlatFile(smb,this);
                break;
            default:
                smb.getLogger().log(Level.SEVERE, "Invalid storage type. Please check configuration.");
        }
        stackStorage.loadStorage();
    }

    public void onServerDisable(){
        stackStorage.saveStorage(amountCache);
        if(stackStorage instanceof MySQL){
            ((MySQL) stackStorage).onDisable();
        }
    }

    public Map<UUID, Integer> getAmountCache() {
        return amountCache;
    }

    public boolean isCached(Entity entity) {
        return amountCache.containsKey(entity.getUniqueId());
    }

    public int getSize(Entity entity) {
        return amountCache.get(entity.getUniqueId());
    }

    public void remove(Entity entity) {
        amountCache.remove(entity.getUniqueId());
    }
}

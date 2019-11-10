package uk.antiperson.stackmobbridge;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import uk.antiperson.stackmob.entity.StackEntity;

public class ChunkLoad implements Listener {

    private StackMobBridge smb;
    public ChunkLoad(StackMobBridge smb) {
        this.smb = smb;
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        for (Entity entity : event.getChunk().getEntities()) {
            if (!(entity instanceof Mob)) {
                continue;
            }
            if (smb.getStackMob().getEntityManager().isStackedEntity((LivingEntity) entity)) {
                continue;
            }
            if (smb.getStorageManager().isCached(entity)) {
                int stackSize = smb.getStorageManager().getSize(entity);
                StackEntity stackEntity = smb.getStackMob().getEntityManager().getStackEntity((LivingEntity) entity);
                stackEntity.setSize(stackSize);
                smb.getStorageManager().remove(entity);
            }
        }
    }
}

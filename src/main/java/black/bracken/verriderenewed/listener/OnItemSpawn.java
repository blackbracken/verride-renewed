package black.bracken.verriderenewed.listener;

import black.bracken.verriderenewed.feature.piggyback.Connector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;

public class OnItemSpawn implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void handle(ItemSpawnEvent event) {
        if (event.isCancelled()) {
            Connector.extractConnectorId(event.getEntity()).ifPresent(id -> event.setCancelled(false));
        }
    }
}

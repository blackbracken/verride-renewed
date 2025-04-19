package black.bracken.verriderenewed.listener;

import black.bracken.verriderenewed.feature.piggyback.Connector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class OnEntityPickupItem implements Listener {

    @EventHandler
    public void handle(EntityPickupItemEvent event) {
        Connector.extractConnectorId(event.getItem()).ifPresent(id -> event.setCancelled(true));
    }
}
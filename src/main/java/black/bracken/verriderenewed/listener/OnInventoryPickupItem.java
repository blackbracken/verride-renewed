package black.bracken.verriderenewed.listener;

import black.bracken.verriderenewed.feature.piggyback.Connector;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;

public class OnInventoryPickupItem implements Listener {

    @EventHandler
    public void handle(InventoryPickupItemEvent event) {
        Connector.extractConnectorId(event.getItem()).ifPresent(id -> event.setCancelled(true));
    }

}

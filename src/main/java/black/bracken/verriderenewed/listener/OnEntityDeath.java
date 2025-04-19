package black.bracken.verriderenewed.listener;

import black.bracken.verriderenewed.VerrideRenewed;
import black.bracken.verriderenewed.feature.piggyback.Connector;
import black.bracken.verriderenewed.feature.piggyback.PiggyBackFeature;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class OnEntityDeath implements Listener {

    private final PiggyBackFeature piggyBackFeature;

    public OnEntityDeath(VerrideRenewed instance) {
        this.piggyBackFeature = instance.getPiggyBackFeature();
    }

    @EventHandler
    public void handle(EntityDeathEvent event) {
        Connector.extractConnectorId(event.getEntity()).ifPresent(piggyBackFeature::disbandByConnectorId);
    }

}

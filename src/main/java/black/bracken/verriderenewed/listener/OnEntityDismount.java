package black.bracken.verriderenewed.listener;

import black.bracken.verriderenewed.VerrideRenewed;
import black.bracken.verriderenewed.entity.PlayerId;
import black.bracken.verriderenewed.feature.piggyback.Connector;
import black.bracken.verriderenewed.feature.piggyback.PiggyBackFeature;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;

public class OnEntityDismount implements Listener {

    private final PiggyBackFeature piggyBackFeature;

    public OnEntityDismount(VerrideRenewed instance) {
        this.piggyBackFeature = instance.getPiggyBackFeature();
    }

    @EventHandler
    public void handle(EntityDismountEvent event) {
        switch (event.getEntity()) {
            case Item item -> Connector.extractConnectorId(item).ifPresent(piggyBackFeature::disbandByConnectorId);
            case Player player -> piggyBackFeature.disbandByUpperId(PlayerId.of(player));
            default -> {
                // Do nothing
            }
        }
    }

}

package black.bracken.verriderenewed.listener;

import black.bracken.verriderenewed.VerrideRenewed;
import black.bracken.verriderenewed.entity.PlayerId;
import black.bracken.verriderenewed.feature.piggyback.Connector;
import black.bracken.verriderenewed.feature.piggyback.PiggyBackFeature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDismountEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PiggyBackDisbandListener implements Listener {

    private final PiggyBackFeature piggyBackFeature;

    public PiggyBackDisbandListener(VerrideRenewed instance) {
        this.piggyBackFeature = instance.getPiggyBackFeature();
    }

    @EventHandler
    public void onDismountUpperOrConnector(EntityDismountEvent event) {
        switch (event.getEntity()) {
            case Player player -> piggyBackFeature.disbandByUpperId(PlayerId.of(player));
            case Entity entity ->
                    Connector.extractConnectorId(entity).ifPresent(piggyBackFeature::disbandByConnectorId);
        }
    }

    @EventHandler
    public void onDeathPlayer(PlayerDeathEvent event) {
        final var playerId = PlayerId.of(event.getPlayer());

        piggyBackFeature.findAssociatedConnections(playerId)
                .forEach(conn -> piggyBackFeature.disbandByConnectorId(conn.connectorId()));
    }

    @EventHandler
    public void onDeathConnector(EntityDeathEvent event) {
        Connector.extractConnectorId(event.getEntity()).ifPresent(piggyBackFeature::disbandByConnectorId);
    }

}

package black.bracken.verriderenewed.listener;

import black.bracken.verriderenewed.VerrideRenewed;
import black.bracken.verriderenewed.entity.PlayerId;
import black.bracken.verriderenewed.feature.piggyback.PiggyBackFeature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;

public class OnPlayerDeath implements Listener {

    private final PiggyBackFeature piggyBackFeature;

    public OnPlayerDeath(VerrideRenewed instance) {
        this.piggyBackFeature = instance.getPiggyBackFeature();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void handle(ItemSpawnEvent event) {
        final var player = event.getEntity() instanceof Player ? (Player) event.getEntity() : null;
        if (player == null) {
            return;
        }
        final var playerId = PlayerId.of(player);

        piggyBackFeature.disbandByUpperId(playerId);
        piggyBackFeature.disbandByLowerId(playerId);
    }
}

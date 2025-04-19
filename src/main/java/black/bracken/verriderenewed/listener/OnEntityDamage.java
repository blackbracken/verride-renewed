package black.bracken.verriderenewed.listener;

import black.bracken.verriderenewed.VerrideRenewed;
import black.bracken.verriderenewed.feature.piggyback.PiggyBackFeature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class OnEntityDamage implements Listener {

    private final PiggyBackFeature piggyBackFeature;

    public OnEntityDamage(VerrideRenewed instance) {
        this.piggyBackFeature = instance.getPiggyBackFeature();
    }

    @EventHandler
    public void handle(EntityDamageEvent event) {
        final var player = event.getEntity() instanceof Player ? (Player) event.getEntity() : null;
        if (player == null) {
            return;
        }

        if (piggyBackFeature.isMounting(player)) {
            switch (event.getCause()) {
                case SUFFOCATION:
                case ENTITY_ATTACK:
                    event.setCancelled(true);
            }
        }
    }

}

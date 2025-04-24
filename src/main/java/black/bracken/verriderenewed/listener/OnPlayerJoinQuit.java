package black.bracken.verriderenewed.listener;

import black.bracken.verriderenewed.VerrideRenewed;
import black.bracken.verriderenewed.feature.geysersupport.GeyserSupportFeature;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerJoinQuit implements Listener {

    private final GeyserSupportFeature geyserSupportFeature;

    public OnPlayerJoinQuit(VerrideRenewed instance) {
        this.geyserSupportFeature = instance.getGeyserSupportFeature();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        geyserSupportFeature.registerPlayer(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        geyserSupportFeature.unregisterPlayer(event.getPlayer());
    }

}

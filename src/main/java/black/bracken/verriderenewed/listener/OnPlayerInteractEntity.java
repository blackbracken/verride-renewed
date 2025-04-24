package black.bracken.verriderenewed.listener;

import black.bracken.verriderenewed.VerrideRenewed;
import black.bracken.verriderenewed.entity.PlayerId;
import black.bracken.verriderenewed.feature.geysersupport.GeyserSupportFeature;
import black.bracken.verriderenewed.feature.piggyback.PiggyBackFeature;
import black.bracken.verriderenewed.feature.wgsupport.WgSupportFeature;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

public class OnPlayerInteractEntity implements Listener {

    private final PiggyBackFeature piggyBackFeature;
    private final WgSupportFeature wgSupportFeature;
    private final GeyserSupportFeature geyserSupportFeature;

    public OnPlayerInteractEntity(VerrideRenewed instance) {
        this.piggyBackFeature = instance.getPiggyBackFeature();
        this.wgSupportFeature = instance.getWgSupportFeature();
        this.geyserSupportFeature = instance.getGeyserSupportFeature();
    }

    @EventHandler
    public void handle(PlayerInteractEntityEvent event) {
        final var player = event.getPlayer();
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }
        if (!(event.getRightClicked() instanceof Player clickedPlayer)) {
            return;
        }
        if (!geyserSupportFeature.canUseVerRide(player) || !geyserSupportFeature.canUseVerRide(clickedPlayer)) {
            return;
        }
        if (!wgSupportFeature.canUseVerRide(player, player.getLocation())) {
            return;
        }

        final var playerId = PlayerId.of(player);
        final var connections = piggyBackFeature.findAssociatedConnections(PlayerId.of(player));

        // プレイヤーが誰かに肩車をしている場合
        final var playerAtLowerConnection = connections.stream()
                .filter(conn -> conn.lowerId().equals(playerId))
                .findFirst().orElse(null);
        if (playerAtLowerConnection != null) {
            if (player.isSneaking() && player.hasPermission(VerrideRenewed.PERM_PITCH)) {
                piggyBackFeature.pitch(player);
            } else {
                piggyBackFeature.disbandByLowerId(playerId);
            }
            return;
        }

        // プレイヤーがスニークしていない場合
        if (!player.isSneaking()) {
            if (player.hasPermission(VerrideRenewed.PERM_RIDE)) {
                piggyBackFeature.mount(player, clickedPlayer);
            }
            return;
        }

        // プレイヤーが誰にも肩車をされていない場合
        final var isPlayerNotRiding = connections.stream()
                .noneMatch(conn -> conn.upperId().equals(playerId));
        if (isPlayerNotRiding && player.hasPermission(VerrideRenewed.PERM_LIFT)) {
            piggyBackFeature.mount(clickedPlayer, player);
        }
    }

}
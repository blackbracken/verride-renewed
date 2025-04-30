package black.bracken.verriderenewed.listener;

import black.bracken.verriderenewed.VerrideRenewed;
import black.bracken.verriderenewed.entity.ConnectorId;
import black.bracken.verriderenewed.entity.PlayerId;
import black.bracken.verriderenewed.feature.piggyback.Connector;
import black.bracken.verriderenewed.feature.piggyback.PiggyBackFeature;
import black.bracken.verriderenewed.feature.wgsupport.WgSupportFeature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Optional;
import java.util.UUID;

public class OnPlayerInteractEntity implements Listener {

    private final PiggyBackFeature piggyBackFeature;
    private final WgSupportFeature wgSupportFeature;

    public OnPlayerInteractEntity(VerrideRenewed instance) {
        this.piggyBackFeature = instance.getPiggyBackFeature();
        this.wgSupportFeature = instance.getWgSupportFeature();
    }

    @EventHandler
    public void handle(PlayerInteractEntityEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) {
            return;
        }

        final var player = event.getPlayer();
        final var clickedPlayer = switch (event.getRightClicked()) {
            case Player clicked -> clicked;
            // コネクタをクリックしたとき、自身と逆位置のプレイヤーをクリックした対象として取得する
            case Entity entity -> Connector.extractConnectorId(entity)
                    .flatMap(piggyBackFeature::findAssociatedConnection)
                    .flatMap(conn -> switch (player.getUniqueId()) {
                        case UUID uuid when conn.upperId().value().equals(uuid) -> conn.lowerId().findPlayer();
                        case UUID uuid when conn.lowerId().value().equals(uuid) -> conn.upperId().findPlayer();
                        default -> Optional.empty();
                    })
                    .orElse(null);
        };
        if (clickedPlayer == null) {
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
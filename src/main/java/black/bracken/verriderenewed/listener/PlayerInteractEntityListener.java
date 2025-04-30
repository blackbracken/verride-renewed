package black.bracken.verriderenewed.listener;

import black.bracken.verriderenewed.VerrideRenewed;
import black.bracken.verriderenewed.entity.PlayerId;
import black.bracken.verriderenewed.feature.piggyback.Connector;
import black.bracken.verriderenewed.feature.piggyback.PiggyBackFeature;
import black.bracken.verriderenewed.feature.wgsupport.WgSupportFeature;
import org.bukkit.entity.Player;
import org.bukkit.entity.Turtle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Optional;

public class PlayerInteractEntityListener implements Listener {

    private final PiggyBackFeature piggyBackFeature;
    private final WgSupportFeature wgSupportFeature;

    public PlayerInteractEntityListener(VerrideRenewed instance) {
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
            case Turtle turtle -> {
                final var clickedMaybe = getOppositePiggyBackingPlayer(player, turtle);

                // コネクタをクリックしていたらイベントをキャンセル
                if (clickedMaybe.isPresent()) {
                    event.setCancelled(true);
                }

                yield clickedMaybe.orElse(null);
            }
            default -> null;
        };
        if (clickedPlayer == null) {
            return;
        }

        if (piggyBackFeature.isJustDismounted(clickedPlayer)) {
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

    private Optional<Player> getOppositePiggyBackingPlayer(Player player, Turtle turtle) {
        final var uuid = player.getUniqueId();

        return Connector.extractConnectorId(turtle)
                .flatMap(piggyBackFeature::findAssociatedConnection)
                .flatMap(conn -> {
                    if (uuid.equals(conn.upperId().value())) return conn.lowerId().findPlayer();
                    if (uuid.equals(conn.lowerId().value())) return conn.upperId().findPlayer();
                    return Optional.empty();
                });
    }

}
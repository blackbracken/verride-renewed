package black.bracken.verriderenewed.feature.piggyback;

import black.bracken.verriderenewed.VerrideRenewed;
import black.bracken.verriderenewed.entity.ConnectorId;
import black.bracken.verriderenewed.entity.PlayerId;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public final class PiggyBackFeature {

    private final ConnectionList connectionList = new ConnectionList();

    public boolean isMounting(Player player) {
        return connectionList.findConnectionByUpperId(PlayerId.of(player)).isPresent();
    }

    public void mount(Player upper, Player lower) {
        if (upper.getUniqueId().equals(lower.getUniqueId())) {
            return;
        }

        final var mountablePlayerId = connectionList.findMountablePlayerId(PlayerId.of(lower)).orElse(null);
        if (mountablePlayerId == null) {
            return;
        }
        final var mountablePlayer = mountablePlayerId.findPlayer().orElse(null);
        if (mountablePlayer == null) {
            return;
        }

        final var pair = Connector.spawnConnector(upper.getLocation());
        final var item = pair.first();
        final var connectorId = pair.second();

        mountablePlayer.addPassenger(item);
        item.addPassenger(upper);

        connectionList.connect(PlayerId.of(upper), PlayerId.of(mountablePlayer), connectorId);
    }

    public void pitch(Player lower) {
        final var upper = connectionList.findConnectionByLowerId(PlayerId.of(lower))
                .flatMap(conn -> conn.upperId().findPlayer())
                .orElse(null);
        if (upper == null) {
            return;
        }

        final var world = upper.getWorld();

        disbandByUpperId(PlayerId.of(upper));

        (new BukkitRunnable() {
            public void run() {
                upper.setVelocity(lower.getEyeLocation().getDirection().add((new Vector(0.0, 0.6, 0.0)).normalize()));
                world.spawnParticle(Particle.EXPLOSION, upper.getLocation(), 1, 0.0, 0.0, 0.0, 0.0);
                world.playSound(upper.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 2.0F, 2.0F);
            }
        }).runTaskLater(VerrideRenewed.getInstance(), 4L);

        (new BukkitRunnable() {
            double height;

            public void run() {
                if (!upper.isOnline() || upper.isDead() || upper.getLocation().getY() == this.height || upper.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) {
                    this.cancel();
                }

                this.height = upper.getLocation().getY();
                world.spawnParticle(Particle.FIREWORK, upper.getLocation(), 4, 0.4, 0.4, 0.4, 0.0);
                upper.setFallDistance(0.0F);
            }
        }).runTaskTimer(VerrideRenewed.getInstance(), 9L, 2L);
    }

    public List<Connection> findAssociatedConnections(PlayerId playerId) {
        return Stream.of(
                connectionList.findConnectionByUpperId(playerId).orElse(null),
                connectionList.findConnectionByLowerId(playerId).orElse(null)
        ).filter(Objects::nonNull).toList();
    }

    public void disbandAll() {
        connectionList.getAllConnections().forEach(this::disband);
        connectionList.disbandAll();

        // 防御的にConnectorを削除
        Bukkit.getWorlds().forEach(world -> {
            world.getEntitiesByClass(Item.class)
                    .stream()
                    .filter(item -> Connector.extractConnectorId(item).isPresent())
                    .forEach(Entity::remove);
        });
    }

    public void disbandByConnectorId(ConnectorId connectorId) {
        connectionList.findConnectionByConnectorId(connectorId).ifPresent(this::disband);
    }

    public void disbandByUpperId(PlayerId upperId) {
        connectionList.findConnectionByUpperId(upperId).ifPresent(this::disband);
    }

    public void disbandByLowerId(PlayerId lowerId) {
        connectionList.findConnectionByLowerId(lowerId).ifPresent(this::disband);
    }

    private void disband(Connection connection) {
        connectionList.disband(connection);

        // プレイヤーが存在しないとき、vehicle/passengersは返らないので双方から取得する
        final var upperVehicle = connection.upperId().findPlayer()
                .flatMap(player -> Optional.ofNullable(player.getVehicle()))
                .stream();
        final var lowerPassengers = connection.lowerId().findPlayer()
                .flatMap(player -> Optional.of(player.getPassengers()))
                .stream()
                .flatMap(List::stream);

        // Connectorを削除
        Stream.concat(upperVehicle, lowerPassengers)
                .filter(Objects::nonNull)
                .filter(e -> !e.isDead() && connection.connectorId().equals(Connector.extractConnectorId(e).orElse(null)))
                .forEach(Entity::remove);
    }
}

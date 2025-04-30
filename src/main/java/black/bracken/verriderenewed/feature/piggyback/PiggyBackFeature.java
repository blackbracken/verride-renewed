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
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.stream.Stream;

public final class PiggyBackFeature {

    private static final Long FALL_DISTANCE_REMOVE_DELAY = 11L;

    private final ConnectionList connectionList = new ConnectionList();
    private final Set<PlayerId> justDismountedPlayerIds = new HashSet<>();

    public boolean isMounting(Player player) {
        return connectionList.findConnectionByUpperId(PlayerId.of(player)).isPresent();
    }

    public boolean isJustDismounted(Player player) {
        return justDismountedPlayerIds.stream().anyMatch(id -> id.equals(PlayerId.of(player)));
    }

    public void mount(Player upper, Player lower) {
        if (upper.getUniqueId().equals(lower.getUniqueId())) {
            return;
        }

        final var candidateConnectorId = new ConnectorId(UUID.randomUUID());
        final var conn = connectionList.tryConnect(
                PlayerId.of(upper),
                PlayerId.of(lower),
                candidateConnectorId
        ).orElse(null);
        if (conn == null) {
            return;
        }

        final var mountablePlayer = conn.lowerId().findPlayer().orElse(null);
        if (mountablePlayer == null) {
            connectionList.disband(conn);
            return;
        }

        final var connectorEntity = Connector.spawnConnector(upper.getLocation().clone().add(0, 2.0, 0), candidateConnectorId);
        mountablePlayer.addPassenger(connectorEntity);
        connectorEntity.addPassenger(upper);
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
            @Override
            public void run() {
                upper.setVelocity(lower.getEyeLocation().getDirection().add((new Vector(0.0, 0.6, 0.0)).normalize()));
                world.spawnParticle(Particle.EXPLOSION, upper.getLocation(), 1, 0.0, 0.0, 0.0, 0.0);
                world.playSound(upper.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 2.0F, 2.0F);
            }
        }).runTaskLater(VerrideRenewed.getInstance(), 4L);
        scheduleRemoveFallDistanceUntilLanding(upper, true);
    }

    public List<Connection> findAssociatedConnections(PlayerId playerId) {
        return Stream.of(
                connectionList.findConnectionByUpperId(playerId).orElse(null),
                connectionList.findConnectionByLowerId(playerId).orElse(null)
        ).filter(Objects::nonNull).toList();
    }

    public Optional<Connection> findAssociatedConnection(ConnectorId connectorId) {
        return connectionList.findConnectionByConnectorId(connectorId);
    }

    public void disbandAll() {
        connectionList.disbandAll();

        Bukkit.getWorlds().forEach(world -> {
            world.getEntities()
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

        final var upperMaybe = connection.upperId().findPlayer();
        upperMaybe.ifPresent(upper -> scheduleRemoveFallDistanceUntilLanding(upper, false));

        // プレイヤーが存在しないとき、vehicle/passengersは返らないので双方から取得する
        final var upperVehicle = upperMaybe
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

    private void scheduleRemoveFallDistanceUntilLanding(Player player, boolean withParticle) {
        final var world = player.getWorld();

        justDismountedPlayerIds.add(PlayerId.of(player));
        Bukkit.getScheduler().runTaskLater(
                VerrideRenewed.getInstance(),
                () -> justDismountedPlayerIds.remove(PlayerId.of(player)),
                FALL_DISTANCE_REMOVE_DELAY
        );

        (new BukkitRunnable() {
            double height;

            @Override
            public void run() {
                if (!player.isOnline() || player.isDead() || player.getLocation().getY() == this.height || player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) {
                    this.cancel();
                }

                this.height = player.getLocation().getY();
                if (withParticle) {
                    world.spawnParticle(Particle.FIREWORK, player.getLocation(), 4, 0.4, 0.4, 0.4, 0.0);
                }
                player.setFallDistance(0.0F);
            }
        }).runTaskTimer(VerrideRenewed.getInstance(), FALL_DISTANCE_REMOVE_DELAY, 4L);
    }
}

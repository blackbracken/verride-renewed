package black.bracken.verriderenewed.feature.piggyback;

import black.bracken.verriderenewed.entity.ConnectorId;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Turtle;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record Connector(/* empty */) {

    private static final NamespacedKey KEY_CONNECTOR_ID = new NamespacedKey("verride-renewed", "connector_id");
    private static final EntityType CONNECTOR_ENTITY_TYPE = EntityType.TURTLE;

    public static Entity spawnConnector(Location at, ConnectorId candidate) {
        final var world = Objects.requireNonNull(at.getWorld());

        final var turtle = world.spawn(at, Turtle.class, t -> {
            t.setInvisible(true);
            t.setGravity(false);
            t.setAI(false);
            t.setInvulnerable(true);
            t.setBaby();
            t.setBreed(false);
            t.setAgeLock(true);
        });

        final var container = turtle.getPersistentDataContainer();
        container.set(
                KEY_CONNECTOR_ID,
                PersistentDataType.STRING,
                candidate.value().toString()
        );

        return turtle;
    }

    public static Optional<ConnectorId> extractConnectorId(Entity entity) {
        if (entity.getType() != CONNECTOR_ENTITY_TYPE) {
            return Optional.empty();
        }

        final var container = entity.getPersistentDataContainer();
        final var rawContainerId = container.get(KEY_CONNECTOR_ID, PersistentDataType.STRING);
        if (rawContainerId == null || rawContainerId.isBlank()) {
            return Optional.empty();
        }

        return Optional.of(new ConnectorId(UUID.fromString(rawContainerId)));
    }

}

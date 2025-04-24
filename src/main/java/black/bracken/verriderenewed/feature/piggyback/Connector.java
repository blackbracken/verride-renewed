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

public final class Connector {

    private static final NamespacedKey KEY_CONNECTOR_ID = new NamespacedKey("verride-renewed", "connector_id");
    private static final EntityType CONNECTOR_ENTITY_TYPE = EntityType.TURTLE;

    private Connector() {
    }

    public static Entity spawnConnector(Location at, ConnectorId candidate) {
        final var world = Objects.requireNonNull(at.getWorld());

        final var turtle = (Turtle) world.spawnEntity(at, CONNECTOR_ENTITY_TYPE);
        turtle.setInvisible(true);
        turtle.setGravity(false);
        turtle.setAI(false);
        turtle.setInvulnerable(true);
        turtle.setBaby();
        turtle.setBreed(false);
        turtle.setAgeLock(true);

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

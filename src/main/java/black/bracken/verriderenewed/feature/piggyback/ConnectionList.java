package black.bracken.verriderenewed.feature.piggyback;

import black.bracken.verriderenewed.entity.ConnectorId;
import black.bracken.verriderenewed.entity.PlayerId;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class ConnectionList {

    private final Map<ConnectorId, Connection> connectionMap = new ConcurrentHashMap<>();

    private static final int MAX_CONNECTION_SIZE = 64;

    public Optional<Connection> tryConnect(PlayerId upperId, PlayerId lowerId, ConnectorId connectorId) {
        if (!(findMountablePlayerId(lowerId).orElse(null) instanceof PlayerId topPlayerId)) {
            return Optional.empty();
        }

        if (connectionMap.values().stream().anyMatch(conn -> conn.upperId() == upperId || conn.lowerId() == topPlayerId)) {
            return Optional.empty();
        }

        final var conn = new Connection(upperId, topPlayerId, connectorId);
        connectionMap.put(connectorId, conn);
        return Optional.of(conn);
    }

    public void disband(Connection connection) {
        connectionMap.remove(connection.connectorId());
    }

    public void disbandAll() {
        connectionMap.clear();
    }

    public Optional<Connection> findConnectionByConnectorId(ConnectorId connectorId) {
        return Optional.ofNullable(connectionMap.get(connectorId));
    }

    public Optional<Connection> findConnectionByUpperId(PlayerId upperId) {
        return connectionMap.values().stream().filter(conn -> conn.upperId().equals(upperId)).findFirst();
    }

    public Optional<Connection> findConnectionByLowerId(PlayerId lowerId) {
        return connectionMap.values().stream().filter(conn -> conn.lowerId().equals(lowerId)).findFirst();
    }

    private Optional<PlayerId> findMountablePlayerId(PlayerId playerId) {
        var currentId = playerId;

        for (int i = 0; i < MAX_CONNECTION_SIZE; i++) {
            final var connection = findConnectionByLowerId(currentId);
            if (connection.isEmpty()) {
                return Optional.of(currentId);
            }
            currentId = connection.get().upperId();
        }

        return Optional.empty();
    }

}

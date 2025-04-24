package black.bracken.verriderenewed.feature.piggyback;

import black.bracken.verriderenewed.entity.ConnectorId;
import black.bracken.verriderenewed.entity.PlayerId;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ConnectionList {

    private final List<Connection> connections = new ArrayList<>();

    private static final int MAX_CONNECTION_SIZE = 64;

    public Optional<Connection> tryConnect(PlayerId upperId, PlayerId lowerId, ConnectorId connectorId) {
        if (!(findMountablePlayerId(lowerId).orElse(null) instanceof PlayerId topPlayerId)) {
            return Optional.empty();
        }

        if (connections.stream().anyMatch(conn -> conn.upperId() == upperId || conn.lowerId() == topPlayerId)) {
            return Optional.empty();
        }

        final var conn = new Connection(upperId, topPlayerId, connectorId);
        connections.add(conn);
        return Optional.of(conn);
    }

    public void disband(Connection connection) {
        connections.remove(connection);
    }

    public void disbandAll() {
        connections.clear();
    }

    public Optional<Connection> findConnectionByConnectorId(ConnectorId connectorId) {
        return connections.parallelStream().filter(conn -> conn.connectorId().equals(connectorId)).findFirst();
    }

    public Optional<Connection> findConnectionByUpperId(PlayerId upperId) {
        return connections.parallelStream().filter(conn -> conn.upperId().equals(upperId)).findFirst();
    }

    public Optional<Connection> findConnectionByLowerId(PlayerId lowerId) {
        return connections.parallelStream().filter(conn -> conn.lowerId().equals(lowerId)).findFirst();
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

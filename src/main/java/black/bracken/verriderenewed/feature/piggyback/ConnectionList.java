package black.bracken.verriderenewed.feature.piggyback;

import black.bracken.verriderenewed.entity.ConnectorId;
import black.bracken.verriderenewed.entity.PlayerId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class ConnectionList {

    private final List<Connection> connections = new ArrayList<>();

    private static final int MAX_CONNECTION_SIZE = 64;

    public void connect(PlayerId upperId, PlayerId lowerId, ConnectorId connectorId) {
        connections.add(new Connection(upperId, lowerId, connectorId));
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

    public Optional<PlayerId> findMountablePlayerId(PlayerId playerId) {
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

    public List<Connection> getAllConnections() {
        return Collections.unmodifiableList(connections);
    }

}

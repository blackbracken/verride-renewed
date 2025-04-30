package black.bracken.verriderenewed.feature.piggyback;

import black.bracken.verriderenewed.entity.ConnectorId;
import black.bracken.verriderenewed.entity.PlayerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionSetTest {

    private ConnectionSet connectionSet;

    @BeforeEach
    void setUp() {
        connectionSet = new ConnectionSet();
    }

    @Test
    void connect_shouldAddConnection_and_findByConnectorId() {
        final var upper = new PlayerId(UUID.randomUUID());
        final var lower = new PlayerId(UUID.randomUUID());
        final var connector = new ConnectorId(UUID.randomUUID());

        final var connectResult = connectionSet.tryConnect(upper, lower, connector);
        assertTrue(connectResult.isPresent());

        final var conn = connectionSet.findConnectionByConnectorId(connector).orElseThrow();
        assertEquals(upper, conn.upperId());
        assertEquals(lower, conn.lowerId());
        assertEquals(connector, conn.connectorId());
    }

    @Test
    void connect_findByUpperId_and_findByLowerId() {
        final var upper = new PlayerId(UUID.randomUUID());
        final var lower = new PlayerId(UUID.randomUUID());
        final var connector = new ConnectorId(UUID.randomUUID());

        final var connectResult = connectionSet.tryConnect(upper, lower, connector);
        assertTrue(connectResult.isPresent());

        assertEquals(lower, connectionSet.findConnectionByUpperId(upper).orElseThrow().lowerId());
        assertEquals(upper, connectionSet.findConnectionByLowerId(lower).orElseThrow().upperId());
    }

    @Test
    void disband_shouldRemoveOnlyThatConnection() {
        final var upper1 = new PlayerId(UUID.randomUUID());
        final var lower1 = new PlayerId(UUID.randomUUID());
        final var conn1 = new ConnectorId(UUID.randomUUID());

        final var conn1Result = connectionSet.tryConnect(upper1, lower1, conn1);
        assertTrue(conn1Result.isPresent());

        final var upper2 = new PlayerId(UUID.randomUUID());
        final var lower2 = new PlayerId(UUID.randomUUID());
        final var conn2 = new ConnectorId(UUID.randomUUID());

        final var conn2Result = connectionSet.tryConnect(upper2, lower2, conn2);
        assertTrue(conn2Result.isPresent());

        final var firstConn = connectionSet.findConnectionByConnectorId(conn1).orElseThrow();
        connectionSet.disband(firstConn);

        assertFalse(connectionSet.findConnectionByConnectorId(conn1).isPresent());
        assertTrue(connectionSet.findConnectionByConnectorId(conn2).isPresent());
    }

    @Test
    void findConnectionByConnectorId_whenExists_returnsConnection() {
        final var upper = new PlayerId(UUID.randomUUID());
        final var lower = new PlayerId(UUID.randomUUID());
        final var connector = new ConnectorId(UUID.randomUUID());

        final var connectResult = connectionSet.tryConnect(upper, lower, connector);
        assertTrue(connectResult.isPresent());

        final var conn = connectionSet.findConnectionByConnectorId(connector).orElseThrow();
        assertEquals(connector, conn.connectorId());
        assertEquals(upper, conn.upperId());
        assertEquals(lower, conn.lowerId());
    }

    @Test
    void findConnectionByUpperId_whenExists_returnsConnection() {
        final var upper = new PlayerId(UUID.randomUUID());
        final var lower = new PlayerId(UUID.randomUUID());
        final var connector = new ConnectorId(UUID.randomUUID());

        final var connectResult = connectionSet.tryConnect(upper, lower, connector);
        assertTrue(connectResult.isPresent());

        final var conn = connectionSet.findConnectionByUpperId(upper).orElseThrow();
        assertEquals(connector, conn.connectorId());
        assertEquals(upper, conn.upperId());
        assertEquals(lower, conn.lowerId());
    }

    @Test
    void findConnectionByLowerId_whenExists_returnsConnection() {
        final var upper = new PlayerId(UUID.randomUUID());
        final var lower = new PlayerId(UUID.randomUUID());
        final var connector = new ConnectorId(UUID.randomUUID());

        final var connectResult = connectionSet.tryConnect(upper, lower, connector);
        assertTrue(connectResult.isPresent());

        final var conn = connectionSet.findConnectionByLowerId(lower).orElseThrow();
        assertEquals(connector, conn.connectorId());
        assertEquals(upper, conn.upperId());
        assertEquals(lower, conn.lowerId());
    }

    @Test
    void findConnectionByConnectorId_noMatch_returnsEmpty() {
        final var unknown = new ConnectorId(UUID.randomUUID());
        assertTrue(connectionSet.findConnectionByConnectorId(unknown).isEmpty());
    }

    @Test
    void findConnectionByUpperId_noMatch_returnsEmpty() {
        final var unknownUpper = new PlayerId(UUID.randomUUID());
        assertTrue(connectionSet.findConnectionByUpperId(unknownUpper).isEmpty());
    }

    @Test
    void findConnectionByLowerId_noMatch_returnsEmpty() {
        final var unknownLower = new PlayerId(UUID.randomUUID());
        assertTrue(connectionSet.findConnectionByLowerId(unknownLower).isEmpty());
    }

}

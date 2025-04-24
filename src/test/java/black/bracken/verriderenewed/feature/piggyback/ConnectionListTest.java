package black.bracken.verriderenewed.feature.piggyback;

import black.bracken.verriderenewed.entity.ConnectorId;
import black.bracken.verriderenewed.entity.PlayerId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionListTest {

    private ConnectionList connectionList;

    @BeforeEach
    void setUp() {
        connectionList = new ConnectionList();
    }

    @Test
    void connect_shouldAddConnection_and_findByConnectorId() {
        final var upper = new PlayerId(UUID.randomUUID());
        final var lower = new PlayerId(UUID.randomUUID());
        final var connector = new ConnectorId(UUID.randomUUID());

        final var connectResult = connectionList.tryConnect(upper, lower, connector);
        assertTrue(connectResult.isPresent());

        final var conn = connectionList.findConnectionByConnectorId(connector).orElseThrow();
        assertEquals(upper, conn.upperId());
        assertEquals(lower, conn.lowerId());
        assertEquals(connector, conn.connectorId());
    }

    @Test
    void connect_findByUpperId_and_findByLowerId() {
        final var upper = new PlayerId(UUID.randomUUID());
        final var lower = new PlayerId(UUID.randomUUID());
        final var connector = new ConnectorId(UUID.randomUUID());

        final var connectResult = connectionList.tryConnect(upper, lower, connector);
        assertTrue(connectResult.isPresent());

        assertEquals(lower, connectionList.findConnectionByUpperId(upper).orElseThrow().lowerId());
        assertEquals(upper, connectionList.findConnectionByLowerId(lower).orElseThrow().upperId());
    }

    @Test
    void disband_shouldRemoveOnlyThatConnection() {
        final var upper1 = new PlayerId(UUID.randomUUID());
        final var lower1 = new PlayerId(UUID.randomUUID());
        final var conn1 = new ConnectorId(UUID.randomUUID());

        final var conn1Result = connectionList.tryConnect(upper1, lower1, conn1);
        assertTrue(conn1Result.isPresent());

        final var upper2 = new PlayerId(UUID.randomUUID());
        final var lower2 = new PlayerId(UUID.randomUUID());
        final var conn2 = new ConnectorId(UUID.randomUUID());

        final var conn2Result = connectionList.tryConnect(upper2, lower2, conn2);
        assertTrue(conn2Result.isPresent());

        final var firstConn = connectionList.findConnectionByConnectorId(conn1).orElseThrow();
        connectionList.disband(firstConn);

        assertFalse(connectionList.findConnectionByConnectorId(conn1).isPresent());
        assertTrue(connectionList.findConnectionByConnectorId(conn2).isPresent());
    }

    @Test
    void findConnectionByConnectorId_whenExists_returnsConnection() {
        final var upper = new PlayerId(UUID.randomUUID());
        final var lower = new PlayerId(UUID.randomUUID());
        final var connector = new ConnectorId(UUID.randomUUID());

        final var connectResult = connectionList.tryConnect(upper, lower, connector);
        assertTrue(connectResult.isPresent());

        final var conn = connectionList.findConnectionByConnectorId(connector).orElseThrow();
        assertEquals(connector, conn.connectorId());
        assertEquals(upper, conn.upperId());
        assertEquals(lower, conn.lowerId());
    }

    @Test
    void findConnectionByUpperId_whenExists_returnsConnection() {
        final var upper = new PlayerId(UUID.randomUUID());
        final var lower = new PlayerId(UUID.randomUUID());
        final var connector = new ConnectorId(UUID.randomUUID());

        final var connectResult = connectionList.tryConnect(upper, lower, connector);
        assertTrue(connectResult.isPresent());

        final var conn = connectionList.findConnectionByUpperId(upper).orElseThrow();
        assertEquals(connector, conn.connectorId());
        assertEquals(upper, conn.upperId());
        assertEquals(lower, conn.lowerId());
    }

    @Test
    void findConnectionByLowerId_whenExists_returnsConnection() {
        final var upper = new PlayerId(UUID.randomUUID());
        final var lower = new PlayerId(UUID.randomUUID());
        final var connector = new ConnectorId(UUID.randomUUID());

        final var connectResult = connectionList.tryConnect(upper, lower, connector);
        assertTrue(connectResult.isPresent());

        final var conn = connectionList.findConnectionByLowerId(lower).orElseThrow();
        assertEquals(connector, conn.connectorId());
        assertEquals(upper, conn.upperId());
        assertEquals(lower, conn.lowerId());
    }

    @Test
    void findConnectionByConnectorId_noMatch_returnsEmpty() {
        final var unknown = new ConnectorId(UUID.randomUUID());
        assertTrue(connectionList.findConnectionByConnectorId(unknown).isEmpty());
    }

    @Test
    void findConnectionByUpperId_noMatch_returnsEmpty() {
        final var unknownUpper = new PlayerId(UUID.randomUUID());
        assertTrue(connectionList.findConnectionByUpperId(unknownUpper).isEmpty());
    }

    @Test
    void findConnectionByLowerId_noMatch_returnsEmpty() {
        final var unknownLower = new PlayerId(UUID.randomUUID());
        assertTrue(connectionList.findConnectionByLowerId(unknownLower).isEmpty());
    }

}

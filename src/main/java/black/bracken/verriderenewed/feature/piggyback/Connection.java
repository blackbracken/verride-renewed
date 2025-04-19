package black.bracken.verriderenewed.feature.piggyback;

import black.bracken.verriderenewed.entity.ConnectorId;
import black.bracken.verriderenewed.entity.PlayerId;

public record Connection(
        PlayerId upperId,
        PlayerId lowerId,
        ConnectorId connectorId
) {
}

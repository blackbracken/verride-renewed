package black.bracken.verriderenewed.feature.geysersupport;

import black.bracken.verriderenewed.entity.PlayerId;
import org.bukkit.entity.Player;
import org.geysermc.api.Geyser;
import org.geysermc.api.GeyserApiBase;

import java.util.HashSet;
import java.util.Set;

public sealed interface GeyserSupportFeature {

    void registerPlayer(Player player);

    void unregisterPlayer(Player player);

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean canUseVerRide(Player player);

    static GeyserSupportFeature create() {
        try {
            return new WithGeyser(Geyser.api());
        } catch (NoClassDefFoundError e) {
            return new WithoutGeyser();
        }
    }

    final class WithGeyser implements GeyserSupportFeature {
        private final GeyserApiBase geyserApiBase;

        private final Set<PlayerId> bedrockPlayerIds = new HashSet<>();

        public WithGeyser(GeyserApiBase geyserApiBase) {
            this.geyserApiBase = geyserApiBase;
        }

        @Override
        public void registerPlayer(Player player) {
            final var playerId = PlayerId.of(player);

            if (geyserApiBase.connectionByUuid(playerId.value()) != null) {
                bedrockPlayerIds.add(playerId);
            }
        }

        @Override
        public void unregisterPlayer(Player player) {
            final var playerId = PlayerId.of(player);

            bedrockPlayerIds.remove(playerId);
        }

        @Override
        public boolean canUseVerRide(Player player) {
            return !bedrockPlayerIds.contains(PlayerId.of(player));
        }

    }

    final class WithoutGeyser implements GeyserSupportFeature {
        @Override
        public void registerPlayer(Player player) {
            // do nothing
        }

        @Override
        public void unregisterPlayer(Player player) {
            // do nothing
        }

        @Override
        public boolean canUseVerRide(Player player) {
            return true;
        }
    }

}

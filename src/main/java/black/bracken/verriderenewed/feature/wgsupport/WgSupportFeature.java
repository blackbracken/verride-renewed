package black.bracken.verriderenewed.feature.wgsupport;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public sealed interface WgSupportFeature {

    void registerFlag();

    boolean canUseVerRide(Player player, Location location);

    static WgSupportFeature create() {
        // WorldGuardが読み込めているかを判別してインスタンスを返却
        try {
            //noinspection ResultOfMethodCallIgnored
            WorldGuard.getInstance();
            return new WithWorldGuard();
        } catch (NoClassDefFoundError e) {
            return new WithoutWorldGuard();
        }
    }

    final class WithWorldGuard implements WgSupportFeature {
        private StateFlag flagAvailable = new StateFlag("verride-available", true);

        // cf. https://worldguard.enginehub.org/en/latest/developer/regions/custom-flags/
        @Override
        public void registerFlag() {
            final var registry = WorldGuard.getInstance().getFlagRegistry();
            List.of(flagAvailable).forEach(flag -> {
                try {
                    registry.register(flag);
                } catch (FlagConflictException exception) {
                    final var existing = registry.get(flag.getName());
                    if (existing instanceof StateFlag stateFlag) {
                        flagAvailable = stateFlag;
                    }
                }
            });
        }

        @Override
        public boolean canUseVerRide(Player player, Location location) {
            // cf.
            // - https://worldguard.enginehub.org/en/latest/developer/regions/spatial-queries/
            // - https://worldguard.enginehub.org/en/latest/developer/native-objects/
            final var regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
            final var localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            final var wgLocation = BukkitAdapter.adapt(location);

            return regionContainer.createQuery().testState(wgLocation, localPlayer, flagAvailable);
        }
    }

    final class WithoutWorldGuard implements WgSupportFeature {
        @Override
        public void registerFlag() {
            // do nothing
        }

        @Override
        public boolean canUseVerRide(Player player, Location location) {
            return true;
        }
    }

}

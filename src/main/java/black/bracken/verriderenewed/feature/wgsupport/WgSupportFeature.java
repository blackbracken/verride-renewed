package black.bracken.verriderenewed.feature.wgsupport;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public final class WgSupportFeature {

    // クラスロード時にWorldGuardのインスタンスを生成しないため文字列で持つ
    private static final String WG_AVAILABLE_FLAG = "verride-available";
    private static final String WG_PLUGIN_NAME = "WorldGuard";

    private final boolean isWorldGuardEnabled;

    public WgSupportFeature() {
        final var worldGuardPluginInstance = Bukkit.getServer().getPluginManager().getPlugin(WG_PLUGIN_NAME);

        this.isWorldGuardEnabled = worldGuardPluginInstance != null;
    }

    public boolean canUseVerRide(Player player, Location location) {
        if (!isWorldGuardEnabled) {
            return true;
        }

        // cf.
        // - https://worldguard.enginehub.org/en/latest/developer/regions/spatial-queries/
        // - https://worldguard.enginehub.org/en/latest/developer/native-objects/
        final var regionContainer = WorldGuard.getInstance().getPlatform().getRegionContainer();
        final var localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        final var wgLocation = BukkitAdapter.adapt(location);
        final var wgAvailableFlag = new StateFlag(WG_AVAILABLE_FLAG, true);

        return regionContainer.createQuery().testState(wgLocation, localPlayer, wgAvailableFlag);
    }

}

package black.bracken.verriderenewed;

import black.bracken.verriderenewed.feature.geysersupport.GeyserSupportFeature;
import black.bracken.verriderenewed.feature.piggyback.PiggyBackFeature;
import black.bracken.verriderenewed.feature.wgsupport.WgSupportFeature;
import black.bracken.verriderenewed.listener.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class VerrideRenewed extends JavaPlugin {

    // permissions
    public static final String PERM_RIDE = "verride.ride";
    public static final String PERM_LIFT = "verride.lift";
    public static final String PERM_PITCH = "verride.pitch";

    private static VerrideRenewed instance;

    private PiggyBackFeature piggyBackFeature;
    private WgSupportFeature wgSupportFeature;
    private GeyserSupportFeature geyserSupportFeature;

    public static VerrideRenewed getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        wgSupportFeature = WgSupportFeature.create();
        wgSupportFeature.registerFlag();
    }

    @Override
    public void onEnable() {
        instance = this;

        piggyBackFeature = new PiggyBackFeature();
        geyserSupportFeature = GeyserSupportFeature.create();

        registerListeners();
    }

    @Override
    public void onDisable() {
        instance = null;

        this.getServer().getScheduler().cancelTasks(this);
        piggyBackFeature.disbandAll();
    }

    public PiggyBackFeature getPiggyBackFeature() {
        return piggyBackFeature;
    }

    public WgSupportFeature getWgSupportFeature() {
        return wgSupportFeature;
    }

    public GeyserSupportFeature getGeyserSupportFeature() {
        return geyserSupportFeature;
    }

    private void registerListeners() {
        final var pm = Bukkit.getPluginManager();
        pm.registerEvents(new OnEntityDamage(this), this);
        pm.registerEvents(new OnEntityDeath(this), this);
        pm.registerEvents(new OnEntityDismount(this), this);
        pm.registerEvents(new OnEntityPickupItem(), this);
        pm.registerEvents(new OnInventoryPickupItem(), this);
        pm.registerEvents(new OnItemSpawn(), this);
        pm.registerEvents(new OnPlayerDeath(this), this);
        pm.registerEvents(new OnPlayerInteractEntity(this), this);
        pm.registerEvents(new OnPlayerJoinQuit(this), this);
    }

}

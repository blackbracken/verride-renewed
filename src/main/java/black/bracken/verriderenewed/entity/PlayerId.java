package black.bracken.verriderenewed.entity;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;

public record PlayerId(UUID value) {

    public static PlayerId of(Player player) {
        return new PlayerId(player.getUniqueId());
    }

    public Optional<Player> findPlayer() {
        return Optional.ofNullable(Bukkit.getPlayer(value));
    }

}

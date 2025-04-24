package black.bracken.verriderenewed.feature.piggyback;

import black.bracken.verriderenewed.entity.ConnectorId;
import black.bracken.verriderenewed.util.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class Connector {

    private static final String CONNECTOR_DISPLAY_NAME = "#VERRIDE_RENEWED_CONNECTOR#";

    private Connector() {
    }

    public static Item spawnConnector(Location at, ConnectorId candidate) {
        final var world = Objects.requireNonNull(at.getWorld());
        final var itemStack = createConnectorItemStack(candidate.value());

        final var item = world.dropItem(at, itemStack);
        item.setPickupDelay(Integer.MAX_VALUE);
        item.setTicksLived(Integer.MAX_VALUE);

        return item;
    }

    public static Optional<ConnectorId> extractConnectorId(Entity entity) {
        if (!(entity instanceof Item item)) {
            return Optional.empty();
        }

        final var itemStack = item.getItemStack();
        if (itemStack.getType() != Material.TORCH) {
            return Optional.empty();
        }

        final var meta = itemStack.getItemMeta();
        if (meta == null) {
            return Optional.empty();
        }

        final var hasMatchedName = switch (meta.displayName()) {
            case TextComponent text -> CONNECTOR_DISPLAY_NAME.equals(text.content());
            case null, default -> false;
        };
        if (!hasMatchedName) {
            return Optional.empty();
        }

        final var rawConnectorId = Optional.ofNullable(meta.lore())
                // loreの1行目を取得
                .filter(components -> components.size() == 1)
                .map(List::getFirst)
                // 文字列に変換
                .filter(component -> component instanceof TextComponent)
                .map(component -> (TextComponent) component)
                .map(TextComponent::content)
                // UUIDに変換
                .map(text -> {
                    try {
                        return UUID.fromString(text);
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                });

        return rawConnectorId.map(ConnectorId::new);
    }

    private static ItemStack createConnectorItemStack(UUID uuid) {
        final var itemStack = ItemStack.of(Material.TORCH);

        final var meta = itemStack.getItemMeta();
        meta.displayName(Component.text(CONNECTOR_DISPLAY_NAME));
        meta.lore(List.of(Component.text(uuid.toString())));

        itemStack.setItemMeta(meta);
        return itemStack;
    }

}

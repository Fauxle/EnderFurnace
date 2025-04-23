package com.github.fauxle.ef.repositories;

import com.github.fauxle.ef.EnderFurnacePlugin;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import lombok.*;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@RequiredArgsConstructor
public class FurnaceRepository {

    private final World world;
    private final EnderFurnacePlugin plugin;

    public EnderFurnace createNew() {
        int x = 0;
        int y = -50;
        int z = 0;
        Block block = world.getBlockAt(x, y, z);
        while (block.getType() == Material.FURNACE) {
            // TODO Need to use the z axis in this
            y++;
            if (y >= world.getMaxHeight()) {
                y = 0;
                x++;
            }
        }
        block.getChunk().setForceLoaded(true);
        EnderFurnace enderFurnace = new EnderFurnace();
        enderFurnace.setFurnaceBlock(block);
        enderFurnace.setCreated(Instant.now());
        return enderFurnace;
    }

    public void save(EnderFurnace enderFurnace) {
        if (enderFurnace.furnaceBlock.getType() != Material.FURNACE) {
            enderFurnace.furnaceBlock.setType(Material.FURNACE, false);
        }
        PersistentDataContainer container =
                ((Furnace) enderFurnace.furnaceBlock.getState()).getPersistentDataContainer();
        container.set(
                new NamespacedKey(plugin, "created"),
                PersistentDataType.LONG,
                enderFurnace.getCreated().toEpochMilli());
        container.set(
                new NamespacedKey(plugin, "ownerUniqueId"),
                PersistentDataType.STRING,
                Objects.toString(enderFurnace.getOwnerUniqueId(), ""));
    }

    @Data
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EnderFurnace {
        private Block furnaceBlock;
        private UUID ownerUniqueId;
        private Set<UUID> canAccessUniqueIds;
        private Instant created;
    }
}

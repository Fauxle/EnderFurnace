package com.github.fauxle.ef.orm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Furnace;

public class FurnaceRepository {

    private final World world;
    private final List<EnderFurnace> enderFurnaces;

    public FurnaceRepository(World furnaceWorld) throws IOException {
        this.world = furnaceWorld;
        this.enderFurnaces = new ArrayList<>();
        Supplier<Chunk> chunkSupplier = newChunkSupplier();
        int furnacesLoaded;
        do {
            furnacesLoaded = 0;
            Chunk chunk = chunkSupplier.get();
            for (BlockState blockState : chunk.getTileEntities()) {
                if (blockState instanceof Furnace furnace) {
                    furnacesLoaded++;
                    EnderFurnace enderFurnace = new EnderFurnace(furnace);
                    enderFurnace.load();
                    enderFurnaces.add(enderFurnace);
                }
            }
            if (furnacesLoaded > 0) {
                chunk.setForceLoaded(true);
            }
        } while (furnacesLoaded > 0);
    }

    public List<EnderFurnace> findAllAccessibleBy(UUID uniqueId) {
        return enderFurnaces.stream()
                .filter(
                        ef ->
                                Objects.equals(ef.getOwnerUniqueId(), uniqueId)
                                        || ef.getCanAccessUniqueIds().contains(uniqueId))
                .collect(Collectors.toList());
    }

    public void delete(EnderFurnace enderFurnace) {
        enderFurnaces.remove(enderFurnace);
        enderFurnace.getFurnace().getBlock().setType(Material.AIR, false);
    }

    public EnderFurnace createNewFurnace(Material furnaceType) {
        Supplier<Block> blockSupplier = newFurnaceBlockSupplier();
        Block b;
        do {
            b = blockSupplier.get();
        } while (!b.isEmpty());
        b.setType(furnaceType, false);
        EnderFurnace ef = new EnderFurnace((Furnace) b.getState());
        enderFurnaces.add(ef);
        return ef;
    }

    private Supplier<Block> newFurnaceBlockSupplier() {
        return new Supplier<>() {
            private final Supplier<Chunk> chunkSupplier = newChunkSupplier();
            private Supplier<Block> blockSupplier =
                    newBlockWithinChunkSupplier(chunkSupplier.get());

            @Override
            public Block get() {
                Block b = blockSupplier.get();
                if (b == null) {
                    blockSupplier = newBlockWithinChunkSupplier(chunkSupplier.get());
                    return get();
                }
                return b;
            }
        };
    }

    private Supplier<Chunk> newChunkSupplier() {
        return new Supplier<>() {
            int x = 0;

            @Override
            public Chunk get() {
                return world.getChunkAt(x++, 0);
            }
        };
    }

    private Supplier<Block> newBlockWithinChunkSupplier(Chunk chunk) {
        return new Supplier<>() {
            int x = 0; // 0 - 15 limit
            int y = 0;
            int z = 0; // 0 - 15 limit

            @Override
            public Block get() {
                Block b = chunk.getBlock(x, y, z);
                y++;
                if (y >= chunk.getWorld().getMaxHeight()) {
                    y = 0;
                    x++;
                    if (x > 15) {
                        x = 0;
                        z++;
                        if (z > 15) {
                            // no more room in the chunk
                            return null;
                        }
                    }
                }
                return b;
            }
        };
    }
}

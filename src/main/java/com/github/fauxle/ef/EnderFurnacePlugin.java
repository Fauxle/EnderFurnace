package com.github.fauxle.ef;

import java.io.IOException;
import java.util.Objects;
import java.util.Random;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

public class EnderFurnacePlugin extends JavaPlugin {

    private World enderFurnaceWorld;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        WorldCreator enderFurnaceWorldCreator =
                new WorldCreator(
                                Objects.requireNonNull(
                                        getConfig()
                                                .getString(
                                                        "furnace-world-name",
                                                        "ender_furnace_plugin")))
                        .environment(World.Environment.NORMAL)
                        .type(WorldType.FLAT)
                        .generateStructures(false)
                        .generator(
                                new ChunkGenerator() {
                                    @Override
                                    public ChunkData generateChunkData(
                                            World world,
                                            Random random,
                                            int x,
                                            int z,
                                            BiomeGrid biome) {
                                        return createChunkData(world);
                                    }
                                });

        enderFurnaceWorld = enderFurnaceWorldCreator.createWorld();
        if (enderFurnaceWorld == null) {
            getLogger().severe("Error: Ender Furnace world not found");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // Set difficulty to peaceful for ensuring mobs are not spawning in the world
        enderFurnaceWorld.setDifficulty(Difficulty.PEACEFUL);

        // Do not keep chunks we do not need loaded
        enderFurnaceWorld.setKeepSpawnInMemory(false);

        // Register player world blocker
        getServer()
                .getPluginManager()
                .registerEvents(new WorldBlockListener(enderFurnaceWorld), this);

        try {
            FurnaceRepository furnaceRepository = new FurnaceRepository(enderFurnaceWorld);
            Objects.requireNonNull(
                            getCommand("enderfurnace"), "Command not registered in plugin.yml")
                    .setExecutor(new EnderFurnaceCommand(this, furnaceRepository));
        } catch (IOException e) {
            getLogger().severe("Error: Failed to load data from ender furnace world");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (enderFurnaceWorld != null) {
            if (!getServer().unloadWorld(enderFurnaceWorld, true)) {
                getLogger().warning("Failed to unload the ender furnace world");
            }
            enderFurnaceWorld = null;
        }
    }
}

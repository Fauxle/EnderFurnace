package com.github.fauxle.ef;

import com.github.fauxle.ef.commands.EnderFurnaceCommand;
import org.bukkit.Difficulty;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.Random;

public class EnderFurnacePlugin extends JavaPlugin {

    private World enderFurnaceWorld;

    @Override
    public void onEnable(){
        Objects.requireNonNull(
                getCommand("enderfurnace"),
                "enderfurnace command is not registered in plugin.yml"
        ).setExecutor(new EnderFurnaceCommand(this));

        WorldCreator enderFurnaceWorldCreator = new WorldCreator("ender_furnace_plugin")
                .environment(World.Environment.NORMAL)
                .type(WorldType.FLAT)
                .generateStructures(false)
                .generator(new ChunkGenerator() {
                    @Override
                    public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
                        return createChunkData(world);
                    }
                });

        enderFurnaceWorld = enderFurnaceWorldCreator.createWorld();
        if(enderFurnaceWorld == null){
            getLogger().severe("Error: Ender Furnace world not found");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // Set difficulty to peaceful for ensuring mobs are not spawning in the world
        enderFurnaceWorld.setDifficulty(Difficulty.PEACEFUL);

        // Method Used: The setForceLoaded(true) method on a Chunk tells
        // the server that this chunk should remain loaded even if no players are nearby.
    }

    @Override
    public void onDisable(){
        if(enderFurnaceWorld != null) {
            getServer().unloadWorld(enderFurnaceWorld, true);
            enderFurnaceWorld = null;
        }
    }

    public World getEnderFurnaceWorld() {
        return enderFurnaceWorld;
    }
}

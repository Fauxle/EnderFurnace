package com.github.fauxle.ef;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;

@RequiredArgsConstructor
public class EnderFurnaceCommand implements CommandExecutor {

    private final EnderFurnacePlugin plugin;
    private final FurnaceRepository repository;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        if (!(sender instanceof HumanEntity p)) {
            sender.sendMessage(ChatColor.DARK_RED + "Error: Only players can use this command.");
            return true;
        }
        EnderFurnace enderFurnace;
        List<EnderFurnace> furnaces = repository.findAllAccessibleBy(p.getUniqueId());
        if (furnaces.isEmpty()) {
            enderFurnace = repository.createNewFurnace(Material.FURNACE);
            enderFurnace.setOwnerUniqueId(p.getUniqueId());
            try {
                enderFurnace.save();
                p.sendMessage(ChatColor.DARK_GREEN + "New ender furnace created!");
            } catch (IOException e) {
                plugin.getLogger()
                        .log(Level.SEVERE, "Failed to save owner to new ender furnace", e);
                p.sendMessage(ChatColor.DARK_RED + "Error: Failed to create new ender furnace");
                return true;
            }
        } else {
            enderFurnace = furnaces.getFirst();
        }
        p.openInventory(enderFurnace.getFurnace().getInventory());
        return true;
    }
}

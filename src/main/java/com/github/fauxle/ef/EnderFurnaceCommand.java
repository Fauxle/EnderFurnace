package com.github.fauxle.ef;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.github.fauxle.ef.orm.EnderFurnace;
import com.github.fauxle.ef.orm.FurnaceRepository;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("enderfurnace|ef")
public class EnderFurnaceCommand extends BaseCommand {

    @Dependency private EnderFurnacePlugin plugin;

    @Dependency private FurnaceRepository repository;

    @Default
    @Description("Opens your ender furnaces")
    @CommandPermission("enderfurnace.command")
    public void onDefault(Player p) {
        List<EnderFurnace> furnaces = repository.findAllAccessibleBy(p.getUniqueId());
        if (furnaces.isEmpty()) {
            p.sendMessage(
                    ChatColor.DARK_RED + "Error: You do not have access to any ender furnaces");
            return;
        }
        p.sendMessage("opening furnace...");
        p.openInventory(furnaces.getFirst().getFurnace().getInventory());
    }

    @Subcommand("new")
    @Description("Creates a new ender furnace")
    @CommandPermission("enderfurnace.command.new")
    public void onNewFurnace(Player p) {
        EnderFurnace enderFurnace = repository.createNewFurnace(Material.FURNACE);
        enderFurnace.setOwnerUniqueId(p.getUniqueId());
        try {
            enderFurnace.save();
            p.sendMessage(ChatColor.DARK_GREEN + "New ender furnace created!");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save owner to new ender furnace", e);
            p.sendMessage(ChatColor.DARK_RED + "Error: Failed to create new ender furnace");
        }
    }

    @Subcommand("delete")
    @Description("Deletes an ender furnace")
    @CommandPermission("enderfurnace.command.delete")
    public void onGoodbye(CommandSender sender) {
        sender.sendMessage("Delete needs implementation");
    }
}

package com.github.fauxle.ef.commands;

import com.github.fauxle.ef.EnderFurnacePlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class EnderFurnaceCommand implements CommandExecutor {

  private final EnderFurnacePlugin plugin;

  public EnderFurnaceCommand(EnderFurnacePlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof HumanEntity p)) {
      sender.sendMessage(
          ChatColor.RED + "Error: Only human entities (players) can execute this command");
      return true;
    }
    Inventory inventory = plugin.getServer().createInventory(null, InventoryType.CHEST);
    p.openInventory(inventory);
    return true;
  }
}

package com.github.fauxle.ef.commands;

import com.github.fauxle.ef.EnderFurnacePlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class EnderFurnaceCommand implements CommandExecutor {

    private final EnderFurnacePlugin plugin;

    public EnderFurnaceCommand(EnderFurnacePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        return false;
    }

}

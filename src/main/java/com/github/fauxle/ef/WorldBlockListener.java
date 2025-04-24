package com.github.fauxle.ef;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

@RequiredArgsConstructor
public class WorldBlockListener implements Listener {

    private final World world;

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onChangeWorld(PlayerChangedWorldEvent event) {
        if (Objects.equals(event.getPlayer().getWorld(), world)
                && runPermissionsCheck(event.getPlayer())) {
            event.getPlayer().teleport(event.getFrom().getSpawnLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Objects.equals(event.getPlayer().getWorld(), world)
                && runPermissionsCheck(event.getPlayer())) {
            event.getPlayer().teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (Objects.equals(event.getPlayer().getWorld(), world)
                && runPermissionsCheck(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (Objects.equals(event.getPlayer().getWorld(), world)
                && runPermissionsCheck(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    private boolean runPermissionsCheck(Player p) {
        if (!p.hasPermission("enderfurnace.world.join")) {
            p.sendMessage(
                    ChatColor.DARK_RED + "Error: You do not have permission to enter this world.");
            return true;
        } else {
            p.sendMessage(
                    ChatColor.YELLOW
                            + ""
                            + ChatColor.BOLD
                            + "WARNING: You joined the ender furnace world");
            p.sendMessage(
                    ChatColor.YELLOW
                            + ""
                            + ChatColor.BOLD
                            + "WARNING: This world is not meant for players");
            p.sendMessage(
                    ChatColor.YELLOW
                            + ""
                            + ChatColor.BOLD
                            + "WARNING: Touching anything could cause plugin issues");
            p.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "WARNING: YOU HAVE BEEN WARNED");
            return false;
        }
    }
}

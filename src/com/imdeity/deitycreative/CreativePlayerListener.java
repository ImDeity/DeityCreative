package com.imdeity.deitycreative;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

import com.imdeity.deityapi.Deity;
import com.imdeity.deityapi.exception.InventoryAlreadySavedException;
import com.imdeity.deityapi.exception.NoInventorySavedException;

public class CreativePlayerListener implements Listener {
	
	public final DeityCreative plugin;

	public CreativePlayerListener(DeityCreative instance) {
		plugin = instance;
	}

	// TADA
	@EventHandler(priority = EventPriority.HIGH)
	public void playerChangedWorld(PlayerChangedWorldEvent event) {

		if (event.getPlayer() == null) {
			return;
		}

		Player player = event.getPlayer();

		if (player.getWorld().getName().equalsIgnoreCase("creative_world")) {
			
			try {
				Deity.player.serializedPlayer.savePlayerInventory(player
						.getName(), Deity.player.serializedPlayer
						.getPlayerInventory(player), "Creative");
				Deity.chat
						.sendPlayerMessage(player,
								"&cYour inventory has been saved for when you go back to the main world");
			} catch (InventoryAlreadySavedException e) {
				plugin.out("WARNING: Inventory already saved for " + player.getName());
				Deity.chat
				.sendPlayerMessage(player,
						"&cUnable to save your inventory - Inventory already saved previously");
				// e.printStackTrace();
			}
			Deity.player.clearAllInventory(player);
		}
		if (event.getFrom().getName().equalsIgnoreCase("creative_world")) {
			Deity.player.clearAllInventory(player);
			try {
				Deity.player.serializedPlayer.setPlayerInventory(player,
						Deity.player.serializedPlayer
								.loadPlayerInventory(player));
				Deity.chat.sendPlayerMessage(player,
						"&cHere is your saved inventory!");
			} catch (NoInventorySavedException e) {
				plugin.out("WARNING: No saved inventory found for " + player.getName());
				Deity.chat
				.sendPlayerMessage(player,
						"&cUnable to restore inventory - No saved inventory found");
				// e.printStackTrace();
			}
		}
	}

}

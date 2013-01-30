package com.imdeity.deitycreative.cmds.creative;

import java.io.IOException;
import java.sql.SQLDataException;

import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.api.DeityCommandReceiver;
import com.imdeity.deityapi.records.DatabaseResults;
import com.imdeity.deitycreative.DeityCreative;
import com.imdeity.deitycreative.Plot;

public class CreativeUnclaimCommand extends DeityCommandReceiver {

	@Override
	public boolean onConsoleRunCommand(String[] args) {
		return false;
	}

	@Override
	public boolean onPlayerRunCommand(Player player, String[] args) {
		String sql = "SELECT `id` FROM " + DeityCreative.database.tableName("deity_creative_", "plots") + " WHERE playername = ?";
		DatabaseResults query = DeityCreative.database.readEnhanced(sql, player.getName());
		if (query != null && query.hasRows()) {
			try {
				int id = query.getInteger(0, "id");
				Plot plot = new Plot(id);
				try {
					plot.removePlayer(player.getName());
				} catch (IOException e) {
					e.printStackTrace();
				}
				plot.setClaimed(false);
				plot.save();
				plot.resetLand();
				DeityAPI.getAPI().getSecAPI().setGreetingFlag("creative_" + id, player, "Available Plot [" + id + "]");
				DeityCreative.plugin.chat.sendPlayerMessage(player, "&ePlot unclaimed!");
				return true;
			} catch (SQLDataException e) {
				e.printStackTrace();
			}
		} else {
			DeityCreative.plugin.chat.sendPlayerMessage(player, "&eYou don't own a plot. Type &3/creative claim &eto claim a plot!");
			return true;
		}
		return false;
	}

}

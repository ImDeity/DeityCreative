package com.imdeity.deitycreative.cmds.creative;

import java.io.IOException;
import java.sql.SQLDataException;

import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.api.DeityCommandReceiver;
import com.imdeity.deityapi.records.DatabaseResults;
import com.imdeity.deitycreative.DeityCreative;
import com.imdeity.deitycreative.Plot;

public class CreativeForceUnclaimCommand extends DeityCommandReceiver {

	public CreativeForceUnclaimCommand() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onConsoleRunCommand(String[] args) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onPlayerRunCommand(Player player, String[] args) {
		//!Deity.perm.has(player, "deity.creative.forceunclaim") permission added to registerCommand() call instead
		if (!DeityAPI.getAPI().getDeityPermAPI().isLeastSubAdmin(player)) {
			DeityCreative.plugin.chat.sendPlayerMessage(player, "&cAdmin permissions required");
			return false;
		}
		
		if (args.length < 1) {
			DeityCreative.plugin.chat.sendPlayerMessage(player, "&cPlot ID required");
			return true;
		}
		
		String plotId = args[0];
        String sql = "SELECT `id`, `playername` FROM " + DeityCreative.database.tableName("deity_creative_", "plots") 	+ " WHERE id = ?";
		DatabaseResults query = DeityCreative.database.readEnhanced(sql, plotId);
		if (query != null && query.hasRows()) {
			try {
				int id = query.getInteger(0, "id");
				String plot_owner = query.getString(0, "playername");
				Plot plot = new Plot(id);
				try {
					plot.removePlayer(plot_owner);
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
			DeityCreative.plugin.chat.sendPlayerMessage(player, "&eInvalid plot ID?");
		}
		return false;
	}

}

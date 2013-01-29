package com.imdeity.deitycreative.cmds.creative;

import java.io.IOException;
import java.sql.SQLDataException;

import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.api.DeityCommandReceiver;
import com.imdeity.deityapi.records.DatabaseResults;
import com.imdeity.deitycreative.CreativeRank;
import com.imdeity.deitycreative.DeityCreative;
import com.imdeity.deitycreative.Plot;

public class CreativeClaimCommand extends DeityCommandReceiver {

	@Override
	public boolean onConsoleRunCommand(String[] arg0) {
		return false;
	}

	@Override
	public boolean onPlayerRunCommand(Player player, String[] args) {
//		String sql = "SELECT `id` FROM " + DeityCreative.database.tableName("deity_creative_", "plots") + " WHERE playername = ?";
//		DatabaseResults query = DeityCreative.database.readEnhanced(sql, player.getName());
//
//		if (query != null && query.hasRows()) {
//			DeityCreative.plugin.chat.sendPlayerMessage(player, "&cSorry you already have a plot. use /creative home to go there");
//			return true;
		
//		}
		
		if(!DeityCreative.database.canClaim(player.getName())){
			DeityCreative.plugin.chat.sendPlayerMessage(player, "&cSorry you cannot claim another plot yet. ");
		}

//		String sql = "SELECT `id` FROM " + DeityCreative.database.tableName("deity_creative_", "plots")
//				+ " WHERE is_claimed = 0 AND playername = '' AND id > 2000 ORDER BY `id` ASC LIMIT 1;";
//		DatabaseResults query2 = DeityCreative.database.readEnhanced(sql);
		CreativeRank rank = DeityCreative.database.getRankOfPlayer(player.getName());
		String sql = "SELECT `id` FROM " + DeityCreative.database.plots + " WHERE plot_size='" + rank.getPlotSize() +
				"is_claimed=0 AND playername='' AND id > 2000 ORDER BY `id` ASC LIMIT 1";
		DatabaseResults query2 = DeityCreative.database.readEnhanced(sql);
		if (query2 != null && query2.hasRows()) {
			try {
				int id = query2.getInteger(0, "id");
				if (id > 2000) {
					Plot plot = new Plot(id);
					plot.addPlayer(player.getName());
					plot.setClaimed(true);
					plot.save();
					DeityAPI.getAPI().getSecAPI().setGreetingFlag("creative_" + id, player, "Owner: " + player.getName() + " [" + id + "]");
					new CreativeHomeCommand().onPlayerRunCommand(player, args);
				} else {
					DeityCreative.plugin.chat.sendPlayerMessage(player, "Sorry there are currently no plots available to claim. Please try again later");
				}
				return true;
			} catch (SQLDataException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			DeityCreative.plugin.chat.sendPlayerMessage(player, "Sorry there are currently no plots available to claim. Please try again later");
		}
		return true;
	}

}

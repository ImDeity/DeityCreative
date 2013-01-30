package com.imdeity.deitycreative.cmds.creative;

import java.io.IOException;

import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.api.DeityCommandReceiver;
import com.imdeity.deitycreative.DeityCreative;
import com.imdeity.deitycreative.Plot;

public class CreativeUnclaimCommand extends DeityCommandReceiver {

	@Override
	public boolean onConsoleRunCommand(String[] args) {
		return false;
	}

	@Override
	public boolean onPlayerRunCommand(Player player, String[] args) {
		Plot plot = DeityCreative.database.getCurrentPlot(player.getName());
		if(plot != null){
			try {
				plot.removePlayer(player.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
			plot.setClaimed(false);
			plot.save();
			plot.resetLand();
			DeityAPI.getAPI().getSecAPI().setGreetingFlag("creative_" + plot.getId(), player, "Available Plot [" + plot.getId() + "]");
			DeityCreative.plugin.chat.sendPlayerMessage(player, "&ePlot unclaimed!");
		} else {
			DeityCreative.plugin.chat.sendPlayerMessage(player, "&eYou don't own a plot. Type &3/creative claim &eto claim a plot!");
			return true;
		}
		return false;
	}

}

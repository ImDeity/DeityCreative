package com.imdeity.deitycreative.cmds.creative;

import org.bukkit.entity.Player;

import com.imdeity.deityapi.api.DeityCommandReceiver;
import com.imdeity.deitycreative.DeityCreative;

public class CreativeReviewCommand extends DeityCommandReceiver {

	@Override
	public boolean onConsoleRunCommand(String[] args) {
		return false;
	}

	@Override
	public boolean onPlayerRunCommand(Player player, String[] args) {
		if(DeityCreative.database.getCurrentPlot(player.getName()) == null){ //player does not have a plot
			DeityCreative.plugin.chat.sendPlayerMessage(player, "&cYou do not have a plot. Type &6/creative claim &cto get one!");
		}else{ //set `needs_promo` to 1 (true)
			DeityCreative.database.setNeedsPromo(player.getName(), true);
			DeityCreative.plugin.chat.sendPlayerMessage(player, "&aYou put your plot up for review");
		}
		return true;
	}

}

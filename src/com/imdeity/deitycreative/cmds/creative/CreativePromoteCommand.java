package com.imdeity.deitycreative.cmds.creative;

import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.api.DeityCommandReceiver;
import com.imdeity.deitycreative.DeityCreative;

//promote <player>, only if needs_promo is 1
public class CreativePromoteCommand extends DeityCommandReceiver {

	@Override
	public boolean onConsoleRunCommand(String[] args) {
		return false;
	}

	@Override
	public boolean onPlayerRunCommand(Player player, String[] args) {
		//TIME TO ADD SEXYNESS
		if(!DeityAPI.getAPI().getDeityPermAPI().isLeastModerator(player)) {
			DeityCreative.plugin.chat.sendPlayerMessage(player, "&cModerator permissions required");
			return false;
		}
		
		return false;
	}

}

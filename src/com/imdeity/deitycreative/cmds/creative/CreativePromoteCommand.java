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
		if(!DeityAPI.getAPI().getDeityPermAPI().isLeastSubAdmin(player)) {
			DeityCreative.plugin.chat.sendPlayerMessage(player, "&cAdmin permissions required");
			return false;
		}
		if(args.length == 0){
			DeityCreative.plugin.chat.sendPlayerMessage(player, "&cNeed to specify a player");
		}else{
			DeityCreative.database.promotePlayer(player, args[0]);
		}
		return false;
	}

}

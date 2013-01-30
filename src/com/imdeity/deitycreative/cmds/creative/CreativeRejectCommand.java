package com.imdeity.deitycreative.cmds.creative;

import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.api.DeityCommandReceiver;
import com.imdeity.deitycreative.DeityCreative;
import com.imdeity.deitycreative.Plot;

public class CreativeRejectCommand extends DeityCommandReceiver {

	@Override
	public boolean onConsoleRunCommand(String[] arg0) {
		return false;
	}

	@Override
	public boolean onPlayerRunCommand(Player player, String[] args) {
		if(!DeityAPI.getAPI().getDeityPermAPI().isLeastSubAdmin(player)) {
			DeityCreative.plugin.chat.sendPlayerMessage(player, "&cAdmin permissions required");
			return false;
		}
		if(args.length == 0){
			DeityCreative.plugin.chat.sendPlayerMessage(player, "&cMust specify a player");
		}else{
			Plot plot = DeityCreative.database.getCurrentPlot(args[0]);
			if(plot == null){
				DeityCreative.plugin.chat.sendPlayerMessage(player, "&6" + args[0] + "&c's plot cannot be found");
			}else{
				if(DeityCreative.database.needsPromotion(player.getName())){
					plot.resetLand();
					DeityCreative.database.setNeedsPromo(args[0], false);
					DeityAPI.getAPI().getChatAPI().sendMailToPlayer("DeityCreative", args[0], "Your promotion request has been rejected and you plot has been reset. Try again");
				}else{
					DeityCreative.plugin.chat.sendPlayerMessage(player, "&cThat player has not requested a promotion");
				}
			}
		}
		return true;
	}

}

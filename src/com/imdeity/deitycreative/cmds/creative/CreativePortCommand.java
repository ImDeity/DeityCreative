package com.imdeity.deitycreative.cmds.creative;

import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.api.DeityCommandReceiver;
import com.imdeity.deitycreative.DeityCreative;

public class CreativePortCommand extends DeityCommandReceiver {

	public CreativePortCommand() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onConsoleRunCommand(String[] args) {
		return false;
	}

	@Override
	public boolean onPlayerRunCommand(Player player, String[] args) {
//		//permission node is the appropriate registerCommand() call
//		if (!Deity.perm.has(player, "creativecontrol.spawn")) {
//			Deity.chat.sendPlayerError(player, "You dont have permission for this command");
//			return true;
//		}
		DeityAPI.getAPI().getPlayerAPI().teleport(player, DeityCreative.plugin.getServer().getWorld("creative_world").getSpawnLocation());
		DeityCreative.plugin.chat.sendPlayerMessage(player, "&eWelcome to the &aImDeity Creative&e world! Type &3/creative &efor help.");
		DeityCreative.plugin.chat.sendPlayerMessage(player, "&eTo begin building, claim a plot with &3/creative claim");
		return true;
	}

}

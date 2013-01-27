package com.imdeity.deitycreative.cmds.creative;

import org.bukkit.entity.Player;

import com.imdeity.deityapi.api.DeityCommandReceiver;
import com.imdeity.deitycreative.DeityCreative;

public class CreativeTeleportCommand extends DeityCommandReceiver {

	@Override
	public boolean onConsoleRunCommand(String[] args) {
		return false;
	}

	@Override
	public boolean onPlayerRunCommand(Player player, String[] args) {
		if (args.length == 1) {
			return new CreativeHomeCommand().onPlayerRunCommand(player, new String[]{args[0]});
		} else {
			DeityCreative.plugin.chat.sendPlayerMessage(player, "You need to enter a playername");
			return true;
		}
	}

}

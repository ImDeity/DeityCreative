package com.imdeity.deitycreative.cmds.creative;

import org.bukkit.entity.Player;

import com.imdeity.deityapi.api.DeityCommandReceiver;

public class CreativeHomeCommand extends DeityCommandReceiver {

	@Override
	public boolean onConsoleRunCommand(String[] arg0) {
		return false;
	}

	@Override
	public boolean onPlayerRunCommand(Player player, String[] args) {
		if(args.length == 0){
			new CreativeTeleportCommand().onPlayerRunCommand(player, new String[]{player.getName()});
		}else{
			new CreativeTeleportCommand().onPlayerRunCommand(player, new String[]{player.getName(), args[0]});
		}
		
		return true;
	}

}

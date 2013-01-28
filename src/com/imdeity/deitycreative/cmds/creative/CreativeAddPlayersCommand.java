package com.imdeity.deitycreative.cmds.creative;

import java.sql.SQLDataException;

import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.api.DeityCommandReceiver;
import com.imdeity.deitycreative.DeityCreative;

public class CreativeAddPlayersCommand extends DeityCommandReceiver {

	@Override
	public boolean onConsoleRunCommand(String[] args) {
		return false;
	}

	@Override
	public boolean onPlayerRunCommand(Player player, String[] args) {
		if(!DeityAPI.getAPI().getDeityPermAPI().isSubAdmin(player) && !DeityAPI.getAPI().getDeityPermAPI().isAdmin(player)) {
			DeityCreative.plugin.chat.sendPlayerMessage(player, "&cAdmin permissions required");
			return false;
		}
		try {
			DeityCreative.database.addCurrentPlayersToTable();
		} catch (SQLDataException e) {
			e.printStackTrace();
		}
		return true;
	}

}

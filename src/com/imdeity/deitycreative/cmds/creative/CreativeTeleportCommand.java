package com.imdeity.deitycreative.cmds.creative;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.imdeity.deityapi.api.DeityCommandReceiver;
import com.imdeity.deitycreative.DeityCreative;
import com.imdeity.deitycreative.Plot;

public class CreativeTeleportCommand extends DeityCommandReceiver {

	@Override
	public boolean onConsoleRunCommand(String[] args) {
		return false;
	}

	@Override
	public boolean onPlayerRunCommand(Player player, String[] args) {
		String playername;
		int which = 0;
		ArrayList<Plot> plots;
		if(args.length == 0){
			playername = player.getName();
		}else if(args.length == 1){
			playername = args[0];
		}else{
			playername = args[0];
			try{
				which = Integer.parseInt(args[1]);
			} catch (NumberFormatException e){
				which = 0;
			}
		}
		plots = DeityCreative.database.getAllPlots(playername);
		if(plots == null || plots.size() == 0){
			DeityCreative.plugin.chat.sendPlayerMessage(player, "&cPlayer does not have any plots");
		}else{
			if(which - 1 > plots.size()) which = 0;
			else if(which == 0) which = plots.size() - 1;
			else which -= 1;
			Plot plot = plots.get(which);
			if(plot != null){
				int x = 0, y = 21, z = 0;
				x = (int) plot.getMaxPoint().getX() - (plot.getPlotSize() / 2);
				z = (int) plot.getMaxPoint().getZ() + 1;
				Location point = new Location(plot.getWorld(), x + .5, y, z + .5, 180, 0);
				player.teleport(point);
				DeityCreative.plugin.chat.sendPlayerMessage(player, "&eWelcome to &a" + plot.getPlayername() + "&e's Plot!");
				return true;
			}else{
				DeityCreative.plugin.chat.sendPlayerMessage(player, "&cPlot doesn't exist");
			}
		}
		return true;
	}

}

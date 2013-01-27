package com.imdeity.deitycreative.cmds.creative;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.api.DeityCommandReceiver;
import com.imdeity.deitycreative.DeityCreative;
import com.imdeity.deitycreative.Plot;

public class CreativeCreateCommand extends DeityCommandReceiver {
	
	@Override
	public boolean onConsoleRunCommand(String[] arg0) {
		return false;
	}

	@Override
	public boolean onPlayerRunCommand(final Player player, String[] args) { // /creative create length width size
		if (!DeityAPI.getAPI().getDeityPermAPI().isAdmin(player)) {
			DeityCreative.plugin.chat.sendPlayerMessage(player, "&cAdmin permissions required");
		}
		
		if (args.length < 2) {
			DeityCreative.plugin.chat.sendPlayerMessage(player, "&cInvalid format: /creative create [Plots per side] [Size of Plot Side]");
		}
		
		final int plotSideCnt = Integer.parseInt(args[0]);
		final int plotSize = Integer.parseInt(args[1]);
		
		int totalPlotCount = plotSideCnt * plotSideCnt; // Total plots to create
		
		DeityCreative.plugin.chat.sendPlayerMessage(player, "Creating " + totalPlotCount + " with plot size of " + plotSize + ". Please wait.. ");
		
		DeityCreative.plugin.getServer().getScheduler().scheduleSyncDelayedTask(DeityCreative.plugin, new Runnable() {

			public void run() {
				Plot.newPlot(player, (CommandSender) player, player.getLocation(), plotSideCnt, plotSize);
			}
			
		}, 1L);
		return true;
	}

}

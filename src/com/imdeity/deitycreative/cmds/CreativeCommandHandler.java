package com.imdeity.deitycreative.cmds;

import com.imdeity.deityapi.api.DeityCommandHandler;
import com.imdeity.deitycreative.cmds.creative.CreativeAddPlayersCommand;
import com.imdeity.deitycreative.cmds.creative.CreativeClaimCommand;
import com.imdeity.deitycreative.cmds.creative.CreativeCreateCommand;
import com.imdeity.deitycreative.cmds.creative.CreativeForceUnclaimCommand;
import com.imdeity.deitycreative.cmds.creative.CreativeHomeCommand;
import com.imdeity.deitycreative.cmds.creative.CreativePortCommand;
import com.imdeity.deitycreative.cmds.creative.CreativePromoteCommand;
import com.imdeity.deitycreative.cmds.creative.CreativeRejectCommand;
import com.imdeity.deitycreative.cmds.creative.CreativeReviewCommand;
import com.imdeity.deitycreative.cmds.creative.CreativeTeleportCommand;
import com.imdeity.deitycreative.cmds.creative.CreativeUnclaimCommand;

public class CreativeCommandHandler extends DeityCommandHandler {

	public CreativeCommandHandler(String pluginName, String baseCommandName) {
		super(pluginName, baseCommandName);
	}

	@Override
	protected void initRegisteredCommands() {
		registerCommand("", new String[]{}, "", "Teleport to the creative world", new CreativePortCommand(), "creativecontrol.spawn");
		registerCommand("create", new String[]{"make"}, "<Plots per side> <Size of plot side>", "Create creative plots", new CreativeCreateCommand() , "");
		registerCommand("claim", new String[]{"me-gusta"}, "", "Claim a plot", new CreativeClaimCommand(), "");
		registerCommand("unclaim", new String[]{"no-want"}, "", "Unclaim your plot", new CreativeUnclaimCommand(), "");
		registerCommand("home", new String[]{"mine"}, "", "Go to your plot", new CreativeHomeCommand(), "");
		registerCommand("teleport", new String[]{"tp"}, "<player>", "Teleport to someone elses plot", new CreativeTeleportCommand(), "");
		registerCommand("forceunclaim", new String[]{"fu"}, "", "Forceably unclaim a plot", new CreativeForceUnclaimCommand(), "deity.creative.forceunclaim");
		registerCommand("add-players", new String[]{"ap"}, "", "Add current players to database that aren't already", new CreativeAddPlayersCommand(), "deity.creative.addplayers");
		registerCommand("promote", new String[]{"add-sexyness", "accept"}, "<player>", "Promote a player", new CreativePromoteCommand(), "deity.creative.promote");
		registerCommand("reject", new String[]{"no-like"}, "<player>", "Reject a plaeyer's promotion request", new CreativeRejectCommand(), "deity.creative.reject");
		registerCommand("review", new String[]{"request-promo"}, "", "Request a promotion", new CreativeReviewCommand(), "");
	}
	
}

package com.imdeity.deitycreative;

import com.imdeity.deityapi.api.DeityPlugin;
import com.imdeity.deitycreative.cmds.CreativeCommandHandler;

/*
 * DeityCreative, ported to the new API.
 * 
 * Promotion system in the works, /creative promote <player> to promote someone
 * 
 * Promotion request can be denied with /creative reject <player>. This will clear the land and set their
 * `needs_promo` to 0 (false)
 */

public class DeityCreative extends DeityPlugin{

	public static DeityCreative plugin;
	public static DeityCreativeDatabase database;
	
	@Override
	protected void initCmds() {
		registerCommand(new CreativeCommandHandler("DeityCreative", "creative"));
	}

	@Override
	protected void initConfig() {}

	@Override
	protected void initDatabase() {
		try {
			database = new DeityCreativeDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void initInternalDatamembers() {}

	@Override
	protected void initLanguage() {}

	@Override
	protected void initListeners() {}

	@Override
	protected void initPlugin() {
		plugin = this;
	}

	@Override
	protected void initTasks() {
		String world = "creative_world";
		if (this.getServer().getWorld(world) != null) {
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new TimeLocker(getServer().getWorld(world)), 600, 600);
		}
	}
	

}

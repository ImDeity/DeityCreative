package com.imdeity.deitycreative;

import com.imdeity.deityapi.api.DeityPlugin;
import com.imdeity.deitycreative.cmds.CreativeCommandHandler;

/*
 * DeityCreative, ported to the new API.
 * 
 * Most of the code is the same as the original (for now). The code that has changed would be code in which
 * the format had to change. For instance, Deity.sec would need to be changed to DeityAPI.getAPI().getSecAPI().
 * 
 * Also, instead of all the commands being in the main class, each command now has own DeityCommandReciever class.
 * 
 * BUG: when using /creative create, two sides are missing from the plots. Therefore the one in the middle are fine, but
 * some on the edges have sides missing.
 */

public class DeityCreative extends DeityPlugin{

	public static DeityCreative plugin;
	public static DeityCreativeDatabase database;
	
	@Override
	protected void initCmds() {
		registerCommand(new CreativeCommandHandler("DeityCreative", "creative"));
	}

	@Override
	protected void initConfig() {
		
	}

	@Override
	protected void initDatabase() {
		try {
			database = new DeityCreativeDatabase();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void initInternalDatamembers() {
		// TODO may not need
	}

	@Override
	protected void initLanguage() {
		// TODO might not need
	}

	@Override
	protected void initListeners() {
		// TODO may need:
		//        * BlockPlaceEvent (not your plot)
		//			-> if not worldgaurd will do it
	}

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

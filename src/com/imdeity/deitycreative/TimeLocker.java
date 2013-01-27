package com.imdeity.deitycreative;

import org.bukkit.World;

/*
 * Copied from first DeityCreative
 */

public class TimeLocker implements Runnable {

	private World world;

	public TimeLocker(World world) {
		super();
		this.world = world;
	}

	public void run() {
		if (world != null) {
			world.setFullTime((long) 6000);
		}
	}
}


package com.imdeity.deitycreative;

import java.io.IOException;
import java.sql.SQLDataException;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.imdeity.deityapi.Deity;
import com.imdeity.deityapi.records.Database;
import com.imdeity.deityapi.records.DatabaseResults;


/*
 * 
 * TODO:
 * Ranks
 * Request promo
 * Claim alternate size
 * List own plotS
 * Unclaim own plotS
 * 
 */

public class DeityCreative extends JavaPlugin {

	public Database db = Deity.data.getDB();

	public final Logger log = Logger.getLogger("Minecraft");
	public boolean logDebug = true;

	private int task_id_1;

	public void onDisable() {

		this.getServer().getScheduler().cancelTask(this.task_id_1);
		out("Disabled!");
	}

	public void onEnable() {

		this.getServer().getPluginManager().registerEvents(new CreativePlayerListener(this), this);
			
		setupDatabase();
		
		if (this.getServer().getWorld("creative_world") != null) {
            this.task_id_1 = Deity.server
                            .getServer()
                            .getScheduler()
                            .scheduleSyncRepeatingTask(
                                            this,
                                            new TimeLocker(this.getServer().getWorld(
                                                            "creative_world")), 600, 600);
		}
		
		out("Enabled!");
	}

	// Setup DB tables
	private void setupDatabase() {

        Deity.data
        .getDB()
        .Write("CREATE TABLE IF NOT EXISTS "
                        + Deity.data.getDB().tableName("deity_creative_",
                                        "plots")
                        + " ("
                        + "`id` INT(16) NOT NULL AUTO_INCREMENT ,"
                        + "`playername` VARCHAR(20) NOT NULL DEFAULT '' ,"
                        + "`world` VARCHAR(32) NOT NULL ,"
                        + "`min_x` INT(16) NOT NULL ,"
                        + "`min_z` INT(16) NOT NULL ,"
                        + "`max_x` INT(16) NOT NULL ,"
                        + "`max_z` INT(16) NOT NULL ,"
                        + "`is_claimed` INT(1) NOT NULL DEFAULT '0',"
                        + "PRIMARY KEY (`id`) ,"
                        + "KEY (`playername`)"
                        + ") ENGINE=MYISAM COMMENT='Deity Creative World Plots' AUTO_INCREMENT=1000;");
		
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[]) {

		if (sender instanceof Player) {

			Player player = (Player) sender;

			if (player.getWorld().getName().equalsIgnoreCase("creative_world")) {

				if (args.length > 0) {
					if (args[0].equalsIgnoreCase("claim")) {
						return this.claimCommand(player, sender, args);
					} else if (args[0].equalsIgnoreCase("unclaim")) {
						return this.unclaimCommand(player, args, sender);
					} else if (args[0].equalsIgnoreCase("teleport")
							|| args[0].equalsIgnoreCase("tp")) {
						return this.teleportCommand(player, args);
					} else if (args[0].equalsIgnoreCase("home")) {
						return this.homeCommand(player, player.getName());
					} else if (args[0].equalsIgnoreCase("create")) {
						return this.createPlot(player, sender, args);
					} else if (args[0].equalsIgnoreCase("forceunclaim") || args[0].equalsIgnoreCase("fu")) {
						return this.adminUnclaim(player, args, sender);
					} else {
						return helpCommand(player);
					}
				} else {

					return helpCommand(player);
					
				}
			} else {
				if (args.length == 0) {
					if (!Deity.perm.has(player, "creativecontrol.spawn")) {
						Deity.chat.sendPlayerError(player, "You dont have permission for this command");
						return true;
					}
					Deity.player.teleport(player, Deity.server.getServer()
							.getWorld("creative_world").getSpawnLocation());
					Deity.chat
							.sendPlayerMessage(player,
									"&eWelcome to the &aImDeity Creative&e world! Type &3/creative &efor help.");
					Deity.chat
							.sendPlayerMessage(player,
									"&eTo begin building, claim a plot with &3/creative claim");

					return true;
				} else {
					Deity.chat
							.sendPlayerMessage(player,
									"&eType &3/creative &eto teleport to the ImDeity Creative world");
					return true;
				}
			}

		}
        return false;
	}


	// Remove a player's plot
	private boolean adminUnclaim(Player player, String[] args,
			CommandSender sender) {
		if (!Deity.perm.isSubAdmin(player) && !Deity.perm.isAdmin(player) && !Deity.perm.has(player, "deity.creative.forceunclaim")) {
			Deity.chat.sendPlayerError(player, "Admin permissions required");
			return false;
		}
		
		if (args.length < 2) {
			Deity.chat.sendPlayerError(player, "Plot ID required");
		}
		
		String plotId = args[1];
        String sql = "SELECT `id`, `playername` FROM "
				+ Deity.data.getDB().tableName("deity_creative_", "plots")
				+ " WHERE id = ?";
		DatabaseResults query = Deity.data.getDB().Read2(sql, plotId);
		if (query != null && query.hasRows()) {
			try {
				int id = query.getInteger(0, "id");
				String plot_owner = query.getString(0, "playername");
				Plot plot = new Plot(id);
				try {
					plot.removePlayer(plot_owner);
				} catch (IOException e) {
					e.printStackTrace();
				}
				plot.setClaimed(false);
				plot.save();
				plot.resetLand(player);
				Deity.sec.setGreetingFlag("creative_" + id, sender,
						"Available Plot [" + id + "]");
				Deity.chat.sendPlayerMessage(player, "&ePlot unclaimed!");
				
				return true;
				
			} catch (SQLDataException e) {
				e.printStackTrace();
			}
		} else {
			Deity.chat.sendPlayerMessage(player, "&eInvalid plot ID?");
		}
		return false;
	}

	private boolean helpCommand(Player player) {

		Deity.chat.sendPlayerMessage(player,
				"&6]---[&eCreative Control Help&6]---[");
		Deity.chat.sendPlayerMessage(player, "&3/creative claim &f- &eClaim a plot");
		Deity.chat.sendPlayerMessage(player, "&3/creative home &f- &eGoto your plot");
		Deity.chat.sendPlayerMessage(player, "&3/creative unclaim &f- &eDelete contents of plot + unclaim");
		Deity.chat.sendPlayerMessage(player, "&3/creative tp [name] &f- &eTeleport to a player's plot");
		
		if (Deity.perm.isAdmin(player)) {
			Deity.chat.sendPlayerMessage(player, "&cAdmin commands: &3create, forceunclaim");
		}
		return true;
	}

	// Create plots
	private boolean createPlot(final Player player, final CommandSender sender, String[] args) {
		
		if (!Deity.perm.isAdmin(player)) {
			Deity.chat.sendPlayerError(player, "Admin permissions required");
			return false;
		}
		
		if (args.length < 3) {
			Deity.chat.sendPlayerError(player, "Invalid format: /creative create [Plots per side] [Size of Plot Side]");
			return false;
		}
		
		final int plotSideCnt = Integer.parseInt(args[1]);
		final int plotSize = Integer.parseInt(args[2]);
		
		int totalPlotCount = plotSideCnt * plotSideCnt; // Total plots to create
		
		Deity.chat.sendPlayerMessage(player, "Creating " + totalPlotCount + " with plot size of " + plotSize + ". Please wait.. ");
		
		this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {

			public void run() {
				Plot.newPlot(player, sender, plotSideCnt, plotSize);
			}
			
		}, 1L);
		
		
		return true;
		
	}

	// Claim a plot
	private boolean claimCommand(Player player, CommandSender sender, String[] args) {

		String sql = "SELECT `id` FROM "
				+ Deity.data.getDB().tableName("deity_creative_", "plots")
				+ " WHERE playername = ?";
		DatabaseResults query = Deity.data.getDB().Read2(sql, player.getName());
		
		if (query != null && query.hasRows()) {
			Deity.chat
					.sendPlayerError(player,
							"Sorry you already have a plot. use /creative home to go there");
			return true;
		}
		
		sql = "SELECT `id` FROM "
				+ Deity.data.getDB().tableName("deity_creative_", "plots")
				+ " WHERE is_claimed = 0 AND playername = '' AND id > 2000 ORDER BY `id` ASC LIMIT 1;";
		DatabaseResults query2 = Deity.data.getDB().Read2(sql);
		if (query2 != null && query2.hasRows()) {
			try {
				int id = query2.getInteger(0, "id");
				if (id > 2000) {
					Plot plot = new Plot(id);
					plot.addPlayer(player.getName());
					plot.setClaimed(true);
					plot.save();
					Deity.sec.setGreetingFlag("creative_" + id, sender,
							"Owner: " + player.getName() + " [" + id + "]");
					this.homeCommand(player, player.getName());
				} else {
					Deity.chat.sendPlayerError(player, "Sorry there are currently no plots available to claim. Please try again later");
				}
				
				return true;
				
			} catch (SQLDataException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} else {
			Deity.chat.sendPlayerError(player, "Sorry there are currently no plots available to claim. Please try again later");
		}
		
		return true;
	}

	private boolean unclaimCommand(Player player, String[] args, CommandSender sender) {
        String sql = "SELECT `id` FROM "
				+ Deity.data.getDB().tableName("deity_creative_", "plots")
				+ " WHERE playername = ?";
		DatabaseResults query = Deity.data.getDB().Read2(sql, player.getName());
		if (query != null && query.hasRows()) {
			try {
				int id = query.getInteger(0, "id");
				Plot plot = new Plot(id);
				try {
					plot.removePlayer(player.getName());
				} catch (IOException e) {
					e.printStackTrace();
				}
				plot.setClaimed(false);
				plot.save();
				plot.resetLand(player);
				Deity.sec.setGreetingFlag("creative_" + id, sender,
						"Available Plot [" + id + "]");
				Deity.chat.sendPlayerMessage(player, "&ePlot unclaimed!");
				
				return true;
				
			} catch (SQLDataException e) {
				e.printStackTrace();
			}
		} else {
			Deity.chat.sendPlayerMessage(player, "&eYou don't own a plot. Type &3/creative claim &eto claim a plot!"); return true;
		}
		return false;
	}
	
	private boolean teleportCommand(Player player, String[] split) {
		if (split.length == 2) {
			return this.homeCommand(player, split[1]);
		} else {
			Deity.chat
					.sendPlayerError(player, "You need to enter a playername");
			return true;
		}
	}

	// Teleport a player to his claimed plot
	// .. changing 2nd variable will port to another player's plot
	private boolean homeCommand(Player player, String playername) {
		
		String sql = "SELECT `id` FROM "
				+ Deity.data.getDB().tableName("deity_creative_", "plots")
				+ " WHERE playername = ?";
		DatabaseResults query = Deity.data.getDB().Read2(sql, playername);
		if (query != null && query.hasRows()) {
			try {
				int id = query.getInteger(0, "id");
				Plot plot = new Plot(id);
				int x = 0, y = 23, z = 0;
				x = (int) plot.getMaxPoint().getX() - (plot.getPlotSize() / 2);
				z = (int) plot.getMaxPoint().getZ() + 1;
				Location point = new Location(plot.getWorld(), x + .5, y,
						z + .5, 180, 0);
				player.teleport(point);
				Deity.chat.sendPlayerMessage(player, "&eWelcome to &a" + playername
						+ "&e's Plot!");
				return true;
			} catch (SQLDataException e) {
				e.printStackTrace();
			}
		} else {
			Deity.chat.sendPlayerMessage(player, "&eYou don't own a plot. Type &3/creative claim &eto claim a plot!");
			return true;
		}

		return false;
	}

	public void out(String message) {
		if (logDebug == true) {
			PluginDescriptionFile pdfFile = this.getDescription();
			log.info("[" + pdfFile.getName() + "] " + message);
		}
	}

	public void broadcast(String message) {
		getServer().broadcastMessage( message );
	}

	public boolean hasPermission(Player player, String node) {
		return Deity.perm.has(player, node);
	}

	public static double round(double val, int places) {
		long factor = (long)Math.pow(10,places);

		// Shift the decimal the correct number of places
		// to the right.
		val = val * factor;

		// Round to the nearest integer.
		long tmp = Math.round(val);

		// Shift the decimal the correct number of places
		// back to the left.
		return (double)tmp / factor;
	}

}

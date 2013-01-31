package com.imdeity.deitycreative;

import java.io.IOException;
import java.sql.SQLDataException;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.records.DatabaseResults;

public class Plot {

	public static final int PLOT_HEIGHT = 19;
	private String playername;
	private Location minPoint, maxPoint;
	private World world;
	private int plotSize, id;
	private boolean isClaimed;
	
	public Plot(int id){
		String sql = "SELECT `id`, `playername`, `world`, `min_x`, `max_x`, `min_z`, `max_z`, `is_claimed`, `plot_size` FROM "
				+ DeityCreative.database.tableName("deity_creative_", "plots") + " WHERE `id` = ?;";
		DatabaseResults query = DeityCreative.database.readEnhanced(sql, id);
		if (query != null) {
			try {
				setAllFields(query);
			} catch (SQLDataException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void newPlot(Player player, CommandSender sender, Location pos, int side, int size){
		int pathSize = 4;
		int plotSizeAndPath = size + pathSize;
		World world = pos.getWorld();
		int playerX = pos.getBlockX();
		int playerZ = pos.getBlockZ();
		int plotCount = 0;
		for(int i=0; i<side; i++){
			for(int j=0; j<side; j++){
				plotCount++;
				int plotMinX = playerX + (j * plotSizeAndPath);
				int plotMinZ = playerZ + (i * plotSizeAndPath);
				
				int plotMaxX = plotMinX + plotSizeAndPath - 1;
				int plotMaxZ = plotMinZ + plotSizeAndPath - 1;
				
				Location minMarker = new Location(world, plotMinX, 1, plotMinZ);
				Location maxMarker = new Location(world, plotMaxX, 1, plotMaxZ);
				DeityAPI.getAPI().getWorldEditAPI().setAreaWithBlock(world.getName(), minMarker, maxMarker, "0"); //air
				
				minMarker = new Location(world, plotMinX, 1, plotMinZ);
				maxMarker = new Location(world, plotMaxX, PLOT_HEIGHT - 1, plotMaxZ);
				DeityAPI.getAPI().getWorldEditAPI().setAreaWithBlock(world.getName(), minMarker, maxMarker, "3"); //dirt
				
				minMarker = new Location(world, plotMinX, PLOT_HEIGHT, plotMinZ);
				maxMarker = new Location(world, plotMaxX, PLOT_HEIGHT, plotMaxZ);
				DeityAPI.getAPI().getWorldEditAPI().setAreaWithBlock(world.getName(), minMarker, maxMarker, "2"); //grass
				
				minMarker = new Location(world, plotMinX, PLOT_HEIGHT + 1, plotMinZ);
				maxMarker = new Location(world, plotMaxX, PLOT_HEIGHT + 1, plotMaxZ);
				DeityAPI.getAPI().getWorldEditAPI().setAreaWithBlock(world.getName(), minMarker, maxMarker, "43"); //double half slab
				
				minMarker = new Location(world, plotMinX, PLOT_HEIGHT + 1, plotMinZ);
				maxMarker = new Location(world, plotMaxX - pathSize, PLOT_HEIGHT + 1, plotMaxZ - pathSize); //originally 20
				DeityAPI.getAPI().getWorldEditAPI().setAreaWithBlock(world.getName(), minMarker, maxMarker, "0"); //air
				
				setupRegion(plotMinX, plotMinZ, (plotMaxX - pathSize), (plotMaxZ - pathSize), size, world, sender);
			}
		}
		
		//create missing edges
		int sideLength = (size * side) + (side * pathSize) - 1;
		
		Location corner = new Location(world, playerX - pathSize + 1, PLOT_HEIGHT + 1, playerZ - pathSize + 1);
		Location point = new Location(world, playerX, PLOT_HEIGHT + 1, playerZ + sideLength);
		DeityAPI.getAPI().getWorldEditAPI().setAreaWithBlock(world.getName(), corner, point, "43"); //double half slab
		
		point = new Location(world, playerX + sideLength, PLOT_HEIGHT + 1, playerZ);
		DeityAPI.getAPI().getWorldEditAPI().setAreaWithBlock(world.getName(), corner, point, "43"); //double half slab
		
		DeityCreative.plugin.chat.sendPlayerMessage(player, "&aDone creating plots " + plotCount + " plots!");
	}
	
	public static void newPlot(World world, Location min, Location max){
		if (min.getX() > max.getX()) {
            double tmpMinX = min.getX();
            double tmpMaxX = max.getX();
            min.setX(tmpMaxX);
            max.setX(tmpMinX);
	    }
	    if (min.getZ() > max.getZ()) {
	            double tmpMinZ = min.getZ();
	            double tmpMaxZ = max.getZ();
	            min.setZ(tmpMaxZ);
	            max.setZ(tmpMinZ);
	    }

        String sql = "SELECT MAX(id) FROM " + DeityCreative.database.tableName("deity_creative_", "plots") + " WHERE id > 2000";
        DatabaseResults query = DeityCreative.database.readEnhanced(sql, new Object[]{});

		if (query != null) {
			try {
				int id = 3000;
				if (query.getInteger(0, "MAX(id)") != null && query.getInteger(0, "MAX(id)") != 0) {
					id = query.getInteger(0, "MAX(id)") + 1;
				}
				sql = "INSERT INTO " + DeityCreative.database.tableName("deity_creative_", "plots")
						+ " (`id`, `world`, `min_x`, `min_z`, `max_x`, `max_z`) "
						+ " VALUES (?, ?,?,?,?,?);";

				DeityCreative.database.write(sql, id, world.getName(), (int) min.getX(), (int) min.getZ(), (int) max.getX(), (int) max.getZ());
				DeityAPI.getAPI().getSecAPI().protectRegion(world, "creative_" + id, min, max);
			} catch (SQLDataException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void setupRegion(double plotMinX, double plotMinZ, double plotMaxX, double plotMaxZ, int plotSize, World currentWorld, CommandSender sender) {
		String sql = "SELECT MAX(id) FROM " + DeityCreative.database.plots + " WHERE id > 2000";
		DatabaseResults query = DeityCreative.database.readEnhanced(sql);
		if (query != null) {
			try {
				int id = 3000;
				if (query.getInteger(0, "MAX(id)") != null
						&& query.getInteger(0, "MAX(id)") != 0) {
					id = query.getInteger(0, "MAX(id)") + 1;
				}
				sql = "INSERT INTO "
						+ DeityCreative.database.tableName("deity_creative_", "plots")
						+ " (`id`, `world`, `min_x`, `min_z`, `max_x`, `max_z`, plot_size) "
						+ " VALUES (?, ?, ?, ?, ?, ?, ?);";
				DeityCreative.database.write(sql, id, currentWorld.getName(), (int) plotMinX, (int) plotMinZ, (int) plotMaxX, (int) plotMaxZ, plotSize);
				DeityAPI.getAPI().getSecAPI().protectRegion(currentWorld, "creative_" + id,
						new Location(currentWorld, plotMinX, 1, plotMinZ),
						new Location(currentWorld, plotMaxX, 127, plotMaxZ));
				DeityAPI.getAPI().getSecAPI().setGreetingFlag("creative_" + id, sender, "Available Plot [" + id + "]");
			} catch (SQLDataException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setAllFields(DatabaseResults query) throws SQLDataException {
		if (query.getInteger(0, "id") != null) {
			this.id = query.getInteger(0, "id");
		}
		if (query.getString(0, "playername") != null) {
			this.playername = (query.getString(0, "playername"));
		}
		if (query.getString(0, "world") != null) {
			this.world = DeityCreative.plugin.getServer().getWorld(
					query.getString(0, "world"));
		}
		int x = 0, z = 0;
		if (query.getInteger(0, "min_x") != null) {
			x = query.getInteger(0, "min_x");
		}
		if (query.getInteger(0, "min_z") != null) {
			z = query.getInteger(0, "min_z");
		}
		this.minPoint = new Location(world, x, 1, z);

		if (query.getInteger(0, "max_x") != null) {
			x = query.getInteger(0, "max_x");
		}
		if (query.getInteger(0, "max_z") != null) {
			z = query.getInteger(0, "max_z");
		}
		if (query.getInteger(0, "plot_size") != null) {
			this.plotSize = query.getInteger(0, "plot_size");
		}
		this.maxPoint = new Location(world, x, 127, z);

		if (query.getInteger(0, "is_claimed") != null) {
			this.isClaimed = (query.getInteger(0, "is_claimed") == 0 ? false
					: true);
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void addPlayer(String playername) throws IOException {
		this.playername = playername;
		/*
		 * String sql = "UPDATE " +
		 * Deity.data.getDB().tableName("deity_creative_", "plots") +
		 * " SET `playername` = ? WHERE `id` = ?;";
		 * 
		 * Deity.data.getDB().Write(sql, playername, this.getId());
		 */
		DeityAPI.getAPI().getSecAPI().addMemberToRegion(playername, this.getWorld(), "creative_" + this.getId());
	}

	public void removePlayer(String playername) throws IOException {
		this.playername = "";
		String sql = "UPDATE " + DeityCreative.database.tableName("deity_creative_", "plots") + " SET `playername` = '' WHERE `id` = ?;";
		DeityCreative.database.write(sql, this.getId());
		DeityAPI.getAPI().getSecAPI().removeMemberFromRegion(playername, this.getWorld(), "creative_" + this.getId());
	}

	public String getPlayername() {
		return playername;
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public Location getMinPoint() {
		return minPoint;
	}

	public void setMinPoint(Location minPoint) {
		this.minPoint = minPoint;
	}

	public Location getMaxPoint() {
		return maxPoint;
	}

	public void setMaxPoint(Location maxPoint) {
		this.maxPoint = maxPoint;
	}

	public Integer getPlotSize() {
		return plotSize;
	}

	public boolean isClaimed() {
		return isClaimed;
	}

	public void setClaimed(boolean isClaimed) {
		this.isClaimed = isClaimed;
	}

	public void save() {
		String sql = "UPDATE " + DeityCreative.database.tableName("deity_creative_", "plots")
				+ " SET `playername` = ?, `world` = ?, `min_x` = ?, `min_z` = ?, "
				+ "`max_x` = ?, `max_z` = ?, `plot_size` = ?, `is_claimed` = ? WHERE `id` = ?;";

		DeityCreative.database.write(sql, this.playername, this.world.getName(),
				(int) this.minPoint.getX(), (int) this.minPoint.getZ(),
				(int) this.maxPoint.getX(), (int) this.maxPoint.getZ(),
				(int) this.plotSize, (this.isClaimed ? 1 : 0), this.id);
	}

	public void resetLand() {

		// Bugged worldguard chunk regen so not doing it this way anymore:
		// Deity.edit.regenRegion(getWorld(), "creative_" + id);

		World currentWorld = getWorld();

		// Set air to clear
		DeityAPI.getAPI().getWorldEditAPI().setAreaWithBlock(currentWorld.getName(), minPoint, maxPoint, "0"); //air

		// Set dirt
		Location minMarker = new Location(currentWorld, getMinPoint().getX(), 1, getMinPoint().getZ());
		Location maxMarker = new Location(currentWorld, getMaxPoint().getX(), PLOT_HEIGHT - 1, getMaxPoint().getZ());
		DeityAPI.getAPI().getWorldEditAPI().setAreaWithBlock(currentWorld.getName(), minMarker, maxMarker, "3"); //dirt

		// Create base grass
		minMarker = new Location(currentWorld, getMinPoint().getX(), PLOT_HEIGHT, getMinPoint().getZ());
		maxMarker = new Location(currentWorld, getMaxPoint().getX(), PLOT_HEIGHT, getMaxPoint().getZ());
		DeityAPI.getAPI().getWorldEditAPI().setAreaWithBlock(currentWorld.getName(), minMarker, maxMarker, "2"); //grass
		
	}

	// public void saveLand() {
	// Deity.edit.saveSchematicFromRegion(playername + "_" + id, "creative_"
	// + id, this.getWorld().getName());
	// }
	
}

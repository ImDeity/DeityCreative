package com.imdeity.deitycreative;

import java.io.IOException;
import java.sql.SQLDataException;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.imdeity.deityapi.Deity;
import com.imdeity.deityapi.records.DatabaseResults;

public class Plot {
    private int id = 0;
    private String playername = "";
    private World world = null;
    private Location minPoint;
    private Location maxPoint;
    private boolean isClaimed = false;
	private Integer plotSize = null;
    
    public final static Logger log = Logger.getLogger("Minecraft");

    public Plot(int id) {
            String sql = "SELECT `id`, `playername`, `world`, `min_x`, `max_x`, `min_z`, `max_z`, `is_claimed`, `plot_size`"
                            + " FROM "
                            + Deity.data.getDB().tableName("deity_creative_", "plots")
                            + " WHERE `id` = ?;";
            DatabaseResults query = Deity.data.getDB().Read2(sql, id);
            if (query != null) {
                    try {
                            setAllFields(query);
                    } catch (SQLDataException e) {
                            e.printStackTrace();
                    }
            }
    }

    
	public static boolean newPlot(Player player, CommandSender sender, int plotSideCnt, int plotSize) {
		
		int pathSize = 4;
		
		int plotSizePlusPath = plotSize + pathSize;
		
		Location playerLoc = player.getLocation();
		World currentWorld = playerLoc.getWorld();
		
		int changedBlocks = 0;
		
		// Round player position
		double playerX = Math.floor(playerLoc.getX());
		double playerZ = Math.floor(playerLoc.getZ());
		
		int createdPlotCnt = 0;
		
		// The big double-loop
		for (int j = 0; j < plotSideCnt; j++) {
			for (int i = 0; i < plotSideCnt; i++) {
				
				createdPlotCnt++;
				
				// First, flatten area for the whole plot + roads
				double plotMinX = playerX + (i * plotSizePlusPath);
				double plotMinZ = playerZ + (j * plotSizePlusPath);
				
				double plotMaxX = plotMinX + plotSizePlusPath - 1;
				double plotMaxZ = plotMinZ + plotSizePlusPath - 1; 
				
				Location minMarker = new Location(currentWorld,
						plotMinX, 1, plotMinZ);
				Location maxMarker = new Location(currentWorld,
						(plotMaxX), 127, 
						(plotMaxZ));
				changedBlocks = Deity.edit.setAreaWithBlock(
						currentWorld.getName(), minMarker, maxMarker, "0");
				if (changedBlocks > 0) {
					// Deity.chat.sendPlayerMessage(player, "Cleared land.. " + changedBlocks + " modified (" + createdPlotCnt + ")");
					log.info("Cleared land.. " + changedBlocks + " modified (" + createdPlotCnt + ")");
					// log.info("i: " + i + "; j: " + j + "; minx: " + plotMinX + "; minz: " + plotMinZ + "; maxX: " + plotMaxX + "; maxZ: " + plotMaxZ);
				}
				
				
				// Create base dirt
				minMarker = new Location(currentWorld,
						plotMinX, 1, plotMinZ);
				maxMarker = new Location(currentWorld,
						(plotMaxX), 18, 
						(plotMaxZ));				
				changedBlocks = Deity.edit.setAreaWithBlock(
						currentWorld.getName(), minMarker, maxMarker, "3");
				if (changedBlocks > 0) {
					// Deity.chat.sendPlayerMessage(player, "Set base dirt.. " + changedBlocks + " modified (" + createdPlotCnt + ")");
					log.info("Set base dirt.. " + changedBlocks + " modified (" + createdPlotCnt + ")");
				}
				
				// Create base grass
				minMarker = new Location(currentWorld,
						plotMinX, 19, plotMinZ);
				maxMarker = new Location(currentWorld,
						(plotMaxX), 19, 
						(plotMaxZ));				
				changedBlocks = Deity.edit.setAreaWithBlock(
						currentWorld.getName(), minMarker, maxMarker, "2");
				if (changedBlocks > 0) {
					// Deity.chat.sendPlayerMessage(player, "Set base grass.. " + changedBlocks + " modified (" + createdPlotCnt + ")");
					log.info("Set base grass.. " + changedBlocks + " modified (" + createdPlotCnt + ")");
				}
				
				// Set path bricks over whole thing
				minMarker = new Location(currentWorld,
						plotMinX, 20, plotMinZ);
				maxMarker = new Location(currentWorld,
						(plotMaxX), 20, 
						(plotMaxZ));		
				changedBlocks = Deity.edit.setAreaWithBlock(
						currentWorld.getName(), minMarker, maxMarker, "43");
				if (changedBlocks > 0) {
					// Deity.chat.sendPlayerMessage(player, "Creating path layer.. " + changedBlocks + " modified (" + createdPlotCnt + ")");
					log.info("Creating path layer.. " + changedBlocks + " modified (" + createdPlotCnt + ")");
				}
		
				
				// Clear topmost layer of path bricks to expose buildable area
				minMarker = new Location(currentWorld,
						plotMinX, 20, plotMinZ);
				maxMarker = new Location(currentWorld,
						(plotMaxX - pathSize), 20, (plotMaxZ - pathSize));		
				changedBlocks = Deity.edit.setAreaWithBlock(
						currentWorld.getName(), minMarker, maxMarker, "0");
				if (changedBlocks > 0) {
					// Deity.chat.sendPlayerMessage(player, "Exposing buildable area.. " + changedBlocks + " modified (" + createdPlotCnt + ")");
					log.info("Exposing buildable area.. " + changedBlocks + " modified (" + createdPlotCnt + ")");
				}
				
				// Create region
				setupRegion(plotMinX, plotMinZ, (plotMaxX - pathSize), (plotMaxZ - pathSize), plotSize, currentWorld, sender);
			}
		}

		Deity.chat.sendPlayerMessage(player, "&aDone creating plots " + createdPlotCnt + " plots!");
		
		
		return true;
	}
    
	// Creates region
    private static void setupRegion(double plotMinX, double plotMinZ, double plotMaxX,
			double plotMaxZ, int plotSize, World currentWorld, CommandSender sender) {
		
    	String sql = "SELECT MAX(id) FROM "
            + Deity.data.getDB().tableName("deity_creative_", "plots") + " WHERE id > 2000";
		DatabaseResults query = Deity.data.getDB().Read2(sql);
		
        if (query != null) {
            try {
                    int id = 3000;
                    if (query.getInteger(0, "MAX(id)") != null
                                    && query.getInteger(0, "MAX(id)") != 0) {
                            id = query.getInteger(0, "MAX(id)") + 1;
                    }
                    sql = "INSERT INTO "
                                    + Deity.data.getDB().tableName("deity_creative_",
                                                    "plots")
                                    + " (`id`, `world`, `min_x`, `min_z`, `max_x`, `max_z`, plot_size) "
                                    + " VALUES (?, ?, ?, ?, ?, ?, ?);";

                    Deity.data.getDB().Write(sql, id, currentWorld.getName(),
                                    (int) plotMinX, (int) plotMinZ,
                                    (int) plotMaxX, (int) plotMaxZ, plotSize);
                    Deity.sec.protectRegion(currentWorld, "creative_" + id,
                    		new Location(currentWorld, plotMinX, 1, plotMinZ),
                    		new Location(currentWorld, plotMaxX, 127, plotMaxZ));
                    Deity.sec.setGreetingFlag("creative_" + id, sender,
    						"Available Plot [" + id + "]");
            } catch (SQLDataException e1) {
                    e1.printStackTrace();
            } catch (Exception e) {
                    e.printStackTrace();
            }
        }
	}


	public static void newPlot(World world, Location pointMin, Location pointMax) {
            if (pointMin.getX() > pointMax.getX()) {
                    double tmpMinX = pointMin.getX();
                    double tmpMaxX = pointMax.getX();
                    pointMin.setX(tmpMaxX);
                    pointMax.setX(tmpMinX);
            }
            if (pointMin.getZ() > pointMax.getZ()) {
                    double tmpMinZ = pointMin.getZ();
                    double tmpMaxZ = pointMax.getZ();
                    pointMin.setZ(tmpMaxZ);
                    pointMax.setZ(tmpMinZ);
            }

            String sql = "SELECT MAX(id) FROM "
                            + Deity.data.getDB().tableName("deity_creative_", "plots") + " WHERE id > 2000";
            DatabaseResults query = Deity.data.getDB().Read2(sql);

            if (query != null) {
                    try {
                            int id = 3000;
                            if (query.getInteger(0, "MAX(id)") != null
                                            && query.getInteger(0, "MAX(id)") != 0) {
                                    id = query.getInteger(0, "MAX(id)") + 1;
                            }
                            sql = "INSERT INTO "
                                            + Deity.data.getDB().tableName("deity_creative_",
                                                            "plots")
                                            + " (`id`, `world`, `min_x`, `min_z`, `max_x`, `max_z`) "
                                            + " VALUES (?, ?,?,?,?,?);";

                            Deity.data.getDB().Write(sql, id, world.getName(),
                                            (int) pointMin.getX(), (int) pointMin.getZ(),
                                            (int) pointMax.getX(), (int) pointMax.getZ());
                            Deity.sec.protectRegion(world, "creative_" + id, pointMin,
                                            pointMax);
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
                    this.world = Deity.server.getServer().getWorld(
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
            String sql = "UPDATE "
                            + Deity.data.getDB().tableName("deity_creative_", "plots")
                            + " SET `playername` = ? WHERE `id` = ?;";

            Deity.data.getDB().Write(sql, playername, this.getId());
            */
            Deity.sec.addMemberToRegion(playername, this.getWorld(), "creative_"
                            + this.getId());
    }

    public void removePlayer(String playername) throws IOException {
            this.playername = "";
            String sql = "UPDATE "
                            + Deity.data.getDB().tableName("deity_creative_", "plots")
                            + " SET `playername` = '' WHERE `id` = ?;";
            Deity.data.getDB().Write(sql, this.getId());
            Deity.sec.removeMemberFromRegion(playername, this.getWorld(),
                            "creative_" + this.getId());
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
            String sql = "UPDATE "
                            + Deity.data.getDB().tableName("deity_creative_", "plots")
                            + " SET `playername` = ?, `world` = ?, `min_x` = ?, `min_z` = ?, "
                            + "`max_x` = ?, `max_z` = ?, `plot_size` = ?, `is_claimed` = ? WHERE `id` = ?;";

            Deity.data.getDB().Write(sql, this.playername, this.world.getName(),
                            (int) this.minPoint.getX(), (int) this.minPoint.getZ(),
                            (int) this.maxPoint.getX(), (int) this.maxPoint.getZ(), (int) this.plotSize,
                            (this.isClaimed ? 1 : 0), this.id);
    }

    public void resetLand(Player player) {
    	
    	// Bugged worldguard chunk regen so not doing it this way anymore:
    	// Deity.edit.regenRegion(getWorld(), "creative_" + id); 
    	
    	World currentWorld = getWorld();
    	
    	// Set air to clear
    	Deity.edit.setAreaWithBlock(currentWorld.getName(), minPoint, maxPoint, "0");
    	
    	// Set dirt
		Location minMarker = new Location(currentWorld,
				getMinPoint().getX(), 1, getMinPoint().getZ());
		Location maxMarker = new Location(currentWorld,
				getMaxPoint().getX(), 18, getMaxPoint().getZ());				
		int changedBlocks = Deity.edit.setAreaWithBlock(
				currentWorld.getName(), minMarker, maxMarker, "3");
		if (changedBlocks > 0) {
			log.info("resetLand - Set base dirt.. " + changedBlocks + " modified by " + player.getName());
		}
		
		// Create base grass
		minMarker = new Location(currentWorld,
				getMinPoint().getX(), 19, getMinPoint().getZ());
		maxMarker =  new Location(currentWorld,
				getMaxPoint().getX(), 19, getMaxPoint().getZ());			
		changedBlocks = Deity.edit.setAreaWithBlock(
				currentWorld.getName(), minMarker, maxMarker, "2");
		if (changedBlocks > 0) {
			log.info("resetLand - Set base grass.. " + changedBlocks + " modified by " + player.getName());
		}
    }




    // public void saveLand() {
    // Deity.edit.saveSchematicFromRegion(playername + "_" + id, "creative_"
    // + id, this.getWorld().getName());
    // }
}



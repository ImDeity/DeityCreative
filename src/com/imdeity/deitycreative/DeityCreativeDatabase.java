package com.imdeity.deitycreative;

import java.sql.SQLDataException;

import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.records.Database;
import com.imdeity.deityapi.records.DatabaseResults;

public class DeityCreativeDatabase extends Database {

	public DeityCreativeDatabase() throws Exception {
		super();
		createTables();
	}
	
	/* Rank suggestions from players:
	 * 
	 * Foreman
	 * Architect
	 * Builder
	 * Crafter
	 * Constructor
	 * Blocksmith
	 * 
	 */
	// Crafter -> Constructor -> Blocksmith -> Builder -> Architect -> Foreman
	private void createTables(){
		String sql = "CREATE TABLE IF NOT EXISTS " + tableName("deity_creative_", "plots") +
		        " (`id` INT(16) NOT NULL AUTO_INCREMENT , `playername` VARCHAR(20) NOT NULL DEFAULT '' , `world` VARCHAR(32) NOT NULL ," +
		        "`min_x` INT(16) NOT NULL , `min_z` INT(16) NOT NULL ," +
		        "`max_x` INT(16) NOT NULL , `max_z` INT(16) NOT NULL ," +
		        "`is_claimed` INT(1) NOT NULL DEFAULT '0', `plot_size` INT(3), PRIMARY KEY (`id`) , KEY (`playername`)" +
		        ") ENGINE=MYISAM COMMENT='Deity Creative World Plots' AUTO_INCREMENT=1000;";
		write(sql);
		sql = "CREATE TABLE IF NOT EXISTS " + tableName("deity_creative_", "players") +
				" (`id` INT(16) NOT NULL AUTO_INCREMENT PRIMARY KEY, `playername` VARCHAR(16) NOT NULL," +
				" `rank` VARCHAR(20) NOT NULL DEFAULT '" + CreativeRank.RANK_1.getName() + "', `needs_promo` INT(1) NOT NULL DEFAULT '0'" +
				") ENGINE=MYISAM COMMENT='ImDeity Creative Player Table'";
		
	}
	
	public CreativeRank getRankOfPlayer(String name) throws SQLDataException{
		return CreativeRank.getRank(getPlayerData(name).getString(0, "playername"));
	}
	
	public boolean needsPromotion(String name) throws SQLDataException{
		return getPlayerData(name).getBoolean(0, "needs_promo");
	}
	
	public void promotePlayer(Player promoter, String name) throws SQLDataException{
		if(getRankOfPlayer(name) == CreativeRank.getMaxRank()){
			DeityAPI.getAPI().getChatAPI().sendPlayerMessage(promoter, "&cThat player is already at the highest rank");
		}else{
			if(needsPromotion(name)){
				String sql = "UPDATE " + tableName("deity_creative_", "plots") + "WHERE `playername`='" + name + "' SET ";
			}
		}
	}
	
	public DatabaseResults getPlayerData(String name){
		String sql = "SELECT * FROM " + tableName("deity_creative_", "plots") + " WHERE `playername`='" + name + "'";
		return readEnhanced(sql);
	}
	
	public void addCurrentPlayersToTable() throws SQLDataException{
		DatabaseResults players = getPlayers();
		for(int i=0; i<players.rowCount(); i++){
			String name = players.getString(i, "playername");
			if(!isInPlayerTable(name)){
				String sql = "INSERT INTO " + tableName("deity_creative_", "players") + " (`playername`) VALUES (?)";
				write(sql, name);
			}
		}
	}
	
	private boolean isInPlayerTable(String name) throws SQLDataException{
		DatabaseResults players = getPlayers();
		for(int i=0; i<players.rowCount(); i++){
			String player = players.getString(i, "playername");
			if(player.equalsIgnoreCase(name)) return true;
		}
		return false;
	}
	
	public DatabaseResults getPlayers(){
		String sql = "SELECT `playername` FROM " + tableName("deity_creative_", "players");
		return readEnhanced(sql);
	}

}

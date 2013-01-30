package com.imdeity.deitycreative;

import java.sql.SQLDataException;

import org.bukkit.entity.Player;

import com.imdeity.deityapi.DeityAPI;
import com.imdeity.deityapi.records.Database;
import com.imdeity.deityapi.records.DatabaseResults;

public class DeityCreativeDatabase extends Database {

	public String players = tableName("deity_creative_", "players");
	public String plots = tableName("deity_creative_", "plots");
	
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
		String sql = "CREATE TABLE IF NOT EXISTS " + plots +
		        " (`id` INT(16) NOT NULL AUTO_INCREMENT , `playername` VARCHAR(20) NOT NULL DEFAULT '' , `world` VARCHAR(32) NOT NULL ," +
		        "`min_x` INT(16) NOT NULL , `min_z` INT(16) NOT NULL ," +
		        "`max_x` INT(16) NOT NULL , `max_z` INT(16) NOT NULL ," +
		        "`is_claimed` INT(1) NOT NULL DEFAULT '0', `plot_size` INT(3), PRIMARY KEY (`id`) , KEY (`playername`)" +
		        ") ENGINE=MYISAM COMMENT='Deity Creative World Plots' AUTO_INCREMENT=1000;";
		write(sql);
		sql = "CREATE TABLE IF NOT EXISTS " + players +
				" (`id` INT(16) NOT NULL AUTO_INCREMENT PRIMARY KEY, `playername` VARCHAR(16) NOT NULL," +
				" `rank` VARCHAR(20) NOT NULL DEFAULT '" + CreativeRank.RANK_1.getName() + "', `needs_promo` INT(1) NOT NULL DEFAULT '0'" +
				") ENGINE=MYISAM COMMENT='ImDeity Creative Player Table'";
		
	}
	
	public CreativeRank getRankOfPlayer(String name){
		try {
			return CreativeRank.getRank(getPlayerData(name).getString(0, "playername"));
		} catch (SQLDataException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean needsPromotion(String name){
		try {
			DatabaseResults data = getPlayerData(name);
			if(data == null){
				return false;
			}else{
				return data.getBoolean(0, "needs_promo");
			}
		} catch (SQLDataException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void setNeedsPromo(String name, boolean needed){
		int promo = needed ? 1 : 0;
		String sql = "UPDATE " + players + " SET `needs_promo`='" + promo + "' WHERE `playername`='" + name + "'";
		write(sql);
	}
	
	public void promotePlayer(Player promoter, String name){
		CreativeRank rank = getRankOfPlayer(name);
		if(rank == CreativeRank.getMaxRank()){
			//player cannot be promoted any further
			DeityCreative.plugin.chat.sendPlayerMessage(promoter, "&cThat player cannot be promoted any further");
		}else{
			if(needsPromotion(name)){
				//set their rank in the database to the next rank
				String sql = "UPDATE " + plots + "SET `rank`='" + CreativeRank.nextRank(rank) + "' WHERE `playername`='" + name + "'";
				write(sql);
				//send the player mail saying they have been promoted
				DeityAPI.getAPI().getChatAPI().sendMailToPlayer("DeityCreative", name, "&bYou have been promoted! Type &3/creative claim&b to claim your new, larger plot!");
			}else{
				//player hasn't requested a promotion
				DeityCreative.plugin.chat.sendPlayerMessage(promoter, "&cThat player has not requested a promotion");
			}
		}
	}
	
	//gets the players plot for his/her current rank. Returns null if there isn't one, or an error occurs
	public Plot getCurrentPlot(String playername){
		try {
			String sql = "SELECT `id` FROM " + plots + " WHERE `playername`='" + playername + "' ORDER BY `id` DESC"; //using DESC because bigger plots will be claimed AFTER each other
			DatabaseResults query = readEnhanced(sql);
			if(query.hasRows()){
				return new Plot(query.getInteger(0, "id"));
			}else{
				return null;
			}
		} catch (SQLDataException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean canClaim(String name){
		String sql = "SELECT * FROM " + plots + " WHERE `playername`='" + name + "'";
		DatabaseResults query = readEnhanced(sql);
		//if the number of plots is less than there rank (ie player is rank 2 but has one plot claimed) then they can claim
		return query.rowCount() > getRankOfPlayer(name).getPlace();
	}
	
	public DatabaseResults getPlayerData(String name){
		String sql = "SELECT * FROM " + plots + " WHERE `playername`='" + name + "'";
		return readEnhanced(sql);
	}
	
	//adds players that already have a plot to the table, with default rank. This must be done before the ranking system takes effect
	public void addCurrentPlayersToTable() throws SQLDataException{
		DatabaseResults players = getPlayers();
		for(int i=0; i<players.rowCount(); i++){
			String name = players.getString(i, "playername");
			if(!isInPlayerTable(name)){
				String sql = "INSERT INTO " + players + " (`playername`) VALUES (?)";
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
		String sql = "SELECT `playername` FROM " + players;
		return readEnhanced(sql);
	}

}

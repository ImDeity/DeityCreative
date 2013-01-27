package com.imdeity.deitycreative;

import com.imdeity.deityapi.records.Database;

public class DeityCreativeDatabase extends Database {

	public DeityCreativeDatabase() throws Exception {
		super();
		createTables();
	}
	
	/* Rank Suggestions:
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
		sql = "CREATE TABLE IF NOT EXISTS " + tableName("deity_creative_", "players")
				+ " (`id` INT(16) NOT NULL AUTO_INCREMENT PRIMARY KEY, `playername` VARCHAR(16) NOT NULL," +
				" `rank` VARCHAR(20) NOT NULL DEFAULT 'Crafter', `needs_promo` INT(1) NOT NULL DEFAULT '0'" +
				") ENGINE=MYISAM COMMENT='ImDeity Creative Player Table'";
		
	}

}

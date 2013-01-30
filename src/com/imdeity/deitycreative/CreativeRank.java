package com.imdeity.deitycreative;

public enum CreativeRank {

	/* to change the name of a rank at any time:
	 * 
	 * 1) change enum
	 * 2) UPDATE `deity_creative_players` SET `rank`='{new-rank}' WHERE `rank`='{old-rank}'
	 */
	
	//gray, white, greens, blues
	RANK_1("Crafter", '7', 32),
	RANK_2("Contructor", 'f', 48),
	RANK_3("Blocksmith", 'a', 64),
	RANK_4("Builder", '2', 128),
	RANK_5("Architect", 'b', 256),
	RANK_6("Foreman", '1', 512);
	
	private String name, color;
	private int plotSize;
	
	private CreativeRank(String name, char color, int plotSize){
		this.name = name;
		this.color = "&" + color;
		this.plotSize = plotSize;
	}
	
	public String getName(){
		return name;
	}
	
	public int getPlotSize(){
		return plotSize;
	}
	
	public String getColorfulName(){
		return color + name;
	}
	
	public static CreativeRank getRank(String rank){
		for(CreativeRank r : CreativeRank.values()){
			if(r.getName().equalsIgnoreCase(rank))
				return r;
		}
		return null;
	}
	
	public static CreativeRank getRank(int place){
		for(CreativeRank r : CreativeRank.values())
			if(r.getPlace() == place) return r;
		return null;
	}
	
	public static CreativeRank getMaxRank(){
		return RANK_6;
	}
	
	public static CreativeRank nextRank(CreativeRank rank){
		if(rank == RANK_1)
			return RANK_2;
		else if(rank == RANK_2)
			return RANK_3;
		else if(rank == RANK_3)
			return RANK_4;
		else if(rank == RANK_4)
			return RANK_5;
		else return RANK_6;
		
	}
	
	public int getPlace(){
		int counter = 0;
		for(CreativeRank r : CreativeRank.values()){
			counter++;
			if(this == r){
				break;
			}
		}
		return counter++;
	}
	
	public String toString(){ return getName(); }
	
}

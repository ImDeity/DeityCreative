package com.imdeity.deitycreative;

public enum CreativeRank {

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
	
	public static CreativeRank getMaxRank(){
		return RANK_6;
	}
	
	public static CreativeRank nextRank(CreativeRank rank){
		boolean isNext = false;
		for(CreativeRank r : CreativeRank.values()){
			if(isNext) return r;
			else if(rank == r) isNext = true;
		}
		return null;
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

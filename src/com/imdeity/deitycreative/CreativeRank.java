package com.imdeity.deitycreative;

public enum CreativeRank {

	//gray, white, greens, blues
	RANK_1("Crafter", '7'),
	RANK_2("Contructor", 'f'),
	RANK_3("Blocksmith", 'a'),
	RANK_4("Builder", '2'),
	RANK_5("Architect", 'b'),
	RANK_6("Foreman", '1');
	
	private String name, color;
	
	private CreativeRank(String name, char color){
		this.name = name;
		this.color = "&" + color;
	}
	
	public String getName(){
		return name;
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
	
	public String toString(){ return getName(); }
	
}

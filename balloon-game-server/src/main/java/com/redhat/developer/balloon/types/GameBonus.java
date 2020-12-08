package com.redhat.developer.balloon.types;

public class GameBonus {

	public String playerId;
  public String playerName;

  public String achievement;
  public Integer bonus;
  public String description;

	public GameBonus() {}

	public GameBonus(String playerId, String playerName, String achievement,
			Integer bonus, String description) {
		super();
		this.playerId = playerId;
		this.playerName = playerName;
		this.achievement = achievement;
		this.bonus = bonus;
		this.description = description;
	}

	@Override
	public String toString() {
		return "GameBonus [playerId=" + playerId + ", playerName=" + playerName
				+ ", achievement=" + achievement + ", bonus=" + bonus
				+ ", description=" + description + "]";
	}

}

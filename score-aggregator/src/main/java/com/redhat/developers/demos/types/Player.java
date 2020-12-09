package com.redhat.developers.demos.types;

public class Player {

  public int total;
  public int team;
  public int yellow;
  public int red;
  public int green;
  public int blue;
  public int goldenSnitch1;
  public int goldenSnitch2;
  public String gameId;
  public String playerId;
  public String username;
  public String locationKey;

  public static Player newPlayer() {
    return new Player();
  }

  public Player gameId(String gameId) {
    this.gameId = gameId;
    return this;
  }

  public Player playerId(String playerId) {
    this.playerId = playerId;
    return this;
  }

  public Player locationKey(String locationKey) {
    this.locationKey = locationKey;
    return this;
  }

  public Player username(String username) {
    this.username = username;
    return this;
  }

  public Player team(int team) {
    this.team = team;
    return this;
  }

  public Player red(int red) {
    this.red = red;
    return this;
  }

  public Player green(int green) {
    this.green = green;
    return this;
  }

  public Player blue(int blue) {
    this.blue = blue;
    return this;
  }

  public Player yellow(int yellow) {
    this.yellow = yellow;
    return this;
  }

  public Player total(int total) {
    this.total = total;
    return this;
  }

  public Player goldenSnitch1(int goldenSnitch1) {
    this.goldenSnitch1 = goldenSnitch1;
    return this;
  }

  public Player goldenSnitch2(int goldenSnitch2) {
    this.goldenSnitch2 = goldenSnitch2;
    return this;
  }
}

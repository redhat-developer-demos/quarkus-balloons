package com.redhat.developer.balloon.types;

import java.util.UUID;

public class Configuration {

  public String gameId;
  public String background;
  public int trafficPercentage;
  public String scale;
  public int opacity;
  public int speed;
  public boolean goldenSnitch1;
  public boolean goldenSnitch2;
  public Points points;

  public Configuration() {
    this(UUID.randomUUID()
             .toString(), "default",
      100, "0.6", 85, 70, false,
      false, new Points());
  }

  public Configuration(String gameId, String background, int trafficPercentage,
    String scale, int opacity, int speed, boolean goldenSnitch1,
    boolean goldenSnitch2, Points points) {
    this.gameId = gameId;
    this.background = background;
    this.trafficPercentage = trafficPercentage;
    this.scale = scale;
    this.opacity = opacity;
    this.speed = speed;
    this.goldenSnitch1 = goldenSnitch1;
    this.goldenSnitch2 = goldenSnitch2;
    this.points = points;
  }

}

/*
 *
 * { "gameId": "d6d5dc35-c4cd-4299-a171-9092bb4ab645", "background": "canary", "trafficPercentage":
 * 100, "scale": ".9", "opacity": 85, "speed": 35, "goldenSnitch1": true, "goldenSnitch2": true,
 * "points": { "red": 1, "yellow": 1, "green": 1, "blue": 1, "goldenSnitch1": 100, "goldenSnitch2":
 * 100 } } - gameId: - background: can be default, blue, green, canary - trafficPercentage: is
 * ignored - scale: is the size of the balloon - opacity: is the opacity of the balloon - speed:
 * higher numbers (e.g. 95) might throw the balloons off the top of the screen, lower numbers (e.g.
 * 20) mean the balloons barely make it halfway up the screen - goldenSnitch1: the Burr balloon in
 * play (based on balloon-game-mobile/src/app/+game/assets/balloons_v2.png) - goldenSnitch2: the Ray
 * balloon in play - points per pop - the client maintains its own state of pops, achievements,
 * calculated by the server may augment the client's score
 */

package com.redhat.developers.demos.types;

public class Points {

  public int red;
  public int yellow;
  public int green;
  public int blue;
  public int goldenSnitch1;
  public int goldenSnitch2;

  public Points() {
    this(
      1, 1, 1, 1, 50, 50);
  }

  public Points(
    int red,
    int yellow,
    int green,
    int blue,
    int goldenSnitch1,
    int goldenSnitch2) {
    this.red = red;
    this.yellow = yellow;
    this.green = green;
    this.blue = blue;
    this.goldenSnitch1 = goldenSnitch1;
    this.goldenSnitch2 = goldenSnitch2;

  }

}

/*
 * "points": { "red": 1, "yellow": 1, "green": 1, "blue": 1, "goldenSnitch1": 100, "goldenSnitch2":
 * 100 }
 */

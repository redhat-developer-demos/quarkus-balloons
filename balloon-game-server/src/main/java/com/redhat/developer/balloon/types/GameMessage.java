package com.redhat.developer.balloon.types;

import java.util.Objects;

public class GameMessage {

  public Game game;
  public Player player;
  public String type;

  public static GameMessage activeGameMsg(Configuration configuration) {
    GameMessage gameMessage = new GameMessage();
    Game game = new Game();
    game.state = GameState.lobby;
    game.configuration = Objects.requireNonNullElseGet(configuration,
      Configuration::new);
    gameMessage.game = game;
    return gameMessage;
  }

  public static GameMessage playGameMsg(Configuration configuration) {
    GameMessage gameMessage = new GameMessage();
    gameMessage.type = "game";
    Game game = new Game();
    game.state = GameState.play;
    game.configuration = Objects.requireNonNullElseGet(configuration,
      Configuration::new);
    gameMessage.game = game;
    return gameMessage;
  }

  public static GameMessage pauseGameMsg(Configuration configuration) {
    GameMessage gameMessage = new GameMessage();
    gameMessage.type = "game";
    Game game = new Game();
    game.state = GameState.pause;
    game.configuration = Objects.requireNonNullElseGet(configuration,
      Configuration::new);
    gameMessage.game = game;
    return gameMessage;
  }

  public static GameMessage gameStoppedMsg(Configuration configuration) {
    GameMessage gameMessage = new GameMessage();
    gameMessage.type = "game";
    Game game = new Game();
    game.state = GameState.stop;
    game.configuration = Objects.requireNonNullElseGet(configuration,
      Configuration::new);
    gameMessage.game = game;
    return gameMessage;
  }

  public static GameMessage bonusMsg(Configuration configuration) {
    GameMessage gameMessage = new GameMessage();
    gameMessage.type = "game";
    Game game = new Game();
    game.state = GameState.bonus;
    game.configuration = Objects.requireNonNullElseGet(configuration,
      Configuration::new);
    gameMessage.game = game;
    return gameMessage;
  }

  public static GameMessage lobbyGameMsg(Configuration configuration) {
    GameMessage gameMessage = new GameMessage();
    gameMessage.type = "game";
    Game game = new Game();
    game.state = GameState.lobby;
    game.configuration = Objects.requireNonNullElseGet(configuration,
      Configuration::new);
    gameMessage.game = game;
    return gameMessage;
  }
}

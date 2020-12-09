package com.redhat.developers.demos.types;

import java.util.Objects;

public class GameMessage {

  public Game game;
  public Player player;
  public String type;

  public GameMessage() {
  }

  public GameMessage(Game game, Player player, String type) {
    this.game = Objects.requireNonNullElseGet(game, Game::new);
    this.player = player;
    this.type = Objects.requireNonNullElse(type, "game");
  }

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

package com.redhat.developer.balloon.types;

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

  public static GameMessage activeGameMsg(String gameId,
    Configuration configuration) {
    GameMessage gameMessage = new GameMessage();
    Game game = new Game();
    game.gameId = gameId;
    game.state = GameState.lobby;
    game.configuration = Objects.requireNonNullElseGet(configuration,
      Configuration::new);
    gameMessage.game = game;
    return gameMessage;
  }

  public static GameMessage playGameMsg(String gameId,Configuration configuration) {
    GameMessage gameMessage = new GameMessage();
    gameMessage.type = "game";
    Game game = new Game();
    game.gameId = gameId;
    game.state = GameState.play;
    game.configuration = Objects.requireNonNullElseGet(configuration,
      Configuration::new);
    gameMessage.game = game;
    return gameMessage;
  }

  public static GameMessage pauseGameMsg(String gameId,Configuration configuration) {
    GameMessage gameMessage = new GameMessage();
    gameMessage.type = "game";
    Game game = new Game();
    game.gameId = gameId;
    game.state = GameState.pause;
    game.configuration = Objects.requireNonNullElseGet(configuration,
      Configuration::new);
    gameMessage.game = game;
    return gameMessage;
  }

  public static GameMessage gameStoppedMsg(String gameId,Configuration configuration) {
    GameMessage gameMessage = new GameMessage();
    gameMessage.type = "game";
    Game game = new Game();
    game.gameId = gameId;
    game.state = GameState.stop;
    game.configuration = Objects.requireNonNullElseGet(configuration,
      Configuration::new);
    gameMessage.game = game;
    return gameMessage;
  }

  public static GameMessage bonusMsg(String gameId,Configuration configuration) {
    GameMessage gameMessage = new GameMessage();
    gameMessage.type = "game";
    Game game = new Game();
    game.gameId = gameId;
    game.state = GameState.bonus;
    game.configuration = Objects.requireNonNullElseGet(configuration,
      Configuration::new);
    gameMessage.game = game;
    return gameMessage;
  }

  public static GameMessage lobbyGameMsg(String gameId,Configuration configuration) {
    GameMessage gameMessage = new GameMessage();
    gameMessage.type = "game";
    Game game = new Game();
    game.gameId = gameId;
    game.state = GameState.lobby;
    game.configuration = Objects.requireNonNullElseGet(configuration,
      Configuration::new);
    gameMessage.game = game;
    return gameMessage;
  }
}

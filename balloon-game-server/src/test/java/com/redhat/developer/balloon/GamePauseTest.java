package com.redhat.developer.balloon;

import static io.restassured.RestAssured.given;

import com.redhat.developer.balloon.types.Configuration;
import com.redhat.developer.balloon.types.Game;
import com.redhat.developer.balloon.types.GameMessage;
import com.redhat.developer.balloon.types.Player;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import java.net.URI;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.websocket.ClientEndpoint;
import javax.websocket.ContainerProvider;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class GamePauseTest {

  private static final LinkedBlockingDeque<String> MESSAGES = new LinkedBlockingDeque<>();

  @TestHTTPResource("/game")
  URI uri;

  @Test
  public void testPauseMessage() throws Exception {
    try (Session session = ContainerProvider.getWebSocketContainer()
                                            .connectToServer(Client.class,
                                              uri)) {

      // need a live session to exchange messages
      session.getAsyncRemote()
             .sendObject("{\"type\":\"register\",\"message\":\"register\"}");

      // play the game
      given()
        .when()
        .get("/api/game/pause")
        .then()
        .statusCode(204);

      Jsonb jsonb = JsonbBuilder.create();
      String msg = MESSAGES.pollLast(10, TimeUnit.SECONDS);
      GameMessage gameMessage = jsonb.fromJson(msg, GameMessage.class);
      Player player = gameMessage.player;
      Assertions.assertNull(player);
      Game game = gameMessage.game;
      Assertions.assertNotNull(game);
      Configuration config = game.configuration;
      Assertions.assertNotNull(config);
      Assertions.assertEquals("game", gameMessage.type);
      Assertions.assertEquals("pause", game.state.name());
    }
  }


  @ClientEndpoint
  public static class Client {

    @OnOpen
    public void open(Session session) {
    }

    @OnMessage
    void message(String msg) throws Exception {
      MESSAGES.add(msg);
    }

  }
}
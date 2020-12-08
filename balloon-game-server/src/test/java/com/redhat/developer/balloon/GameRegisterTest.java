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
public class GameRegisterTest {

  private static final LinkedBlockingDeque<String> MESSAGES = new LinkedBlockingDeque<>();

  @TestHTTPResource("/game")
  URI uri;

  @Test
  public void testHelloEndpoint() {
    given()
      .when()
      .get("/api/game/lobby")
      .then()
      .statusCode(204);
  }

  @Test
  public void testWebsocketChat() throws Exception {
    try (Session session = ContainerProvider.getWebSocketContainer()
                                            .connectToServer(Client.class,
                                              uri)) {
      Assertions.assertEquals("REGISTER", MESSAGES.poll(10, TimeUnit.SECONDS));

      String msg = MESSAGES.poll(10, TimeUnit.SECONDS);
      Jsonb jsonb = JsonbBuilder.create();
      GameMessage gameMessage = jsonb.fromJson(msg, GameMessage.class);
      Assertions.assertNotNull(gameMessage);
      Player player = gameMessage.player;
      Assertions.assertNotNull(player);
      Game game = gameMessage.game;
      Assertions.assertNotNull(game);
      Configuration config = game.configuration;
      Assertions.assertNotNull(config);
      Assertions.assertEquals("register", gameMessage.type);
      Assertions.assertEquals("lobby", game.state.name());
    }
  }


  @ClientEndpoint
  public static class Client {

    @OnOpen
    public void open(Session session) {
      MESSAGES.add("REGISTER");
      session.getAsyncRemote()
             .sendObject("{\"type\":\"register\",\"message\":\"register\"}");
    }

    @OnMessage
    void message(String msg) {
      MESSAGES.add(msg);
    }

  }
}
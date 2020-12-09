package com.redhat.developer.balloon.endpoints;

import com.redhat.developer.balloon.services.GameService;
import com.redhat.developer.balloon.types.GameTransaction;
import io.smallrye.reactive.messaging.kafka.KafkaRecord;
import io.smallrye.reactive.messaging.kafka.Record;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

@ServerEndpoint("/game") // for the mobile game
@ApplicationScoped
public class GameWebSocket {

  private static final Logger LOG =
    Logger.getLogger(GameWebSocket.class.getName());

  @Inject
  GameService gameService;

  @Inject
  @Channel("balloon-pops")
  Emitter<Record<String, String>> scoreEmitter;

  public GameWebSocket(GameService gameService) {
    this.gameService = gameService;
  }

  @ConfigProperty(name = "ballon.game.kafkaforpops", defaultValue = "false")
  boolean kafkaSend;

  Jsonb jsonb;

  @PostConstruct
  public void init() {
    this.jsonb = JsonbBuilder.create();
  }

  @OnOpen
  public void onOpen(Session session) {
    LOG.info("onOpen");
    LOG.info("id: " + session.getId());
    gameService.addSession(session.getId(), session);
  }

  @OnClose
  public void onClose(Session session) {
    LOG.info("onClose");
    gameService.removeSession(session.getId());
  }

  @OnError
  public void onError(Session session, Throwable throwable) {
    LOG.log(Level.SEVERE, "onError", throwable);
  }

  @OnMessage // from client to server
  public void onMessage(String message, Session session) {
    if (message == null) {
      return;
    }

    //Jsonb jsonb = JsonbBuilder.create();

    //TODO make it use Json Marshall/UnMarshall
    JsonReader jsonReader = Json.createReader(new StringReader(message));
    JsonObject jsonMessage = jsonReader.readObject();

    // LOG.info("jsonMessage: " + jsonMessage);
    String messageType = jsonMessage.getString("type");
    // LOG.info("messageType: " + messageType);

    if (messageType != null && !messageType.equals("")) {
      if (messageType.equals("register")) {
        try {
          gameService.registerClient(jsonMessage, session);
        } catch (Exception e) {
          LOG.log(Level.SEVERE, "Error registering client", e);
        }
      } else if (messageType.equals("score")) {
        var strMessage = jsonMessage.toString();

        GameTransaction gameTx = jsonb.fromJson(strMessage,
          GameTransaction.class);
        var key = gameTx.player.playerId;
        LOG.log(Level.INFO, "POP:\nKey:{0} \nValue:\n {1}",
          new Object[]{key, strMessage});
        scoreEmitter.send(Record.of(key, strMessage));
      }
    }
  }// OnMessage

} 


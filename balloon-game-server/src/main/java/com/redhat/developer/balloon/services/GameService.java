package com.redhat.developer.balloon.services;

import com.redhat.developer.balloon.types.Configuration;
import com.redhat.developer.balloon.types.GameMessage;
import com.redhat.developer.balloon.types.GameState;
import com.redhat.developer.balloon.types.Points;
import com.redhat.developer.balloon.types.Player;
import com.redhat.developer.balloon.utils.UserNameGenerator;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.websocket.Session;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class GameService {

  private static final Logger LOG =
    Logger.getLogger(GameService.class.getName());

  @ConfigProperty(name = "LOCATION_KEY")
  String locationKey;

  public String currentGameState = GameState.lobby.name();

  // @Inject
  // @Remote("playerSessions")
  // RemoteCache<String, Session> sessions;

  // websocket client sessions/connections
  Map<String, Session> sessions = new ConcurrentHashMap<>();

  // playerMap
  Map<String, Session> playerSessions = new ConcurrentHashMap<>();

  // this value should be reset at "startgame" but hardcoding for now
  public String currentGameId = UUID.randomUUID()
                                    .toString();

  public String prevPolledResponse = "";
  public int pollCnt = 0;

  Points defaultPoints = new Points(
    1, 1, 1, 1, 50, 50);

  public Configuration currentGame = new Configuration();

  Points pointsC = new Points(1, 1, 1, 1, 50, 50);

  Configuration defaultGameConfiguration = new Configuration(currentGameId,
    "default", 100, "0.9", 85, 50, false, false, pointsC);


  // send a message to a single client
  public void sendOnePlayer(String id, String message) {
    LOG.info("Sending player for session :" + id);
    Session oneSession = playerSessions.get(id);
    oneSession.getAsyncRemote()
              .sendObject(message, result -> {
                if (result.getException() != null) {
                  LOG.severe(
                    "Unable to send message: " + result.getException());
                }
              });
  } // sendOnePlayer

  /**
   * TODO add retry; FaultTolerance
   */
  public void broadcast(Object objMessage) {
    Jsonb jsonb = JsonbBuilder.create();
    var message = jsonb.toJson(objMessage);
    LOG.log(Level.INFO, "Sending Message: \n {0}", message);
    for (String sessionKey : sessions.keySet()) {
      Session session = sessions.get(sessionKey);
      session.getAsyncRemote()
             .sendObject(message, result -> {
               if (result.getException() != null) {

                 LOG.log(Level.SEVERE,
                   "Unable to send message: "
                     + result.getException());
               }
             });
    }
  }

  /*
   * Mobile/Client/Game requests - called at initial page load, refresh of browser
   */
  public void registerClient(JsonObject jsonMessage, Session session) {

    // right now, always create a new playerId during registration
    String playerId = UUID.randomUUID()
                          .toString();
    // and assigns a new generated user name
    String username = UserNameGenerator.generate();
    // and assigns a random team number
    int teamNumber = ThreadLocalRandom.current()
                                      .nextInt(1, 5);
    LOG.info("\nLOCATION_KEY: " + locationKey);
    LOG.info("\nCreating:");
    LOG.info("username: " + username);
    LOG.info("playerId: " + playerId);
    LOG.info("teamNumber: " + teamNumber);

    playerSessions.putIfAbsent(playerId, session);

    /*
     * client needs 3 messages initially: id, configuration, game state
     */

    Jsonb jsonb = JsonbBuilder.create();

    /*
     * Send Game Config TODO: This needs to pick up the current, potentially overridden game config from
     * the polled configservice for now, just hacking around it with the resetting of the variable
     */
    prevPolledResponse = "";

    Player playerResp = new Player();
    playerResp.score = 0;
    playerResp.team = teamNumber;
    playerResp.playerId = playerId;
    playerResp.username = username;
    playerResp.locationKey = locationKey;

    GameMessage gameMessage = GameMessage.lobbyGameMsg(null);
    gameMessage.type = "register";
    gameMessage.player = playerResp;

    String strGameMessage = jsonb.toJson(gameMessage);

    sendOnePlayer(playerId, strGameMessage);

    LOG.info("\n GAME-MESSAGE: " + strGameMessage + "\n");

  } // registerClient

  /*
   * With each balloon pop, send it to Kafka for async analysis
   */
  public String score(JsonObject jsonMessage) {
    LOG.info("POP: " + jsonMessage.toString());
    return jsonMessage.toString();
  }

  public GameMessage play() {
    LOG.info("Play the Game");
    currentGameState = GameState.play.name();
    return GameMessage.playGameMsg(null);
  }

  public GameMessage pause() {
    LOG.info("Pause the Game");
    currentGameState = GameState.pause.name();
    return GameMessage.pauseGameMsg(null);
  }

  public GameMessage lobby() {
    LOG.info("Lobby the Game");
    currentGameState = GameState.lobby.name();
    return GameMessage.lobbyGameMsg(null);
  }

  public GameMessage stop() {
    LOG.info("Game Over");
    currentGameState = GameState.stop.name();
    return GameMessage.gameStoppedMsg(null);
  }

  public Collection<Session> getSessions() {
    return this.sessions.values();
  }

  public void addSession(String key, Session value) {
    this.sessions.putIfAbsent(key, value);
  }

  public void removeSession(String key) {
    this.sessions.remove(key);
  }

  public Collection<Session> getPlayerSession() {
    return this.playerSessions.values();
  }
}

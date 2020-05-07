package com.redhat.developer.balloon.services;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.websocket.Session;
import com.redhat.developer.balloon.types.Config;
import com.redhat.developer.balloon.types.Points;
import com.redhat.developer.balloon.types.RegistrationResponse;
import com.redhat.developer.balloon.utils.UserNameGenerator;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class GameService {

	private static final Logger LOG =
			Logger.getLogger(GameService.class.getName());

	@ConfigProperty(name = "LOCATION_KEY")
	String locationKey;

	// @Inject
	// @Remote("playerSessions")
	// RemoteCache<String, Session> sessions;

	// websocket client sessions/connections
	Map<String, Session> sessions = new ConcurrentHashMap<>();

	// playerMap
	Map<String, Session> playerSessions = new ConcurrentHashMap<>();

	// TODO: move to game configuration service

	private static final String STARTGAME = "start-game";
	private static final String PLAY = "play";
	private static final String PAUSE = "pause";
	private static final String GAMEOVER = "game-over";

	// this value should be reset at "startgame" but hardcoding for now
	public String currentGameId = "ab7e90e1-18a6-94d1-05bb-1e17a9cc8dad";

	public String prevPolledResponse = "";
	public int pollCnt = 0;

	Points defaultPoints = new Points(
			1, 1, 1, 1, 50, 50);

	public Config currentGame = new Config(
			currentGameId,
			"default", // background
			100, // ignore
			"0.6", // size
			85, // opacity
			70, // speed
			false, // snitch1
			false, // snitch2
			defaultPoints);

	JsonObject pointsConfiguration = Json.createObjectBuilder()
			.add("red", 1)
			.add("yellow", 1)
			.add("green", 1)
			.add("blue", 1)
			.add("goldenSnitch1", 50)
			.add("goldenSnitch2", 50)
			.build();


	JsonObject defaultGameConfiguration = Json.createObjectBuilder()
			.add("gameId", currentGameId)
			.add("background", "default")
			.add("trafficPercentage", 100)
			.add("scale", "0.9")
			.add("opacity", 85)
			.add("speed", 50)
			.add("goldenSnitch1", Boolean.FALSE)
			.add("goldenSnitch2", Boolean.FALSE)
			.add("points", pointsConfiguration).build();

	// current can be overwritten at runtime
	JsonObject currentGameConfiguration = defaultGameConfiguration;

	JsonObject startGameMsg = Json.createObjectBuilder()
			.add("type", "state")
			.add("state", STARTGAME).build();

	JsonObject playGameMsg = Json.createObjectBuilder()
			.add("type", "state")
			.add("state", PLAY).build();

	JsonObject pauseGameMsg = Json.createObjectBuilder()
			.add("type", "state")
			.add("state", PAUSE).build();

	JsonObject gameOverMsg = Json.createObjectBuilder()
			.add("type", "state")
			.add("state", GAMEOVER).build();

	public String currentGameState = STARTGAME;

	// END TODO: move to game configuration service

	// send a message to a single client
	public void sendOnePlayer(String id, String message) {
		LOG.info("Sending player for session :" + id);
		Session oneSession = playerSessions.get(id);
		oneSession.getAsyncRemote().sendObject(message, result -> {
			if (result.getException() != null) {
				LOG.severe("Unable to send message: " + result.getException());
			}
		});
	} // sendOnePlayer

	/**
	 * 
	 */
	public void broadcast(String message) {
		sessions.keySet().forEach(sessionKey -> {
			sessions.get(sessionKey).getAsyncRemote().sendObject(message,
					result -> {
						if (result.getException() != null) {
							LOG.log(Level.SEVERE,
									"Unable to send message: "
											+ result.getException());
							LOG.log(Level.SEVERE, "Retrying");
							sessions.get(sessionKey).getAsyncRemote()
									.sendObject(message,
											result2 -> {
												if (result2
														.getException() != null) {
													LOG.log(Level.SEVERE,
															"2nd failed send, removing: "
																	+ result2
																			.getException());
													sessions.remove(sessionKey);
												}
											});
							// if unable to send, remove it

						}
					});
		});

		// sessions.values().forEach(session -> {
		// session.getAsyncRemote().sendObject(message, result -> {
		// if (result.getException() != null) {

		// LOG.error("Unable to send message: " + result.getException());
		// }
		// });
		// });
	} // broadcast

	/*
	 * Mobile/Client/Game requests - called at initial page load, refresh of browser
	 */
	public void registerClient(JsonObject jsonMessage, Session session) {

		// right now, always create a new playerId during registration
		String playerId = UUID.randomUUID().toString();
		// and assigns a new generated user name
		String username = UserNameGenerator.generate();
		// and assigns a random team number
		int teamNumber = ThreadLocalRandom.current().nextInt(1, 5);
		LOG.info("\nLOCATION_KEY: " + locationKey);
		LOG.info("\nCreating:");
		LOG.info("username: " + username);
		LOG.info("playerId: " + playerId);
		LOG.info("teamNumber: " + teamNumber);

		playerSessions.putIfAbsent(playerId, session);

		/*
		 * client needs 3 messages initially: id, configuration, game state
		 */

		// Send Player's ID
		JsonObject idResponse = Json.createObjectBuilder()
				.add("type", "id")
				.add("id", playerId).build();

		LOG.info("idResponse: " + idResponse.toString());

		sendOnePlayer(playerId, idResponse.toString());


		/*
		 * Send Game Config TODO: This needs to pick up the current, potentially overridden game config from
		 * the polled configservice for now, just hacking around it with the resetting of the variable
		 */
		prevPolledResponse = "";

		RegistrationResponse configurationResponse = new RegistrationResponse(
				0, // initial score
				teamNumber,
				playerId,
				username,
				"configuration", // type
				currentGame, // the actual game config
				locationKey // AWS, AZR, GCP, etc
		);

		Jsonb jsonb = JsonbBuilder.create();
		String stringConfigurationResponse =
				jsonb.toJson(configurationResponse);

		sendOnePlayer(playerId, stringConfigurationResponse);

		LOG.info("\n GAME-STATE: " + currentGameState + "\n");

		// Send Game State

		if (currentGameState.equals(STARTGAME)) {
			sendOnePlayer(playerId, startGameMsg.toString());
		} else if (currentGameState.equals(PLAY)) {
			sendOnePlayer(playerId, playGameMsg.toString());
		} else if (currentGameState.equals(PAUSE)) {
			sendOnePlayer(playerId, pauseGameMsg.toString());
		} else if (currentGameState.equals(GAMEOVER)) {
			sendOnePlayer(playerId, gameOverMsg.toString());
		}

	} // registerClient

	/*
	 * With each balloon pop, send it to Kafka for async analysis
	 */
	public String score(JsonObject jsonMessage) {
		LOG.info("POP: " + jsonMessage.toString());
		return jsonMessage.toString();
	}

	public JsonObject start() {
		currentGameId = UUID.randomUUID().toString();
		currentGameState = STARTGAME;
		return startGameMsg;
	}

	public JsonObject play() {
		currentGameState = PLAY;
		return playGameMsg;
	}

	public JsonObject pause() {
		LOG.info("Pause the Game");
		currentGameState = PAUSE;
		return pauseGameMsg;
	}

	public JsonObject gameOver() {
		LOG.info("Game Over");
		currentGameState = GAMEOVER;
		return gameOverMsg;
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

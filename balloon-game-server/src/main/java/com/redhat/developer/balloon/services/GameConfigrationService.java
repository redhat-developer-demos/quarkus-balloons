package com.redhat.developer.balloon.services;

import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import com.redhat.developer.balloon.types.Config;
import com.redhat.developer.balloon.types.Points;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class GameConfigrationService {

	private static final Logger LOGGER =
			Logger.getLogger(GameConfigrationService.class.getName());

	@Inject
	GameService gameService;

	@Inject
	@RestClient
	ConfigurationService configurationService;

	@Scheduled(every = "2s")
	void pollConfig() {
		// LOGGER.info("every 2 sec");
		try {
			String response = configurationService.getConfig().trim();
			// response is a string of JSON
			// LOGGER.info("\n *** response");
			// LOGGER.info(response);

			// only alert clients if the config has changed since previous poll
			if (!response.equals(gameService.prevPolledResponse)) {

				LOGGER.info(
						"\n " + gameService.pollCnt++ + " NEW config :"
								+ response);
				gameService.prevPolledResponse = response;

				Config convertedConfig =
						convertResponseStringToConfig(response);
				gameService.currentGame
						.setBackground(convertedConfig.getBackground());
				// do NOT override currentGame.setGameId()
				gameService.currentGame
						.setGoldenSnitch1(convertedConfig.isGoldenSnitch1());
				gameService.currentGame
						.setGoldenSnitch2(convertedConfig.isGoldenSnitch2());
				gameService.currentGame
						.setOpacity(convertedConfig.getOpacity());
				gameService.currentGame.setPoints(convertedConfig.getPoints());
				gameService.currentGame.setScale(convertedConfig.getScale());
				gameService.currentGame.setSpeed(convertedConfig.getSpeed());

			}

		} catch (Exception ex) {
			LOGGER.log(Level.WARNING, "Ignoring: ", ex);
		}
	}

	private Config convertResponseStringToConfig(String response) {

		Config convertedConfig = new Config();

		JsonReader jsonReader = Json.createReader(new StringReader(response));
		JsonObject jsonObjectConfig = jsonReader.readObject();
		convertedConfig.setBackground(jsonObjectConfig.getString("background"));
		convertedConfig.setGameId(jsonObjectConfig.getString("gameId"));
		convertedConfig
				.setGoldenSnitch1(jsonObjectConfig.getBoolean("goldenSnitch1"));
		convertedConfig
				.setGoldenSnitch2(jsonObjectConfig.getBoolean("goldenSnitch2"));
		convertedConfig.setOpacity(jsonObjectConfig.getInt("opacity"));
		convertedConfig.setScale(jsonObjectConfig.getString("scale"));
		convertedConfig.setSpeed(jsonObjectConfig.getInt("speed"));
		convertedConfig
				.setTrafficPercentage(
						jsonObjectConfig.getInt("trafficPercentage"));

		Points convertedPoints = new Points(
				jsonObjectConfig.getJsonObject("points").getInt("red"),
				jsonObjectConfig.getJsonObject("points").getInt("yellow"),
				jsonObjectConfig.getJsonObject("points").getInt("green"),
				jsonObjectConfig.getJsonObject("points").getInt("blue"),
				jsonObjectConfig.getJsonObject("points")
						.getInt("goldenSnitch1"),
				jsonObjectConfig.getJsonObject("points")
						.getInt("goldenSnitch2"));

		convertedConfig.setPoints(convertedPoints);


		return convertedConfig;
	}


	// AWS aggressively timesout Websocket connections, so ping the users
	@Scheduled(every = "10s")
	void heartbeat() {
		gameService.broadcast("{\"type\" : \"heartbeat\"}");
	}

}

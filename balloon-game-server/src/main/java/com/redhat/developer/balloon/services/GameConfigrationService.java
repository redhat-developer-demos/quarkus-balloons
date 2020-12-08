package com.redhat.developer.balloon.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redhat.developer.balloon.types.GameMessage;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import com.redhat.developer.balloon.types.Configuration;
import com.redhat.developer.balloon.types.Points;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import io.quarkus.scheduler.Scheduled;

@ApplicationScoped
public class GameConfigrationService {

  private static final Logger LOGGER =
    Logger.getLogger(GameConfigrationService.class.getName());

  @Inject
  GameService gameService;

//  @Inject
//  @RestClient
//  ConfigurationService configurationService;

  @Scheduled(every = "24h")
  void pollConfig() {
    // LOGGER.info("every 2 sec");
    try {
      //String response = configurationService.getConfig();
      String response = "";
      // response is a string of JSON
      // LOGGER.info("\n *** response");
      // LOGGER.info(response);

      // only alert clients if the config has changed since previous poll
      if (!response.equals(gameService.prevPolledResponse)) {

        LOGGER.info(
          "\n " + gameService.pollCnt++ + " NEW config :"
            + response);
        gameService.prevPolledResponse = response;

        Configuration convertedConfig =
          convertResponseStringToConfig(response);
        gameService.currentGame.background = convertedConfig.background;
        // do NOT override currentGame.setGameId()
        gameService.currentGame.goldenSnitch1 = convertedConfig.goldenSnitch1;
        gameService.currentGame.goldenSnitch2 = convertedConfig.goldenSnitch2;
        gameService.currentGame.opacity = convertedConfig.opacity;
        gameService.currentGame.points = convertedConfig.points;
        gameService.currentGame.scale = convertedConfig.scale;
        gameService.currentGame.speed = convertedConfig.speed;

      }

    } catch (Exception ex) {
      LOGGER.log(Level.WARNING, "Ignoring: ", ex);
    }
  }

  private Configuration convertResponseStringToConfig(String response) {

    ObjectMapper objectMapper = new ObjectMapper();
    Configuration convertedConfig = new Configuration();
    try {
      convertedConfig = objectMapper.readValue(response, Configuration.class);
    } catch (JsonProcessingException e) {
      LOGGER.log(Level.SEVERE, "Error reading config response", e);
    }

    return convertedConfig;
  }

  // AWS aggressively timesout Websocket connections, so ping the users
  @Scheduled(every = "24h")
  void heartbeat() {
    GameMessage gameMessage = new GameMessage();
    gameMessage.type = "heartbeat";
    gameService.broadcast(gameMessage);
  }

}

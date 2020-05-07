package com.redhat.developer.balloon.endpoints;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.redhat.developer.balloon.services.GameService;
import com.redhat.developer.balloon.types.Config;
import com.redhat.developer.balloon.types.ConfigMsgType;
import com.redhat.developer.balloon.types.Points;

// @RolesAllowed("admin")
@Path("/a")
@ApplicationScoped
public class GameAdminResource {

  private static final Logger LOGGER =
      Logger.getLogger(GameAdminResource.class.getName());

  GameService gameService;

  public GameAdminResource(GameService gameService) {
    this.gameService = gameService;
  }

  @GET
  @Path("/start")
  public Response startGame() {
    var startGameMsg = gameService.start();
    gameService.broadcast(startGameMsg.toString());
    return Response.status(200).build();
  }

  @GET
  @Path("/play")
  public Response playGame() {
    JsonObject playGameMsg = gameService.play();
    gameService.broadcast(playGameMsg.toString());
    return Response.status(200).build();
  }

  @GET
  @Path("/pause")
  public Response pauseGame() {
    JsonObject pauseGameMsg = gameService.pause();
    gameService.broadcast(pauseGameMsg.toString());
    return Response.status(200).build();
  }

  @GET
  @Path("/gameover")
  public Response gameOver() {
    JsonObject pauseGameMsg = gameService.pause();
    gameService.broadcast(pauseGameMsg.toString());
    return Response.status(200).build();
  }

  @GET
  @Path("/gamestate")
  @Produces(MediaType.APPLICATION_JSON)
  public Response gamestate() {
    return Response.ok(gameService.currentGameState).status(200).build();
  }

  @GET
  @Path("/config")
  @Produces(MediaType.APPLICATION_JSON)
  public Response config() {
    return Response.ok(gameService.currentGame).status(200).build();
  }

  @GET
  @Path("/sessions")
  public Response sessions() {
    StringBuffer sb = new StringBuffer();
    gameService.getSessions().forEach(session -> {
      sb.append(session.getId() + "<br>");
    });
    return Response.ok(sb.toString()).status(200).build();
  }

  // TODO: can use infinispan queries
  // @GET
  // @Path("/playersessions")
  // public Response playersessions() {
  // StringBuffer sb = new StringBuffer();
  // playerSessions.keySet().forEach(playerId -> {
  // sb.append(
  // playerSessions.get(playerId).getId() + " : " + playerId + "<br>");
  // });
  // return Response.ok(sb.toString()).status(200).build();
  // }

  // @GET
  // @Path("/playercount")
  // public Response playercount() {
  // return Response.ok("Player Count: " + playerSessions.keySet().size())
  // .build();
  // }



  @GET
  @Path("/achieve")
  @Produces(MediaType.APPLICATION_JSON)
  public Response achievement() {
    // There can be multiple achievements so send them all to all players
    JsonArray achievements = Json.createArrayBuilder()
        .add(Json.createObjectBuilder()
            .add("type", "pops1")
            .add("description", "1 in a row!")
            .add("bonus", 10))
        .add(Json.createObjectBuilder()
            .add("type", "pops2")
            .add("description", "2 in a row!")
            .add("bonus", 20))
        .add(Json.createObjectBuilder()
            .add("type", "pops3")
            .add("description", "3 in a row!")
            .add("bonus", 30))
        .add(Json.createObjectBuilder()
            .add("type", "score1")
            .add("description", "Level 1")
            .add("bonus", 100))
        .add(Json.createObjectBuilder()
            .add("type", "score2")
            .add("description", "Level 2")
            .add("bonus", 200))
        .add(Json.createObjectBuilder()
            .add("type", "score3")
            .add("description", "Level 3")
            .add("bonus", 300))
        .add(Json.createObjectBuilder()
            .add("type", "golden")
            .add("description", "Solid Gold")
            .add("bonus", 1000))

        .build();


    JsonObject allAcheivements = Json.createObjectBuilder()
        .add("type", "achievements")
        .add("achievements", achievements)
        .build();

    // for testing purposes, sending all players
    gameService.broadcast(allAcheivements.toString());

    return Response.ok(allAcheivements).status(200).build();
  }

  @GET
  @Path("/easy")
  @Produces(MediaType.APPLICATION_JSON)
  public Response easyconfig() {

    Points easyPoints = new Points(
        4, 2, 3, 1, 100, 100);

    Config easyGame = new Config(
        gameService.currentGameId,
        "default", // background
        100, // ignore
        "1.0", // size
        85, // opacity
        35, // speed
        false, // snitch1
        false, // snitch2
        easyPoints);

    gameService.currentGame = easyGame;

    sendGameConfigUpdate();

    return Response.ok(gameService.currentGame).status(200).build();
  }

  @GET
  @Path("/hard")
  @RolesAllowed({"admin"})
  @Produces(MediaType.APPLICATION_JSON)
  public Response hardconfig() {
    Points hardPoints = new Points(
        4, 2, 3, 1, 100, 100);

    Config hardGame = new Config(
        gameService.currentGameId,
        "default", // background
        100, // ignore
        ".3", // size
        50, // opacity
        95, // speed
        false, // snitch1
        false, // snitch2
        hardPoints);

    gameService.currentGame = hardGame;

    sendGameConfigUpdate();

    return Response.ok(gameService.currentGame).status(200).build();
  }


  @GET
  @Path("/green")
  @Produces(MediaType.APPLICATION_JSON)
  public Response goldenSnitch1() {

    gameService.currentGame.setGoldenSnitch1(Boolean.TRUE);
    gameService.currentGame.setGoldenSnitch2(Boolean.FALSE);
    gameService.currentGame.setBackground("green");

    sendGameConfigUpdate();

    return Response.ok(gameService.currentGame).status(200).build();
  }

  @GET
  @Path("/blue")
  @Produces(MediaType.APPLICATION_JSON)
  public Response goldenSnitch2() {
    gameService.currentGame.setGoldenSnitch1(Boolean.FALSE);
    gameService.currentGame.setGoldenSnitch2(Boolean.TRUE);
    gameService.currentGame.setBackground("blue");

    sendGameConfigUpdate();

    return Response.ok(gameService.currentGame).status(200).build();
  }

  @GET
  @Path("/both")
  @Produces(MediaType.APPLICATION_JSON)
  public Response goldenSnitchBoth() {

    gameService.currentGame.setGoldenSnitch1(Boolean.TRUE);
    gameService.currentGame.setGoldenSnitch2(Boolean.TRUE);
    gameService.currentGame.setBackground("canary");

    sendGameConfigUpdate();

    return Response.ok(gameService.currentGame).status(200).build();
  }


  @GET
  @Path("/default")
  @Produces(MediaType.APPLICATION_JSON)
  public Response defaultConfig() {

    // resetting to the default configuration
    gameService.prevPolledResponse = "";

    Points defaultPoints = new Points(
        1, 1, 1, 1, 50, 50);

    Config defaultGame = new Config(
        gameService.currentGameId,
        "default", // background
        100, // ignore
        "0.6", // size
        85, // opacity
        70, // speed
        false, // snitch1
        false, // snitch2
        defaultPoints);

    gameService.currentGame = defaultGame;

    sendGameConfigUpdate();

    return Response.ok(gameService.currentGame).status(200).build();
  }

  void sendGameConfigUpdate() {
    Jsonb jsonb = JsonbBuilder.create();

    ConfigMsgType type =
        new ConfigMsgType("configuration", gameService.currentGame);
    String stringJsonMsgType = jsonb.toJson(type);

    LOGGER.log(Level.INFO, "Game Config Update {0}", stringJsonMsgType);
    // broadcast out the websocket to all players
    gameService.broadcast(stringJsonMsgType);
  }
}

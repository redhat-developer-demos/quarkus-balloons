package com.redhat.developer.balloon.streams;

import java.util.concurrent.CompletionStage;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import com.redhat.developer.balloon.types.GameBonus;
import io.smallrye.reactive.messaging.kafka.KafkaMessage;

public class BonusStream {

	private static final Logger LOG =
			Logger.getLogger(BonusStream.class.getName());

	/*
	 * Listen for bonus topic, bonuses should sent out the unique player
	 */
	// @Incoming("bonusstream")
	public CompletionStage<Void> process(KafkaMessage<String, GameBonus> msg) {
		// public void process(String message) {


		LOG.info("\n!!!BONUSSTREAM!!! " + msg.getPayload().toString());


		String achievement = msg.getPayload().getAchievement();

		if (achievement != null && !achievement.trim().equals("")) {
			String playerId = msg.getPayload().getPlayerId();
			String description = msg.getPayload().getDescription();
			int bonus = msg.getPayload().getBonus();
			LOG.info("!!! Achievement !!! " + achievement + " for: " + playerId
					+ " value: " + bonus);

			JsonArray achievements = Json.createArrayBuilder()
					.add(Json.createObjectBuilder()
							.add("type", achievement)
							.add("description", description)
							.add("bonus", bonus))
					.build();

			JsonObject allAcheivements = Json.createObjectBuilder()
					.add("type", "achievements")
					.add("achievements", achievements)
					.build();

			LOG.info(allAcheivements.toString());
			// In theory, the server could queue the achievements per player and send as one
			// the client is prepared to receive several achievements in a batch

			// sendOnePlayer(playerId, allAcheivements.toString());

		}

		return msg.ack();
	}

}

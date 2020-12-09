/*-
 * #%L
 * Leaderboard Aggregator
 * %%
 * Copyright (C) 2020 Red Hat Inc.,
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.redhat.developers.demos.streams;

import com.redhat.developers.demos.types.Game;
import com.redhat.developers.demos.types.GameTransaction;
import com.redhat.developers.demos.types.Player;
import io.quarkus.kafka.client.serialization.JsonbSerde;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 *
 */
@ApplicationScoped
public class LeaderBoardAggregator {

  Logger logger = Logger.getLogger(LeaderBoardAggregator.class.getName());


  @ConfigProperty(name = "quarkus.kafka-streams.topics")
  List<String> topics;
  
  @ConfigProperty(name = "balloon.game.leaderboard.kvstore.name")
  String kvStoreName;

  @ConfigProperty(name = "balloon.game.leaderboard.aggregator.stream")
  String outStream;

  @Produces
  public Topology buildLeaderBoard() {
    JsonbSerde<GameTransaction> gameTxSerde =
      new JsonbSerde<>(GameTransaction.class);
    JsonbSerde<Player> playerSerde = new JsonbSerde<>(Player.class);
    StreamsBuilder builder = new StreamsBuilder();
    KeyValueBytesStoreSupplier storeSupplier =
      Stores.persistentKeyValueStore(kvStoreName);

    // logger.log(Level.FINE, "Using Topic Pattern :{0}", topicPattern);
    // validatePattern();

    KStream<String, Player> playerStream = builder
      .stream(topics,
        (Consumed.with(Serdes.String(), gameTxSerde)))
      .groupByKey()
      .aggregate(Player::newPlayer, this::aggregatePlayerScore,
        Materialized.<String, Player>as(storeSupplier)
          .withKeySerde(Serdes.String())
          .withValueSerde(playerSerde))
      .toStream();

    playerStream.to(outStream,
      Produced.with(Serdes.String(), playerSerde));

    Topology topology = builder.build();
    logger.log(Level.FINE, topology.describe()
                                   .toString());
    return topology;
  }

  /**
   *
   */
  protected Player aggregatePlayerScore(String key,
    GameTransaction gameTransaction,
    Player aggregatedPlayer) {
    Game game = gameTransaction.game;
    Player player = gameTransaction.player;
    logger.log(Level.FINE,
      "Aggregation Key {0} and Player {1}",
      new Object[]{key, aggregatedPlayer.playerId});
    aggregatedPlayer
      .gameId(game.gameId)
      .playerId(player.playerId)
      .username(player.username)
      .total(gameTransaction.total)
      .red(gameTransaction.red)
      .green(gameTransaction.green)
      .blue(gameTransaction.blue)
      .yellow(gameTransaction.yellow)
      .goldenSnitch1(gameTransaction.goldenSnitch1)
      .goldenSnitch2(gameTransaction.goldenSnitch2);
    logger.log(Level.FINE,
      "Aggregated score for player Player {0} is {1} ",
      new Object[]{aggregatedPlayer.playerId,
        gameTransaction.total});
    return aggregatedPlayer;
  }
}

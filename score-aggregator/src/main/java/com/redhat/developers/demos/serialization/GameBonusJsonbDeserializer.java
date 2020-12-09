package com.redhat.developers.demos.serialization;


import com.redhat.developers.demos.types.GameBonus;
import io.quarkus.kafka.client.serialization.JsonbDeserializer;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class GameBonusJsonbDeserializer extends JsonbDeserializer<GameBonus> {
	public GameBonusJsonbDeserializer() {
		super(GameBonus.class);
	}

}

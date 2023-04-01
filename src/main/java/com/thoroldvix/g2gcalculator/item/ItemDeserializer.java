package com.thoroldvix.g2gcalculator.item;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class ItemDeserializer extends StdDeserializer<ItemStats> {
    public ItemDeserializer() {
        this(null);
    }

    public ItemDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public ItemStats deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        JsonNode node = jp.getCodec().readTree(jp);

        String server = node.get("server").asText();
        int itemId = node.get("itemId").asInt();
        String name = node.get("name").asText();
        long marketValue = node.get("stats").get("current").get("marketValue").asLong();
        long minBuyout = node.get("stats").get("current").get("minBuyout").asLong();
        int quantity = node.get("stats").get("current").get("quantity").asInt();
        String lastUpdatedString = node.get("stats").get("lastUpdated").asText();

        LocalDateTime lastUpdated = getLastUpdated(lastUpdatedString);

        return ItemStats.builder()
                .server(server)
                .itemId(itemId)
                .lastUpdated(lastUpdated)
                .name(name)
                .marketValue(marketValue)
                .minBuyout(minBuyout)
                .quantity(quantity)
                .build();
    }

    private LocalDateTime getLastUpdated(String lastUpdatedString) {
        return Instant.parse(lastUpdatedString)
                .atZone(ZoneOffset.UTC)
                .toLocalDateTime();

    }
}
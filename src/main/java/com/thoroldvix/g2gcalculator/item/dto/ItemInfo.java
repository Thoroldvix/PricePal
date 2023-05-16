package com.thoroldvix.g2gcalculator.item.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thoroldvix.g2gcalculator.item.ItemQuality;
import com.thoroldvix.g2gcalculator.item.ItemType;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@JsonDeserialize(using = ItemInfoDeserializer.class)
public record ItemInfo(
        String server,
        ItemType type,
        ItemQuality quality,
        String icon,
        BigDecimal price,
        String currency,
        String name,
        AuctionHouseInfo auctionHouseInfo,
        LocalDateTime lastUpdated

) {
    public String getFormatterItemName() {
        return String.join("-", name.toLowerCase()
                .split("\\s+"));
    }
    public String getWowheadUrl() {
        return String.format("https://www.wowhead.com/wotlk/item=%d", auctionHouseInfo.itemId());
    }
}
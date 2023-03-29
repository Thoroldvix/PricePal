package com.thoroldvix.g2gcalculator.service;

import com.thoroldvix.g2gcalculator.api.AuctionHouseClient;
import com.thoroldvix.g2gcalculator.dto.AuctionHouseResponse;
import com.thoroldvix.g2gcalculator.dto.ItemResponse;
import com.thoroldvix.g2gcalculator.error.NotFoundException;
import com.thoroldvix.g2gcalculator.mapper.AuctionHouseMapper;
import com.thoroldvix.g2gcalculator.model.Realm;
import com.thoroldvix.g2gcalculator.repository.AuctionHouseRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoroldvix.g2gcalculator.validation.ValidAhID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClassicAuctionHouseService implements AuctionHouseService {

    private final AuctionHouseClient auctionHouseClient;

    private final ObjectMapper objectMapper;
    private final AuctionHouseRepository auctionHouseRepository;

    private final AuctionHouseMapper auctionHouseMapper;

    private final ClassicRealmService classicRealmService;
    @Override
    public AuctionHouseResponse getAuctionHouseByRealmName(String realmName) {
        Realm realm = classicRealmService.getRealm(realmName);
        return auctionHouseRepository.findByRealm(realm).map(auctionHouseMapper::toAuctionHouseResponse)
                .orElseThrow(() -> new NotFoundException("No auction house found for realm: " + realmName));
    }
     @Override
     public AuctionHouseResponse getAuctionHouseById(Integer auctionHouseId) {
        return auctionHouseRepository.findById(auctionHouseId).map(auctionHouseMapper::toAuctionHouseResponse)
                .orElseThrow(() -> new NotFoundException("No auction house found for id: " + auctionHouseId));
    }

    @Override
    @SneakyThrows
    public List<ItemResponse> getAllItemsByAuctionHouseId(Integer auctionHouseId) {
        return objectMapper.readValue(auctionHouseClient.getAllAuctionHouseItems(auctionHouseId), new TypeReference<List<ItemResponse>>() {});
    }

    @Override
    @SneakyThrows
    public ItemResponse getAuctionHouseItem(Integer auctionHouseId, Integer itemId) {
        return objectMapper.readValue(auctionHouseClient.getAuctionHouseItem(auctionHouseId, itemId), ItemResponse.class);
    }
}
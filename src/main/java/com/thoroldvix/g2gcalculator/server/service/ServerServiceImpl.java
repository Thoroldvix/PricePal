package com.thoroldvix.g2gcalculator.server.service;

import com.thoroldvix.g2gcalculator.common.StringEnumConverter;
import com.thoroldvix.g2gcalculator.server.api.PopulationClient;
import com.thoroldvix.g2gcalculator.server.dto.PopulationResponse;
import com.thoroldvix.g2gcalculator.server.dto.ServerPrice;
import com.thoroldvix.g2gcalculator.server.dto.ServerResponse;
import com.thoroldvix.g2gcalculator.server.entity.Faction;
import com.thoroldvix.g2gcalculator.server.entity.Region;
import com.thoroldvix.g2gcalculator.server.entity.Server;
import com.thoroldvix.g2gcalculator.server.entity.ServerRepository;
import com.vaadin.flow.router.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServerServiceImpl implements ServerService {

    private final ServerRepository serverRepository;
    private final ServerMapper serverMapper;
    private final PriceMapper priceMapper;
    private final PopulationClient populationClient;

    @Override
    public List<ServerResponse> getAllServers(Pageable pageable) {
        return serverRepository.findAll(pageable).getContent().stream()
                .map(serverMapper::toServerResponse)
                .toList();
    }

    @Override
    public Server getServerById(int id) {
        return serverRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No server found for id: " + id));
    }

    @Override
    public ServerResponse getServerResponseById(int id) {
        return serverRepository.findById(id)
                .map(serverMapper::toServerResponse)
                .orElseThrow(() -> new NotFoundException("No server found for id: " + id));
    }

    @Override
    public List<ServerResponse> getAllServersForRegion(Region region) {
        Objects.requireNonNull(region, "Region cannot be null");
        return getAllServersForRegion(List.of(region));
    }

    @Override
    public List<ServerResponse> getAllServersForRegion(List<Region> regions) {
        Objects.requireNonNull(regions, "Regions cannot be null");
        return serverRepository.findAllByRegionIn(regions).stream()
                .map(serverMapper::toServerResponse)
                .toList();
    }

    @Override
    public List<ServerResponse> getAllServers() {
        return serverRepository.findAll().stream()
                .map(serverMapper::toServerResponse)
                .toList();
    }

    public List<ServerResponse> getAllServersByName(String name) {
        if (!StringUtils.hasText(name)) {
            return getAllServers();
        } else {
            return serverRepository.findAllByName(name).stream()
                    .map(serverMapper::toServerResponse)
                    .toList();
        }
    }

    @Override
    public ServerResponse getServerResponse(String serverName) {
        String exactServerName = getExactServerName(serverName, " ");
        Faction faction = getFaction(serverName);

        Server server = serverRepository.findByNameAndFaction(exactServerName, faction)
                .orElseThrow(() -> new NotFoundException("No server found with name: " + exactServerName));
        PopulationResponse population = getPopulation(server, serverName);
        ServerPrice price = priceMapper.toPriceResponse(serverMapper.mostRecentPrice(server.getPrices()));

        return ServerResponse.builder()
                .name(server.getName())
                .type(server.getType())
                .faction(server.getFaction())
                .region(server.getRegion())
                .price(price)
                .population(population)
                .build();
    }

    private PopulationResponse getPopulation(Server server, String serverName) {
        String exactServerName = getExactServerName(serverName).replaceAll("'", "");
        return populationClient.getPopulationForServer(server.getRegion().getParentRegion(), exactServerName);
    }

    @Override
    public Server getServer(String serverName) {
        String exactServerName = getExactServerName(serverName);
        Faction faction = getFaction(serverName);
        return serverRepository.findByNameAndFaction(exactServerName, faction)
                .orElseThrow(() -> new NotFoundException("No server found with name: " + exactServerName));
    }

    private String getExactServerName(String serverName) {
        if (!StringUtils.hasText(serverName)) {
            throw new IllegalArgumentException("Server name cannot be null or empty");
        }
        String[] split = serverName.split("-");
        if (split.length == 3) {
            return (split[0] +  split[1]).toLowerCase();
        }
        return serverName.split("-")[0].toLowerCase();
    }

    private Faction getFaction(String serverName) {
        String[] split = serverName.split("-");
        if (split.length == 1) {
            throw new IllegalArgumentException("Server name must contain a faction");
        }
        String faction = split[split.length - 1];

        return StringEnumConverter.fromString(faction, Faction.class);
    }

}
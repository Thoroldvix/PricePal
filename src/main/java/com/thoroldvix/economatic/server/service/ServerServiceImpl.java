package com.thoroldvix.economatic.server.service;

import com.thoroldvix.economatic.server.dto.ServerListResponse;
import com.thoroldvix.economatic.server.dto.ServerResponse;
import com.thoroldvix.economatic.server.dto.ServerSummaryResponse;
import com.thoroldvix.economatic.server.error.ServerNotFoundException;
import com.thoroldvix.economatic.server.mapper.ServerMapper;
import com.thoroldvix.economatic.server.mapper.ServerSummaryMapper;
import com.thoroldvix.economatic.server.model.Faction;
import com.thoroldvix.economatic.server.model.Region;
import com.thoroldvix.economatic.server.model.Server;
import com.thoroldvix.economatic.server.repository.ServerRepository;
import com.thoroldvix.economatic.server.repository.ServerSummaryProjection;
import com.thoroldvix.economatic.shared.dto.SearchRequest;
import com.thoroldvix.economatic.shared.service.SearchSpecification;
import com.thoroldvix.economatic.shared.util.StringEnumConverter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

import static com.thoroldvix.economatic.server.error.ServerErrorMessages.*;
import static com.thoroldvix.economatic.shared.util.Utils.notEmpty;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ServerServiceImpl implements ServerService {

    private final ServerRepository serverRepository;
    private final ServerSummaryMapper serverSummaryMapper;
    private final ServerMapper serverMapper;
    private final SearchSpecification<Server> searchSpecification;


    @Override
    @Cacheable("server-cache")
    public ServerResponse getServer(
            @NotEmpty(message = SERVER_IDENTIFIER_CANNOT_BE_NULL_OR_EMPTY)
            String serverIdentifier) {
        Optional<Server> server = findServer(serverIdentifier);
        return server.map(serverMapper::toResponse)
                .orElseThrow(() -> new ServerNotFoundException("No server found for identifier: " + serverIdentifier));
    }

    @Override
    public ServerListResponse search(@Valid @NotNull(message = "Search request cannot be null") SearchRequest searchRequest) {
        Specification<Server> spec =
                searchSpecification.create(searchRequest.globalOperator(), searchRequest.searchCriteria());
        List<Server> servers = serverRepository.findAll(spec);
        notEmpty(servers, () -> new ServerNotFoundException("No servers found for search request"));
        return serverMapper.toServerListResponse(servers);
    }

    @Override
    @Cacheable("server-cache")
    public ServerListResponse getAll() {
        List<Server> servers = serverRepository.findAll();
        notEmpty(servers, () -> new ServerNotFoundException("No servers found"));
        return serverMapper.toServerListResponse(servers);
    }


    @Override
    public ServerSummaryResponse getSummary() {
        ServerSummaryProjection summaryProjection = serverRepository.getSummary();
        return serverSummaryMapper.toResponse(summaryProjection);
    }

    @Override
    public ServerListResponse getAllForRegion(
            @NotEmpty(message = REGION_NAME_CANNOT_BE_NULL_OR_EMPTY)
            String regionName) {
        List<Server> servers = findAllByRegion(regionName);
        notEmpty(servers, () -> new ServerNotFoundException("No servers found for region: " + regionName));
        return serverMapper.toServerListResponse(servers);
    }

    @Override
    public ServerListResponse getAllForFaction(
            @NotEmpty(message = FACTION_NAME_CANNOT_BE_NULL_OR_EMPTY)
            String factionName) {
        List<Server> servers = findAllByFaction(factionName);
        notEmpty(servers, () -> new ServerNotFoundException("No servers found for faction: " + factionName));
        return serverMapper.toServerListResponse(servers);
    }

    private List<Server> findAllByRegion(String regionName) {
        Region region = StringEnumConverter.fromString(regionName, Region.class);
        return serverRepository.findAllByRegion(region);
    }

    private List<Server> findAllByFaction(String factionName) {
        Faction faction = StringEnumConverter.fromString(factionName, Faction.class);
        return serverRepository.findAllByFaction(faction);
    }

    private Optional<Server> findServer(String serverIdentifier) {
        try {
            int serverId = Integer.parseInt(serverIdentifier);
            return serverRepository.findById(serverId);
        } catch (NumberFormatException e) {
            return serverRepository.findByUniqueName(serverIdentifier);
        }
    }
}
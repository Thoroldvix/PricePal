package com.thoroldvix.economatic.population.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.thoroldvix.economatic.population.model.Population;
import com.thoroldvix.economatic.population.dto.TotalPopResponse;
import com.thoroldvix.economatic.population.error.PopulationParsingException;
import com.thoroldvix.economatic.population.rest.WarcraftTavernClient;
import com.thoroldvix.economatic.server.dto.ServerResponse;
import com.thoroldvix.economatic.server.model.Faction;
import com.thoroldvix.economatic.server.model.Server;
import com.thoroldvix.economatic.server.repository.ServerRepository;
import com.thoroldvix.economatic.server.service.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.thoroldvix.economatic.shared.util.Utils.elapsedTimeInMillis;

@RequiredArgsConstructor
@Service
@Slf4j
public  class PopulationUpdateService {

    public static final String UPDATE_ON_STARTUP_OR_DEFAULT = "#{${economatic.update-on-startup} ? -1 : ${economatic.population.update-rate}}";
    public static final String UPDATE_RATE = "${economatic.population.update-rate}";
    private final WarcraftTavernClient warcraftTavernClient;
    private final ServerRepository serverRepository;
    private final ServerService serverServiceImpl;
    private final PopulationService populationService;

    @Scheduled(fixedRateString = UPDATE_RATE,
            initialDelayString = UPDATE_ON_STARTUP_OR_DEFAULT,
            timeUnit = TimeUnit.DAYS)
    @Retryable(maxAttempts = 5)
    protected  void update() {
        log.info("Updating population");
        Instant start = Instant.now();
        String populationJson = warcraftTavernClient.getAll();
        List<Population> populations = retrievePopulations(populationJson);
        populationService.saveAll(populations);
        log.info("Finished updating population in {} ms", elapsedTimeInMillis(start));
    }


    private List<Population> retrievePopulations(String populationJson) {
        List<ServerResponse> servers = serverServiceImpl.getAll().servers();
        List<TotalPopResponse> totalPopulationsForServer = extractFromJson(populationJson);
        return getPopulationList(totalPopulationsForServer, servers);
    }

    private List<Population> getPopulationList(List<TotalPopResponse> totalPopulationsForServer, List<ServerResponse> servers) {
        return totalPopulationsForServer.stream()
                .flatMap(totalPop -> getForBothFactions(totalPop, servers).stream())
                .toList();
    }

    private List<Population> getForBothFactions(TotalPopResponse totalPop,
                                                List<ServerResponse> servers) {

        return servers.stream()
                .filter(server -> server.name().equals(totalPop.serverName()))
                .map(server -> getPopulation(totalPop, server))
                .toList();
    }

    private Population getPopulation(TotalPopResponse totalPopulation, ServerResponse server) {
        int populationSize = server.faction().equals(Faction.HORDE)
                ? totalPopulation.popHorde()
                : totalPopulation.popAlliance();

        Server serverEntity = serverRepository.getReferenceById(server.id());

        return Population.builder()
                .value(populationSize)
                .server(serverEntity)
                .build();
    }

    private List<TotalPopResponse> extractFromJson(String populationJson) {
        ObjectMapper mapper = new ObjectMapper();
        CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, TotalPopResponse.class);
        try {
            return mapper.readValue(populationJson, collectionType);
        } catch (JsonProcessingException e) {
            throw new PopulationParsingException("Error while parsing population json");
        }
    }

}

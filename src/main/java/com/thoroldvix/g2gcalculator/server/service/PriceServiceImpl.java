package com.thoroldvix.g2gcalculator.server.service;

import com.thoroldvix.g2gcalculator.server.dto.ServerPrice;
import com.thoroldvix.g2gcalculator.server.entity.Price;
import com.thoroldvix.g2gcalculator.server.entity.PriceRepository;
import com.thoroldvix.g2gcalculator.server.entity.Region;
import com.thoroldvix.g2gcalculator.server.entity.Server;
import com.vaadin.flow.router.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PriceServiceImpl implements PriceService {

    private final PriceRepository priceRepository;
    private final ServerService serverServiceImpl;

    private final PriceMapper priceMapper;

    @Override
    public ServerPrice getPriceForServer(String serverName) {
        if (!StringUtils.hasText(serverName)) {
            throw new IllegalArgumentException("Server name must not be null or empty");
        }
        Server server = serverServiceImpl.getServer(serverName);
        return priceRepository.findMostRecentPriceByServer(server).map(priceMapper::toPriceResponse)
                .orElseThrow(() -> new NotFoundException("No price found for server: " + server.getName()));
    }

    @Override
    public ServerPrice getPriceForServer(Server server) {
        Objects.requireNonNull(server, "Server must not be null");
        return priceRepository.findMostRecentPriceByServer(server).map(priceMapper::toPriceResponse)
                .orElseThrow(() -> new NotFoundException("No price found for server: " + server.getName()));
    }

    @Override
    @Transactional
    public void savePrice(int serverId, ServerPrice recentPrice) {
        Objects.requireNonNull(recentPrice, "Price must not be null");
        Server server = serverServiceImpl.getServerById(serverId);
        Price price = priceMapper.toPrice(recentPrice);
        price.setServer(server);
        priceRepository.save(price);
    }

    @Override
    public ServerPrice getAvgPriceForRegion(Region region) {
        Objects.requireNonNull(region, "Region must not be null");
        LocalDateTime startDate = LocalDateTime.now().minusDays(14);
        BigDecimal price = priceRepository.findAvgPriceForRegion(region, startDate).map(BigDecimal::new)
                .orElseThrow(() -> new NotFoundException("No avg price found for region: " + region.name()));
        return ServerPrice.builder()
                .value(price)
                .updatedAt(LocalDateTime.now())
                .build();
    }


    @Override
    public List<ServerPrice> getAllPricesForServer(String serverName, Pageable pageable) {
        if (!StringUtils.hasText(serverName)) {
            throw new IllegalArgumentException("Server name must not be null or empty");
        }
        Server server = serverServiceImpl.getServer(serverName);
        return priceRepository.findAllByServer(server, pageable).getContent().stream()
                .map(priceMapper::toPriceResponse)
                .toList();
    }

    @Override
    public List<ServerPrice> getAllPricesForServer(String serverName) {
        if (!StringUtils.hasText(serverName)) {
            throw new IllegalArgumentException("Server name must not be null or empty");
        }
        Server server = serverServiceImpl.getServer(serverName);
        return priceRepository.findAllByServer(server).stream()
                .map(priceMapper::toPriceResponse)
                .toList();
    }

    @Override
    public List<ServerPrice> getAllPricesForServer(int serverId) {
        if (serverId <= 0) {
            throw new IllegalArgumentException("Server id must be positive");
        }
        Server server = serverServiceImpl.getServerById(serverId);
        return priceRepository.findAllByServer(server).stream()
                .map(priceMapper::toPriceResponse)
                .toList();
    }

    @Override
    public ServerPrice getAvgPriceForServer(String serverName) {
        if (!StringUtils.hasText(serverName)) {
            throw new IllegalArgumentException("Server name must not be null or empty");
        }
        Server server = serverServiceImpl.getServer(serverName);
        LocalDateTime startDate = LocalDateTime.now().minusDays(14);
        BigDecimal price = priceRepository.findAvgPriceForServer(server, startDate).map(BigDecimal::new)
                .orElseThrow(() -> new NotFoundException("No avg price found for server: " + server.getName()));

        return ServerPrice.builder()
                .value(price)
                .serverName(server.getName())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
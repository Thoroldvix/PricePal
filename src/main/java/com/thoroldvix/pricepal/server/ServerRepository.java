package com.thoroldvix.pricepal.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServerRepository extends JpaRepository<Server, Integer>, JpaSpecificationExecutor<Server> {

    Optional<Server> findByUniqueName(String uniqueName);

    List<Server> findAllByRegion(Region region);

    @Query(value = """
            SELECT SUM(CASE WHEN faction = 0 THEN 1 ELSE 0 END)          AS alliance,
                   SUM(CASE WHEN faction = 1 THEN 1 ELSE 0 END)          AS horde,
                   SUM(CASE WHEN region = 0 THEN 1 ELSE 0 END) / 2       AS eu,
                   SUM(CASE WHEN region = 1 THEN 1 ELSE 0 END) / 2       AS us,
                   SUM(CASE WHEN type = 0 THEN 1 ELSE 0 END) / 2         AS pve,
                   SUM(CASE WHEN type = 1 THEN 1 ELSE 0 END) / 2         AS pvp,
                   SUM(CASE WHEN type = 2 THEN 1 ELSE 0 END) / 2         AS pvpRp,
                   SUM(CASE WHEN type = 3 THEN 1 ELSE 0 END) / 2         AS rp,
                   SUM(CASE WHEN locale = 'en_GB' THEN 1 ELSE 0 END) / 2 AS enGB,
                   SUM(CASE WHEN locale = 'en_US' THEN 1 ELSE 0 END) / 2 AS enUS,
                   SUM(CASE WHEN locale = 'de_DE' THEN 1 ELSE 0 END) / 2 AS deDE,
                   SUM(CASE WHEN locale = 'es_ES' THEN 1 ELSE 0 END) / 2 AS esES,
                   SUM(CASE WHEN locale = 'fr_FR' THEN 1 ELSE 0 END) / 2 AS frFR,
                   SUM(CASE WHEN locale = 'ru_RU' THEN 1 ELSE 0 END) / 2 AS ruRU,
                   COUNT(name) as total
            FROM server
            """, nativeQuery = true)
    ServerSummaryProjection getSummary();
}
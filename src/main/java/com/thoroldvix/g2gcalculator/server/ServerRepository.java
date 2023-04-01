package com.thoroldvix.g2gcalculator.server;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServerRepository extends JpaRepository<Server, Integer> {


    @Query("select r from Server r  where lower(r.name) = lower(?1) and r.faction = ?2")
    Optional<Server> findByNameAndFaction(String name, Faction faction);

    @Query("SELECT s FROM Server s WHERE s.region IN (:regions)")
    List<Server> findAllByRegionIn(List<Region> regions);
}
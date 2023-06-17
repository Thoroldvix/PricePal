package com.thoroldvix.pricepal.server;

public enum Faction {
    ALLIANCE,
    HORDE;
    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }
}
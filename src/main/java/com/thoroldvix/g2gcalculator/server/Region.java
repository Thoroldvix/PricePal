package com.thoroldvix.g2gcalculator.server;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum Region {
    EU("ac3f85c1-7562-437e-b125-e89576b9a38e"),
    US(("dfced32f-2f0a-4df5-a218-1e068cfadffa")),
    FR("ac3f85c1-7562-437e-b125-e89576b9a38e"),
    DE("ac3f85c1-7562-437e-b125-e89576b9a38e"),
    ES("ac3f85c1-7562-437e-b125-e89576b9a38e"),
    OCE("dfced32f-2f0a-4df5-a218-1e068cfadffa");

    public static List<Region> getEURegions() {
        return  List.of(EU, FR, DE, ES);
    }
    public static List<Region> getUSRegions() {
        return List.of(US, OCE);
    }

    public final String g2gId;

    Region(String g2gId) {
        this.g2gId = g2gId;
    }

}
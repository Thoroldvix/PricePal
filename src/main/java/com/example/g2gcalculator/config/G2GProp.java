package com.example.g2gcalculator.config;

import com.example.g2gcalculator.model.Currency;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;


@ConfigurationProperties(prefix = "g2g")
public record G2GProp(
        /**
         * The interval between updates of the price data, in minutes.
         * Default value is 60 (1 hour).
         */
        @DurationUnit(ChronoUnit.MINUTES)
        Duration updateFreq,
        /**
         * Flag indicating whether to force an update of the price data, effectively ignoring update interval.
         * Default value is false.
         */
        boolean forceUpdate,
        /**
         * The currency used for price calculations.
         * Default value is EUR (Euro).
         */
        Currency currency,

        String apiKey,

        TsmProp tsm


) {
    public record TsmProp(
            String apiKey
    ) {
        public TsmProp {
            if (apiKey == null || apiKey.isBlank()) {
                throw new IllegalArgumentException("TSM API key must not be null or blank");
            }
        }
    }

    public G2GProp {
       updateFreq = updateFreq == null ? Duration.ofMinutes(60) : updateFreq;
       if (updateFreq.isNegative() || updateFreq.isZero()) {
            throw new IllegalArgumentException("Scraping interval must be a positive value");
        }
       currency = currency == null ? Currency.EUR : currency;
    }
}
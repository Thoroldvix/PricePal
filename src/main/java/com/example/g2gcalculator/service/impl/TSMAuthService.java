package com.example.g2gcalculator.service.impl;

import com.example.g2gcalculator.api.TSMAuthClient;
import com.example.g2gcalculator.dto.TokenRequest;
import com.example.g2gcalculator.dto.TokenResponse;
import com.example.g2gcalculator.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TSMAuthService implements AuthService {
    private final TSMAuthClient tsmAuthClient;
    @Value("${g2g.tsm.api-key}")
    private String apiKey;
    private String accessToken;
    private String refreshToken;
    private long expirationTime;

    @Override
    public String getAccessToken() {
        if (accessToken == null || Instant.now().getEpochSecond() > expirationTime) {
            refreshToken();
        }
        return accessToken;
    }

    @Override
    public void refreshToken() {
            TokenRequest request = new TokenRequest();
            request.setClientId("c260f00d-1071-409a-992f-dda2e5498536");
            request.setGrantType("api_token");
            request.setScope("app:realm-api app:pricing-api");
            request.setToken(apiKey);

            TokenResponse response = tsmAuthClient.getToken(request);
            accessToken = response.accessToken();
            refreshToken = response.refreshToken();
            expirationTime = System.currentTimeMillis() + (response.expiresIn() * 1000);
    }
}
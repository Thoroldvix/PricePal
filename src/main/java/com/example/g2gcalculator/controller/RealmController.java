package com.example.g2gcalculator.controller;

import com.example.g2gcalculator.dto.RealmResponse;
import com.example.g2gcalculator.service.RealmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/realms")
@RequiredArgsConstructor
public class RealmController {
    private final RealmService classicRealmService;

    @GetMapping("/{version}")
    public ResponseEntity<List<RealmResponse>> getAllRealms(@PathVariable String version) {
        return ResponseEntity.ok(classicRealmService.getAllRealms());
    }
}
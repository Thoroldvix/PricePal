package com.thoroldvix.g2gcalculator.server;

import com.thoroldvix.g2gcalculator.common.ApiError;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wow-classic/v1/servers")
@RequiredArgsConstructor
public class ServerController {

    private final ServerService serverServiceImpl;

    @GetMapping("/{realmName}")
    public ResponseEntity<ServerResponse> getRealm(@PathVariable String realmName) {
        return ResponseEntity.ok(serverServiceImpl.getServerResponse(realmName));
    }

    @GetMapping
    public ResponseEntity<List<ServerResponse>> getAllRealms(Pageable pageable) {
        return ResponseEntity.ok(serverServiceImpl.getAllServers(pageable));
    }

    @GetMapping("/regions/{regions}")
    public ResponseEntity<?> getAllRealmsForRegion(@PathVariable List<String> regions) {
        if (!verifyRegions(regions))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad request"));

        List<Region> regionList = regions.stream().map(region -> Region.valueOf(region.toUpperCase())).toList();
        return ResponseEntity.ok(serverServiceImpl.getAllServersForRegion(regionList));
    }

    private boolean verifyRegions(List<String> regions) {
        for (String region : regions) {
            if (!Region.contains(region))
                return false;
        }
        return true;
    }
}
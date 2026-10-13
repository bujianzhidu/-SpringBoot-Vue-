package com.example.tourism.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.tourism.entity.TourRoute;
import com.example.tourism.service.TourRouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tour-routes")
@Tag(name = "Tour Route Management", description = "Tour route related operations")
public class TourRouteController {

    private final TourRouteService tourRouteService;

    public TourRouteController(TourRouteService tourRouteService) {
        this.tourRouteService = tourRouteService;
    }

    @GetMapping("/search")
    @Operation(summary = "Search tour routes")
    public ResponseEntity<Page<TourRoute>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer days,
            @RequestParam(required = false) String destination,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ResponseEntity.ok(tourRouteService.search(keyword, days, destination, pageNum, pageSize));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new tour route")
    public ResponseEntity<Void> createRoute(@RequestBody TourRoute tourRoute) {
        tourRouteService.createRoute(tourRoute);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a tour route")
    public ResponseEntity<Void> updateRoute(@PathVariable Long id, @RequestBody TourRoute tourRoute) {
        tourRoute.setId(id);
        tourRouteService.updateRoute(tourRoute);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update tour route status")
    public ResponseEntity<Void> updateStatus(@PathVariable Long id, @RequestParam String status) {
        tourRouteService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get tour route details")
    public ResponseEntity<TourRoute> getRoute(@PathVariable Long id) {
        return ResponseEntity.ok(tourRouteService.getById(id));
    }
} 
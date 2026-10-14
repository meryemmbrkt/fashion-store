package com.meryem.fashionstore.controller;

import com.meryem.fashionstore.dto.CityRequest;
import com.meryem.fashionstore.dto.CityResponse;
import com.meryem.fashionstore.dto.DistrictRequest;
import com.meryem.fashionstore.dto.DistrictResponse;
import com.meryem.fashionstore.dto.PageResponse;
import com.meryem.fashionstore.service.CityDistrictService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/cities-districts")
@RequiredArgsConstructor

public class CityDistrictController {

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "name");

    private final CityDistrictService cityDistrictService;

    @GetMapping("/cities")
    public ResponseEntity<PageResponse<CityResponse>> getAllCities(
            @ParameterObject @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        validateSort(pageable);
        return ResponseEntity.ok(cityDistrictService.getAllCities(pageable));
    }

    @GetMapping("/cities/{id}")
    public ResponseEntity<CityResponse> getCityById(@PathVariable Long id) {
        return ResponseEntity.ok(cityDistrictService.getCityById(id));
    }

    @GetMapping("/cities/name/{name}")
    public ResponseEntity<CityResponse> getCityByName(@PathVariable String name) {
        return ResponseEntity.ok(cityDistrictService.getCityByName(name));
    }

    @GetMapping("/cities/search")
    public ResponseEntity<PageResponse<CityResponse>> searchCities(
            @RequestParam String name,
            @ParameterObject @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        validateSort(pageable);
        return ResponseEntity.ok(cityDistrictService.searchCities(name, pageable));
    }

    @PostMapping("/cities")
    public ResponseEntity<CityResponse> createCity(@Valid @RequestBody CityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cityDistrictService.createCity(request));
    }

    @PutMapping("/cities/{id}")
    public ResponseEntity<CityResponse> updateCity(
            @PathVariable Long id,
            @Valid @RequestBody CityRequest request) {
        return ResponseEntity.ok(cityDistrictService.updateCity(id, request));
    }

    @DeleteMapping("/cities/{id}")
    public ResponseEntity<Void> deleteCity(@PathVariable Long id) {
        cityDistrictService.deleteCity(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/cities/{cityId}/districts")
    public ResponseEntity<PageResponse<DistrictResponse>> getDistrictsByCity(
            @PathVariable Long cityId,
            @ParameterObject @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        validateSort(pageable);
        return ResponseEntity.ok(cityDistrictService.getDistrictsByCityId(cityId, pageable));
    }

    @GetMapping("/districts/{id}")
    public ResponseEntity<DistrictResponse> getDistrictById(@PathVariable Long id) {
        return ResponseEntity.ok(cityDistrictService.getDistrictById(id));
    }

    @GetMapping("/districts/search")
    public ResponseEntity<PageResponse<DistrictResponse>> searchDistricts(
            @RequestParam String name,
            @ParameterObject @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        validateSort(pageable);
        return ResponseEntity.ok(cityDistrictService.searchDistricts(name, pageable));
    }

    @PostMapping("/districts")
    public ResponseEntity<DistrictResponse> createDistrict(
            @Valid @RequestBody DistrictRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cityDistrictService.createDistrict(request));
    }

    @PutMapping("/districts/{id}")
    public ResponseEntity<DistrictResponse> updateDistrict(
            @PathVariable Long id,
            @Valid @RequestBody DistrictRequest request) {
        return ResponseEntity.ok(cityDistrictService.updateDistrict(id, request));
    }

    @DeleteMapping("/districts/{id}")
    public ResponseEntity<Void> deleteDistrict(@PathVariable Long id) {
        cityDistrictService.deleteDistrict(id);
        return ResponseEntity.noContent().build();
    }


    private void validateSort(Pageable pageable) {
        pageable.getSort().forEach(order -> {
            if (!ALLOWED_SORT_FIELDS.contains(order.getProperty())) {
                throw new IllegalArgumentException("Invalid sort field: " + order.getProperty() + ". Allowed: " + ALLOWED_SORT_FIELDS);}
        });
    }
}

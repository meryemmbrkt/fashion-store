package com.meryem.fashionstore.service;

import com.meryem.fashionstore.dto.CityRequest;
import com.meryem.fashionstore.dto.CityResponse;
import com.meryem.fashionstore.dto.DistrictRequest;
import com.meryem.fashionstore.dto.DistrictResponse;
import com.meryem.fashionstore.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface CityDistrictService {


    PageResponse<CityResponse> getAllCities(Pageable pageable);
    CityResponse getCityById(Long id);
    CityResponse getCityByName(String name);
    PageResponse<CityResponse> searchCities(String name, Pageable pageable);
    CityResponse createCity(CityRequest request);
    CityResponse updateCity(Long id, CityRequest request);
    void deleteCity(Long id);


    PageResponse<DistrictResponse> getDistrictsByCityId(Long cityId, Pageable pageable);
    DistrictResponse getDistrictById(Long id);
    PageResponse<DistrictResponse> searchDistricts(String name, Pageable pageable);
    DistrictResponse createDistrict(DistrictRequest request);
    DistrictResponse updateDistrict(Long id, DistrictRequest request);
    void deleteDistrict(Long id);
}
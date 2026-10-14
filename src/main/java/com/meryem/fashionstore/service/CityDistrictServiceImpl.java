package com.meryem.fashionstore.service;

import com.meryem.fashionstore.dto.CityRequest;
import com.meryem.fashionstore.dto.CityResponse;
import com.meryem.fashionstore.dto.DistrictRequest;
import com.meryem.fashionstore.dto.DistrictResponse;
import com.meryem.fashionstore.dto.PageResponse;
import com.meryem.fashionstore.entity.City;
import com.meryem.fashionstore.entity.District;
import com.meryem.fashionstore.exception.DuplicateResourceException;
import com.meryem.fashionstore.exception.ResourceNotFoundException;
import com.meryem.fashionstore.mapper.CityDistrictMapper;
import com.meryem.fashionstore.repository.CityRepository;
import com.meryem.fashionstore.repository.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CityDistrictServiceImpl implements CityDistrictService {

    private final CityRepository cityRepository;
    private final DistrictRepository districtRepository;
    private final CityDistrictMapper mapper;


    @Override
    public PageResponse<CityResponse> getAllCities(Pageable pageable) {
        return mapper.toCityPageResponse(cityRepository.findAll(pageable));
    }

    @Override
    public CityResponse getCityById(Long id) {
        return mapper.toCityResponse(findCityOrThrow(id));
    }

    @Override
    public CityResponse getCityByName(String name) {
        String cleanName = validateAndClean(name);

        City city = cityRepository.findByNameIgnoreCase(cleanName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "City not found with name: " + cleanName));

        return mapper.toCityResponse(city);
    }

    @Override
    public PageResponse<CityResponse> searchCities(String name, Pageable pageable) {
        String cleanName = validateAndClean(name);
        return mapper.toCityPageResponse(
                cityRepository.findByNameContainingIgnoreCase(cleanName, pageable));
    }

    @Override
    @Transactional
    public CityResponse createCity(CityRequest request) {
        String cleanName = validateAndClean(request.name());

        if (cityRepository.existsByNameIgnoreCase(cleanName)) {
            throw new DuplicateResourceException("City already exists: " + cleanName);
        }

        try {
            City saved = cityRepository.saveAndFlush(mapper.toCity(cleanName));
            return mapper.toCityResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateResourceException("City already exists: " + cleanName);
        }
    }

    @Override
    @Transactional
    public CityResponse updateCity(Long id, CityRequest request) {
        String cleanName = validateAndClean(request.name());

        City city = findCityOrThrow(id);

        if (cityRepository.existsByNameIgnoreCaseAndIdNot(cleanName, id)) {
            throw new DuplicateResourceException("City already exists: " + cleanName);
        }

        city.setName(cleanName);

        try {
            return mapper.toCityResponse(cityRepository.saveAndFlush(city));
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateResourceException("City already exists: " + cleanName);
        }
    }

    @Override
    @Transactional
    public void deleteCity(Long id) {

        cityRepository.delete(findCityOrThrow(id));
    }


    @Override
    public PageResponse<DistrictResponse> getDistrictsByCityId(Long cityId, Pageable pageable) {
        if (!cityRepository.existsById(cityId)) {
            throw new ResourceNotFoundException("City not found with id: " + cityId);
        }
        return mapper.toDistrictPageResponse(
                districtRepository.findByCityId(cityId, pageable));
    }

    @Override
    public DistrictResponse getDistrictById(Long id) {
        return mapper.toDistrictResponse(findDistrictOrThrow(id));
    }

    @Override
    public PageResponse<DistrictResponse> searchDistricts(String name, Pageable pageable) {
        String cleanName = validateAndClean(name);
        return mapper.toDistrictPageResponse(
                districtRepository.findByNameContainingIgnoreCase(cleanName, pageable));
    }

    @Override
    @Transactional
    public DistrictResponse createDistrict(DistrictRequest request) {
        String cleanName = validateAndClean(request.name());
        City city = findCityOrThrow(request.cityId());

        if (districtRepository.existsByNameIgnoreCaseAndCityId(cleanName, city.getId())) {
            throw new DuplicateResourceException(
                    "District already exists in this city: " + cleanName);
        }

        try {
            District saved = districtRepository.saveAndFlush(
                    mapper.toDistrict(cleanName, city));
            return mapper.toDistrictResponse(saved);
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateResourceException(
                    "District already exists in this city: " + cleanName);
        }
    }

    @Override
    @Transactional
    public DistrictResponse updateDistrict(Long id, DistrictRequest request) {
        String cleanName = validateAndClean(request.name());

        District district = findDistrictOrThrow(id);
        City city = findCityOrThrow(request.cityId());

        if (districtRepository.existsByNameIgnoreCaseAndCityIdAndIdNot(
                cleanName, city.getId(), id)) {
            throw new DuplicateResourceException(
                    "District already exists in this city: " + cleanName);
        }

        district.setName(cleanName);
        district.setCity(city);

        try {
            return mapper.toDistrictResponse(districtRepository.saveAndFlush(district));
        } catch (DataIntegrityViolationException ex) {
            throw new DuplicateResourceException(
                    "District already exists in this city: " + cleanName);
        }
    }

    @Override
    @Transactional
    public void deleteDistrict(Long id) {
        districtRepository.delete(findDistrictOrThrow(id));
    }


    private City findCityOrThrow(Long id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "City not found with id: " + id));
    }

    private District findDistrictOrThrow(Long id) {
        return districtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "District not found with id: " + id));
    }

    private String validateAndClean(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        return value.trim();
    }
}
package com.meryem.fashionstore.mapper;

import com.meryem.fashionstore.dto.CityResponse;
import com.meryem.fashionstore.dto.DistrictResponse;
import com.meryem.fashionstore.dto.PageResponse;
import com.meryem.fashionstore.entity.City;
import com.meryem.fashionstore.entity.District;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CityDistrictMapper {



    public CityResponse toCityResponse(City city) {
        return new CityResponse(city.getId(), city.getName());
    }

    public DistrictResponse toDistrictResponse(District district) {
        return new DistrictResponse(
                district.getId(),
                district.getName(),
                district.getCity().getId(),
                district.getCity().getName());
    }



    public PageResponse<CityResponse> toCityPageResponse(Page<City> page) {
        return toPageResponse(page, this::toCityResponse);
    }

    public PageResponse<DistrictResponse> toDistrictPageResponse(Page<District> page) {
        return toPageResponse(page, this::toDistrictResponse);
    }


    private <E, R> PageResponse<R> toPageResponse(Page<E> page, Function<E, R> converter) {
        return new PageResponse<>(
                page.getContent().stream().map(converter).toList(),
                page.getNumber() + 1,
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }


    public City toCity(String cleanName) {
        City city = new City();
        city.setName(cleanName);
        return city;
    }

    public District toDistrict(String cleanName, City city) {
        District district = new District();
        district.setName(cleanName);
        district.setCity(city);
        return district;
    }
}
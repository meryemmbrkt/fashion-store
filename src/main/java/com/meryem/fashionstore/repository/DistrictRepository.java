package com.meryem.fashionstore.repository;

import com.meryem.fashionstore.entity.District;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {


    @EntityGraph(attributePaths = "city")
    Page<District> findByCityId(Long cityId, Pageable pageable);

    @Query(value = """
            SELECT d
            FROM District d
            JOIN FETCH d.city c
            WHERE c.id = :cityId
            """,
            countQuery = """
            SELECT COUNT(d)
            FROM District d
            WHERE d.city.id = :cityId
            """)
    Page<District> findByCityIdCustom(@Param("cityId") Long cityId, Pageable pageable);


    @EntityGraph(attributePaths = "city")
    Page<District> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query(value = """
            SELECT d
            FROM District d
            JOIN FETCH d.city
            WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))
            """,
            countQuery = """
            SELECT COUNT(d)
            FROM District d
            WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))
            """)
    Page<District> searchByNameCustom(@Param("name") String name, Pageable pageable);


    @EntityGraph(attributePaths = "city")
    List<District> findByCityIdAndNameContainingIgnoreCase(Long cityId, String name);

    @Query("""
            SELECT d
            FROM District d
            JOIN FETCH d.city c
            WHERE c.id = :cityId
              AND LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%'))
            ORDER BY d.name
            """)
    List<District> searchInCityCustom(@Param("cityId") Long cityId,
                                      @Param("name") String name);


    boolean existsByNameIgnoreCaseAndCityId(String name, Long cityId);

    @Query("""
            SELECT COUNT(d) > 0
            FROM District d
            WHERE LOWER(d.name) = LOWER(:name)
              AND d.city.id = :cityId
            """)
    boolean existsInCityCustom(@Param("name") String name,
                               @Param("cityId") Long cityId);


    boolean existsByNameIgnoreCaseAndCityIdAndIdNot(String name, Long cityId, Long id);

    @Query("""
            SELECT COUNT(d) > 0
            FROM District d
            WHERE LOWER(d.name) = LOWER(:name)
              AND d.city.id = :cityId
              AND d.id <> :id
            """)
    boolean existsInCityExcludingIdCustom(@Param("name") String name,
                                          @Param("cityId") Long cityId,
                                          @Param("id") Long id);


    @Query("""
            SELECT COUNT(d)
            FROM District d
            WHERE d.city.id = :cityId
            """)
    long countByCityIdCustom(@Param("cityId") Long cityId);

    @Query("""
            SELECT d
            FROM District d
            JOIN FETCH d.city c
            WHERE LOWER(c.name) = LOWER(:cityName)
            ORDER BY d.name
            """)
    List<District> findByCityNameCustom(@Param("cityName") String cityName);

    @Query("""
            SELECT d
            FROM District d
            JOIN FETCH d.city
            WHERE d.id = :id
            """)
    Optional<District> findByIdWithCity(@Param("id") Long id);
}
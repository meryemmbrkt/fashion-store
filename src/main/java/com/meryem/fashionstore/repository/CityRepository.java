package com.meryem.fashionstore.repository;

import com.meryem.fashionstore.entity.City;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {


    Optional<City> findByNameIgnoreCase(String name);

    @Query("""
            SELECT c
            FROM City c
            WHERE LOWER(c.name) = LOWER(:name)
            """)
    Optional<City> findByNameCustom(@Param("name") String name);


    Page<City> findByNameContainingIgnoreCase(String name, Pageable pageable);


    @Query(value = """
            SELECT c
            FROM City c
            WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))
            """,
            countQuery = """
            SELECT COUNT(c)
            FROM City c
            WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))
            """)
    Page<City> searchByNameCustom(@Param("name") String name, Pageable pageable);


    boolean existsByNameIgnoreCase(String name);

    @Query("""
            SELECT COUNT(c) > 0
            FROM City c
            WHERE LOWER(c.name) = LOWER(:name)
            """)
    boolean existsByNameCustom(@Param("name") String name);


    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    @Query("""
            SELECT COUNT(c) > 0
            FROM City c
            WHERE LOWER(c.name) = LOWER(:name)
              AND c.id <> :id
            """)
    boolean existsByNameAndIdNotCustom(@Param("name") String name,
                                       @Param("id") Long id);


    @Query("""
            SELECT DISTINCT c
            FROM City c
            JOIN c.districts d
            ORDER BY c.name
            """)
    List<City> findCitiesWithDistricts();

    @Query("""
            SELECT c
            FROM City c
            WHERE NOT EXISTS (
                SELECT d FROM District d WHERE d.city = c
            )
            ORDER BY c.name
            """)
    List<City> findCitiesWithoutDistricts();
}
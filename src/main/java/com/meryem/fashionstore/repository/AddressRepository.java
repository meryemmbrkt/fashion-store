package com.meryem.fashionstore.repository;

import com.meryem.fashionstore.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByCustomerId(Long customerId);

    @Query(value = "SELECT a FROM Address a WHERE a.id = :id")
    Optional<Address> findById(@Param("id") Long id);

}

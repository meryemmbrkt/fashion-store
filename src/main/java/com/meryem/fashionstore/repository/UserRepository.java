package com.meryem.fashionstore.repository;

import com.meryem.fashionstore.entity.Role;
import com.meryem.fashionstore.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findByEmailContainingIgnoreCaseAndRole(
            String email, Role role, Pageable pageable);


    Page<User> findByEmailContainingIgnoreCase(
            String email, Pageable pageable);

    Page<User> findByRole(Role role, Pageable pageable);

}

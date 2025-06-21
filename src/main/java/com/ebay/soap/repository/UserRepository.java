package com.ebay.soap.repository;

import com.ebay.soap.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find users by username containing (case insensitive)
     */
    List<User> findByUsernameContainingIgnoreCase(String username);

    /**
     * Find users by first name or last name containing
     */
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName);

    /**
     * Find users with rating above threshold
     */
    List<User> findByRatingGreaterThanEqual(Double rating);

    /**
     * Find top sellers by total sales
     */
    List<User> findTop10ByOrderByTotalSalesDesc();

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
}
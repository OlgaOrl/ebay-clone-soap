package com.ebay.soap.repository;

import com.ebay.soap.entity.Listing;
import com.ebay.soap.types.ListingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {
    List<Listing> findByCategory(String category);
    List<Listing> findByStatus(ListingStatus status);
    List<Listing> findByTitleContainingOrDescriptionContaining(String title, String description);
    List<Listing> findBySellerId(Long sellerId);
}
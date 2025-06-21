package com.ebay.soap.repository;

import com.ebay.soap.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    List<Bid> findByListingId(Long listingId);
    List<Bid> findByUserId(Long userId);
}
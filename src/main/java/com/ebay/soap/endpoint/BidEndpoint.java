package com.ebay.soap.endpoint;

import com.ebay.soap.types.*;
import com.ebay.soap.repository.BidRepository;
import com.ebay.soap.repository.ListingRepository;
import com.ebay.soap.repository.UserRepository;
import com.ebay.soap.entity.Bid;
import com.ebay.soap.entity.Listing;
import com.ebay.soap.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Endpoint
public class BidEndpoint {
    private static final String NAMESPACE_URI = "http://soap.ebay.com/service";
    
    @Autowired
    private BidRepository bidRepository;
    
    @Autowired
    private ListingRepository listingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "placeBidRequest")
    @ResponsePayload
    public PlaceBidResponse placeBid(@RequestPayload PlaceBidRequest request) {
        try {
            // Validate user token
            Optional<User> bidderOpt = userRepository.findByAuthToken(request.getAuthToken());
            if (bidderOpt.isEmpty()) {
                return new PlaceBidResponse(null, "Invalid authentication token");
            }
            
            User bidder = bidderOpt.get();
            
            // Get listing
            Optional<Listing> listingOpt = listingRepository.findById(request.getListingId());
            if (listingOpt.isEmpty()) {
                return new PlaceBidResponse(null, "Listing not found");
            }
            
            Listing listing = listingOpt.get();
            
            // Check if listing is active
            if (listing.getStatus() != ListingStatus.ACTIVE) {
                return new PlaceBidResponse(null, "Listing is not active");
            }
            
            // Check if listing has ended
            if (listing.getEndTime().isBefore(LocalDateTime.now())) {
                listing.setStatus(ListingStatus.EXPIRED);
                listingRepository.save(listing);
                return new PlaceBidResponse(null, "Listing has ended");
            }
            
            // Check if bid amount is higher than current price
            if (request.getAmount().compareTo(listing.getCurrentPrice()) <= 0) {
                return new PlaceBidResponse(null, "Bid amount must be higher than current price");
            }
            
            // Create new bid
            Bid bid = new Bid();
            bid.setListingId(listing.getId());
            bid.setBidderId(bidder.getId());
            bid.setAmount(request.getAmount());
            bid.setCreatedAt(LocalDateTime.now());
            
            // Update listing
            listing.setCurrentPrice(request.getAmount());
            listing.setBidCount(listing.getBidCount() + 1);
            
            // Save bid and update listing
            Bid savedBid = bidRepository.save(bid);
            listingRepository.save(listing);
            
            return new PlaceBidResponse(savedBid, "Bid placed successfully");
        } catch (Exception e) {
            return new PlaceBidResponse(null, "Error placing bid: " + e.getMessage());
        }
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getBidHistoryRequest")
    @ResponsePayload
    public GetBidHistoryResponse getBidHistory(@RequestPayload GetBidHistoryRequest request) {
        try {
            // Get listing
            Optional<Listing> listingOpt = listingRepository.findById(request.getListingId());
            if (listingOpt.isEmpty()) {
                return new GetBidHistoryResponse(List.of(), "Listing not found");
            }
            
            // Get bids for listing
            List<Bid> bids = bidRepository.findByListingIdOrderByAmountDesc(request.getListingId());
            
            return new GetBidHistoryResponse(bids, "Bid history retrieved");
        } catch (Exception e) {
            return new GetBidHistoryResponse(List.of(), "Error retrieving bid history: " + e.getMessage());
        }
    }
}

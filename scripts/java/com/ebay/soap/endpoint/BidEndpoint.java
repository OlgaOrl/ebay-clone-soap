package com.ebay.soap.endpoint;

import com.ebay.soap.entity.Bid;
import com.ebay.soap.entity.Listing;
import com.ebay.soap.entity.User;
import com.ebay.soap.exception.EbayServiceException;
import com.ebay.soap.repository.BidRepository;
import com.ebay.soap.repository.ListingRepository;
import com.ebay.soap.repository.UserRepository;
import com.ebay.soap.types.BidRequest;
import com.ebay.soap.types.BidResponse;
import com.ebay.soap.types.ListingStatus;
import com.ebay.soap.types.PlaceBidResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Endpoint
public class BidEndpoint {
    private static final String NAMESPACE_URI = "http://soap.ebay.com/types";

    @Autowired
    private BidRepository bidRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserRepository userRepository;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "placeBidRequest")
    @ResponsePayload
    public PlaceBidResponse placeBid(@RequestPayload BidRequest request) {
        PlaceBidResponse response = new PlaceBidResponse();

        // Find listing
        Optional<Listing> listingOpt = listingRepository.findById(request.getListingId());
        if (!listingOpt.isPresent()) {
            throw new EbayServiceException("Listing not found", "LISTING_NOT_FOUND");
        }
        
        Listing listing = listingOpt.get();
        
        // Check if listing is active
        if (listing.getStatus() != ListingStatus.ACTIVE) {
            response.setSuccess(false);
            response.setMessage("Listing is not active");
            return response;
        }
        
        // Check if listing has ended
        if (listing.getEndTime().before(new Date())) {
            listing.setStatus(ListingStatus.EXPIRED);
            listingRepository.save(listing);
            response.setSuccess(false);
            response.setMessage("Listing has ended");
            return response;
        }
        
        // Check if bid amount is higher than current price
        if (request.getBidAmount().compareTo(listing.getCurrentPrice()) <= 0) {
            response.setSuccess(false);
            response.setMessage("Bid amount must be higher than current price");
            return response;
        }
        
        // Find user
        Optional<User> userOpt = userRepository.findById(request.getUserId());
        if (!userOpt.isPresent()) {
            throw new EbayServiceException("User not found", "USER_NOT_FOUND");
        }
        
        User user = userOpt.get();
        
        // Create and save bid
        Bid bid = new Bid();
        bid.setUser(user);
        bid.setListing(listing);
        bid.setAmount(request.getBidAmount());
        bid.setCreatedAt(new Date());
        bidRepository.save(bid);
        
        // Update listing current price
        listing.setCurrentPrice(request.getBidAmount());
        listingRepository.save(listing);
        
        response.setSuccess(true);
        response.setMessage("Bid placed successfully");
        response.setBidId(bid.getId());
        
        return response;
    }
}
package com.ebay.soap.endpoint;

import com.ebay.soap.entity.Listing;
import com.ebay.soap.entity.User;
import com.ebay.soap.exception.EbayServiceException;
import com.ebay.soap.repository.ListingRepository;
import com.ebay.soap.repository.UserRepository;
import com.ebay.soap.types.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Endpoint
public class ListingEndpoint {
    private static final String NAMESPACE_URI = "http://soap.ebay.com/types";

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private UserRepository userRepository;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createListingRequest")
    @ResponsePayload
    public CreateListingResponse createListing(@RequestPayload CreateListingRequest request) {
        // Find user
        Optional<User> userOpt = userRepository.findById(request.getSellerId());
        if (!userOpt.isPresent()) {
            throw new EbayServiceException("Seller not found", "USER_NOT_FOUND");
        }
        
        User seller = userOpt.get();
        
        // Create listing
        Listing listing = new Listing();
        listing.setSeller(seller);
        listing.setTitle(request.getTitle());
        listing.setDescription(request.getDescription());
        listing.setCategory(request.getCategory());
        listing.setStartPrice(request.getStartPrice());
        listing.setCurrentPrice(request.getStartPrice());
        listing.setStatus(ListingStatus.ACTIVE);
        listing.setCreatedAt(new Date());
        
        // Set end time (default to 7 days if not specified)
        if (request.getDurationDays() > 0) {
            Date endTime = new Date();
            endTime.setTime(endTime.getTime() + (request.getDurationDays() * 24 * 60 * 60 * 1000));
            listing.setEndTime(endTime);
        } else {
            Date endTime = new Date();
            endTime.setTime(endTime.getTime() + (7 * 24 * 60 * 60 * 1000));
            listing.setEndTime(endTime);
        }
        
        // Save listing
        listing = listingRepository.save(listing);
        
        // Create response
        CreateListingResponse response = new CreateListingResponse();
        response.setListingId(listing.getId());
        response.setMessage("Listing created successfully");
        
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getListingRequest")
    @ResponsePayload
    public GetListingResponse getListing(@RequestPayload GetListingRequest request) {
        // Find listing
        Optional<Listing> listingOpt = listingRepository.findById(request.getListingId());
        if (!listingOpt.isPresent()) {
            throw new EbayServiceException("Listing not found", "LISTING_NOT_FOUND");
        }
        
        Listing listing = listingOpt.get();
        
        // Check if listing has ended
        if (listing.getStatus() == ListingStatus.ACTIVE && listing.getEndTime().before(new Date())) {
            listing.setStatus(ListingStatus.EXPIRED);
            listingRepository.save(listing);
        }
        
        // Create response
        GetListingResponse response = new GetListingResponse();
        response.setListing(convertToListingType(listing));
        
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "searchListingsRequest")
    @ResponsePayload
    public SearchListingsResponse searchListings(@RequestPayload SearchListingsRequest request) {
        List<Listing> listings;
        
        // Search by category if specified
        if (request.getCategory() != null && !request.getCategory().isEmpty()) {
            listings = listingRepository.findByCategory(request.getCategory());
        } else if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            // Search by keyword
            listings = listingRepository.findByTitleContainingOrDescriptionContaining(
                request.getKeyword(), request.getKeyword());
        } else {
            // Get all active listings
            listings = listingRepository.findByStatus(ListingStatus.ACTIVE);
        }
        
        // Create response
        SearchListingsResponse response = new SearchListingsResponse();
        response.getListings().addAll(listings.stream()
            .map(this::convertToListingType)
            .collect(Collectors.toList()));
        
        return response;
    }

    private com.ebay.soap.types.Listing convertToListingType(Listing listing) {
        com.ebay.soap.types.Listing result = new com.ebay.soap.types.Listing();
        result.setId(listing.getId());
        result.setTitle(listing.getTitle());
        result.setDescription(listing.getDescription());
        result.setCategory(listing.getCategory());
        result.setStartPrice(listing.getStartPrice());
        result.setCurrentPrice(listing.getCurrentPrice());
        result.setStatus(listing.getStatus());
        result.setSellerId(listing.getSeller().getId());
        result.setSellerUsername(listing.getSeller().getUsername());
        result.setCreatedAt(listing.getCreatedAt());
        result.setEndTime(listing.getEndTime());
        return result;
    }
}
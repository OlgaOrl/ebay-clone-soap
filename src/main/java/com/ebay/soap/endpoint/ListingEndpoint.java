package com.ebay.soap.endpoint;

import com.ebay.soap.types.*;
import com.ebay.soap.repository.ListingRepository;
import com.ebay.soap.repository.UserRepository;
import com.ebay.soap.entity.Listing;
import com.ebay.soap.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Endpoint
public class ListingEndpoint {
    private static final String NAMESPACE_URI = "http://soap.ebay.com/service";
    
    @Autowired
    private ListingRepository listingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createListingRequest")
    @ResponsePayload
    public CreateListingResponse createListing(@RequestPayload CreateListingRequest request) {
        try {
            // Validate user token
            Optional<User> sellerOpt = userRepository.findByAuthToken(request.getAuthToken());
            if (sellerOpt.isEmpty()) {
                return new CreateListingResponse(null, "Invalid authentication token");
            }
            
            User seller = sellerOpt.get();
            
            // Create new listing
            Listing listing = new Listing();
            listing.setTitle(request.getTitle());
            listing.setDescription(request.getDescription());
            listing.setStartingPrice(request.getStartingPrice());
            listing.setBuyNowPrice(request.getBuyNowPrice());
            listing.setCategoryId(request.getCategoryId());
            listing.setSellerId(seller.getId());
            listing.setStatus(ListingStatus.ACTIVE);
            listing.setCondition(request.getCondition());
            
            // Set end time based on duration (in days)
            LocalDateTime endTime = LocalDateTime.now().plusDays(request.getDuration());
            listing.setEndTime(endTime);
            listing.setCreatedAt(LocalDateTime.now());
            listing.setBidCount(0);
            
            // Save images
            if (request.getImages() != null && !request.getImages().isEmpty()) {
                listing.setImages(request.getImages());
            }
            
            Listing savedListing = listingRepository.save(listing);
            return new CreateListingResponse(savedListing, "Listing created successfully");
        } catch (Exception e) {
            return new CreateListingResponse(null, "Error creating listing: " + e.getMessage());
        }
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getListingRequest")
    @ResponsePayload
    public GetListingResponse getListing(@RequestPayload GetListingRequest request) {
        try {
            Optional<Listing> listingOpt = listingRepository.findById(request.getListingId());
            if (listingOpt.isPresent()) {
                return new GetListingResponse(listingOpt.get(), "Listing found");
            } else {
                return new GetListingResponse(null, "Listing not found");
            }
        } catch (Exception e) {
            return new GetListingResponse(null, "Error retrieving listing: " + e.getMessage());
        }
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "searchListingsRequest")
    @ResponsePayload
    public SearchListingsResponse searchListings(@RequestPayload SearchListingsRequest request) {
        try {
            List<Listing> listings;
            
            // Implement search logic based on request parameters
            if (request.getQuery() != null && !request.getQuery().isEmpty()) {
                listings = listingRepository.findByTitleContainingIgnoreCase(request.getQuery());
            } else if (request.getCategoryId() != null) {
                listings = listingRepository.findByCategoryId(request.getCategoryId());
            } else {
                listings = listingRepository.findAll();
            }
            
            // Apply price filters if provided
            if (request.getMinPrice() != null) {
                listings = listings.stream()
                    .filter(l -> l.getCurrentPrice().compareTo(request.getMinPrice()) >= 0)
                    .collect(Collectors.toList());
            }
            
            if (request.getMaxPrice() != null) {
                listings = listings.stream()
                    .filter(l -> l.getCurrentPrice().compareTo(request.getMaxPrice()) <= 0)
                    .collect(Collectors.toList());
            }
            
            // Apply pagination
            int page = request.getPage() != null ? request.getPage() : 0;
            int size = request.getSize() != null ? request.getSize() : 20;
            
            int totalElements = listings.size();
            int totalPages = (int) Math.ceil((double) totalElements / size);
            
            int fromIndex = page * size;
            int toIndex = Math.min(fromIndex + size, totalElements);
            
            List<Listing> pagedListings = fromIndex < totalElements 
                ? listings.subList(fromIndex, toIndex) 
                : List.of();
            
            return new SearchListingsResponse(
                pagedListings,
                totalElements,
                totalPages,
                size,
                page
            );
        } catch (Exception e) {
            return new SearchListingsResponse(
                List.of(),
                0,
                0,
                0,
                0
            );
        }
    }
}

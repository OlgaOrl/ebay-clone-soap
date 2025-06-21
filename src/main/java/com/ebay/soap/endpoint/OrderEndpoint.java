package com.ebay.soap.endpoint;

import com.ebay.soap.types.*;
import com.ebay.soap.repository.ListingRepository;
import com.ebay.soap.repository.OrderRepository;
import com.ebay.soap.repository.UserRepository;
import com.ebay.soap.entity.Listing;
import com.ebay.soap.entity.Order;
import com.ebay.soap.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Endpoint
public class OrderEndpoint {
    private static final String NAMESPACE_URI = "http://soap.ebay.com/service";
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ListingRepository listingRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "buyNowRequest")
    @ResponsePayload
    public BuyNowResponse buyNow(@RequestPayload BuyNowRequest request) {
        try {
            // Validate user token
            Optional<User> buyerOpt = userRepository.findByAuthToken(request.getAuthToken());
            if (buyerOpt.isEmpty()) {
                return new BuyNowResponse(null, "Invalid authentication token");
            }
            
            User buyer = buyerOpt.get();
            
            // Get listing
            Optional<Listing> listingOpt = listingRepository.findById(request.getListingId());
            if (listingOpt.isEmpty()) {
                return new BuyNowResponse(null, "Listing not found");
            }
            
            Listing listing = listingOpt.get();
            
            // Check if listing is active
            if (listing.getStatus() != ListingStatus.ACTIVE) {
                return new BuyNowResponse(null, "Listing is not active");
            }
            
            // Check if listing has buy now price
            if (listing.getBuyNowPrice() == null) {
                return new BuyNowResponse(null, "Listing does not have buy now option");
            }
            
            // Create new order
            Order order = new Order();
            order.setListingId(listing.getId());
            order.setBuyerId(buyer.getId());
            order.setSellerId(listing.getSellerId());
            order.setPrice(listing.getBuyNowPrice());
            order.setStatus(OrderStatus.PENDING_PAYMENT);
            order.setCreatedAt(LocalDateTime.now());
            
            // Update listing status
            listing.setStatus(ListingStatus.SOLD);
            
            // Save order and update listing
            Order savedOrder = orderRepository.save(order);
            listingRepository.save(listing);
            
            return new BuyNowResponse(savedOrder, "Order created successfully");
        } catch (Exception e) {
            return new BuyNowResponse(null, "Error creating order: " + e.getMessage());
        }
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getUserOrdersRequest")
    @ResponsePayload
    public GetUserOrdersResponse getUserOrders(@RequestPayload GetUserOrdersRequest request) {
        try {
            // Validate user token
            Optional<User> userOpt = userRepository.findByAuthToken(request.getAuthToken());
            if (userOpt.isEmpty()) {
                return new GetUserOrdersResponse(List.of(), "Invalid authentication token");
            }
            
            User user = userOpt.get();
            
            // Check if requested user ID matches authenticated user
            if (!user.getId().equals(request.getUserId())) {
                return new GetUserOrdersResponse(List.of(), "Unauthorized to view these orders");
            }
            
            // Get orders for user (as buyer or seller)
            List<Order> orders;
            if (request.getStatus() != null) {
                orders = orderRepository.findByBuyerIdAndStatus(user.getId(), request.getStatus());
            } else {
                orders = orderRepository.findByBuyerId(user.getId());
            }
            
            return new GetUserOrdersResponse(orders, "Orders retrieved successfully");
        } catch (Exception e) {
            return new GetUserOrdersResponse(List.of(), "Error retrieving orders: " + e.getMessage());
        }
    }
}

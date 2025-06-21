package com.ebay.soap.endpoint;

import com.ebay.soap.entity.User;
import com.ebay.soap.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import jakarta.xml.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Endpoint
public class UserEndpoint {
    private static final String NAMESPACE_URI = "http://soap.ebay.com/service";
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "registerUserRequest")
    @ResponsePayload
    public JAXBElement<User> registerUser(@RequestPayload RegisterUserRequest request) {
        try {
            // Validate input
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                throw new EbayServiceException("Username is required", "INVALID_INPUT");
            }
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                throw new EbayServiceException("Email is required", "INVALID_INPUT");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                throw new EbayServiceException("Password is required", "INVALID_INPUT");
            }

            // Check if user already exists
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                throw new EbayServiceException("Username already exists", "USER_EXISTS");
            }
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new EbayServiceException("Email already exists", "USER_EXISTS");
            }
            
            // Create and save user
            User user = new User();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setCreatedAt(new Date());
            
            user = userRepository.save(user);
            
            // Create response
            ObjectFactory factory = new ObjectFactory();
            return factory.createRegisterUserResponse(user);
        } catch (EbayServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new EbayServiceException("Error registering user: " + e.getMessage(), "INTERNAL_ERROR");
        }
    }
}

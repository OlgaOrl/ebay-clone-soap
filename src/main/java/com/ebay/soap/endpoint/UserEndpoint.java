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

    // Request/Response DTOs
    @XmlRootElement(name = "registerUserRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class RegisterUserRequest {
        @XmlElement(required = true)
        private String username;
        @XmlElement(required = true)
        private String email;
        @XmlElement(required = true)
        private String password;
        @XmlElement(required = true)
        private String firstName;
        @XmlElement(required = true)
        private String lastName;

        // Getters and Setters
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
    }

    @XmlRootElement(name = "registerUserResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class RegisterUserResponse {
        @XmlElement(required = true)
        private User user;
        @XmlElement
        private String message;

        public RegisterUserResponse() {}
        public RegisterUserResponse(User user, String message) {
            this.user = user;
            this.message = message;
        }

        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    @XmlRootElement(name = "getUserRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GetUserRequest {
        @XmlElement(required = true)
        private Long userId;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
    }

    @XmlRootElement(name = "getUserResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class GetUserResponse {
        @XmlElement
        private User user;
        @XmlElement
        private String message;

        public GetUserResponse() {}
        public GetUserResponse(User user, String message) {
            this.user = user;
            this.message = message;
        }

        public User getUser() { return user; }
        public void setUser(User user) { this.user = user; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    @XmlRootElement(name = "searchUsersRequest", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SearchUsersRequest {
        @XmlElement
        private String username;
        @XmlElement
        private String email;

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    @XmlRootElement(name = "searchUsersResponse", namespace = NAMESPACE_URI)
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SearchUsersResponse {
        @XmlElement
        private List<User> users;
        @XmlElement
        private int totalCount;

        public SearchUsersResponse() {}
        public SearchUsersResponse(List<User> users) {
            this.users = users;
            this.totalCount = users != null ? users.size() : 0;
        }

        public List<User> getUsers() { return users; }
        public void setUsers(List<User> users) { this.users = users; }
        public int getTotalCount() { return totalCount; }
        public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    }

    // SOAP Operations
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "registerUserRequest")
    @ResponsePayload
    public RegisterUserResponse registerUser(@RequestPayload RegisterUserRequest request) {
        try {
            // ОТЛАДКА - логируем что пришло
            System.out.println("=== DEBUG registerUser ===");
            System.out.println("Request object: " + request);
            System.out.println("Username: '" + request.getUsername() + "'");
            System.out.println("Email: '" + request.getEmail() + "'");
            System.out.println("Password: '" + request.getPassword() + "'");
            System.out.println("FirstName: '" + request.getFirstName() + "'");
            System.out.println("LastName: '" + request.getLastName() + "'");
            System.out.println("============================");

            // Validate input
            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                System.out.println("ERROR: Username is null or empty!");
                return new RegisterUserResponse(null, "Username is required");
            }
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return new RegisterUserResponse(null, "Email is required");
            }
            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return new RegisterUserResponse(null, "Password is required");
            }

            // Check if user already exists
            if (userRepository.findByUsername(request.getUsername()).isPresent()) {
                return new RegisterUserResponse(null, "Username already exists");
            }
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return new RegisterUserResponse(null, "Email already exists");
            }

            // Create new user
            User user = new User();
            user.setUsername(request.getUsername().trim());
            user.setEmail(request.getEmail().trim());
            user.setFirstName(request.getFirstName() != null ? request.getFirstName().trim() : "");
            user.setLastName(request.getLastName() != null ? request.getLastName().trim() : "");
            user.setRating(0.0);
            user.setTotalSales(0);
            user.setCreatedAt(LocalDateTime.now());

            User savedUser = userRepository.save(user);
            return new RegisterUserResponse(savedUser, "User registered successfully");

        } catch (Exception e) {
            System.out.println("EXCEPTION: " + e.getMessage());
            e.printStackTrace();
            return new RegisterUserResponse(null, "Error registering user: " + e.getMessage());
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getUserRequest")
    @ResponsePayload
    public GetUserResponse getUser(@RequestPayload GetUserRequest request) {
        try {
            if (request.getUserId() == null) {
                return new GetUserResponse(null, "User ID is required");
            }

            Optional<User> userOpt = userRepository.findById(request.getUserId());
            if (userOpt.isPresent()) {
                return new GetUserResponse(userOpt.get(), "User found");
            } else {
                return new GetUserResponse(null, "User not found");
            }

        } catch (Exception e) {
            return new GetUserResponse(null, "Error retrieving user: " + e.getMessage());
        }
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "searchUsersRequest")
    @ResponsePayload
    public SearchUsersResponse searchUsers(@RequestPayload SearchUsersRequest request) {
        try {
            List<User> users;

            if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
                users = userRepository.findByUsernameContainingIgnoreCase(request.getUsername().trim());
            } else if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                Optional<User> userOpt = userRepository.findByEmail(request.getEmail().trim());
                users = userOpt.map(List::of).orElse(List.of());
            } else {
                users = userRepository.findAll();
            }

            return new SearchUsersResponse(users);

        } catch (Exception e) {
            return new SearchUsersResponse(List.of());
        }
    }
}
package com.ebay.soap.entity;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@XmlRootElement(name = "user", namespace = "http://soap.ebay.com/types")
@XmlAccessorType(XmlAccessType.FIELD)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @XmlElement
    private Long id;

    @Column(unique = true, nullable = false)
    @XmlElement
    private String username;

    @Column(unique = true, nullable = false)
    @XmlElement
    private String email;

    @Column(name = "first_name")
    @XmlElement
    private String firstName;

    @Column(name = "last_name")
    @XmlElement
    private String lastName;

    @Column
    @XmlElement
    private Double rating = 0.0;

    @Column(name = "total_sales")
    @XmlElement
    private Integer totalSales = 0;

    @Column(name = "created_at")
    @XmlElement
    private LocalDateTime createdAt;

    // Constructors
    public User() {}

    public User(String username, String email, String firstName, String lastName) {
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.rating = 0.0;
        this.totalSales = 0;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(Integer totalSales) {
        this.totalSales = totalSales;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", rating=" + rating +
                ", totalSales=" + totalSales +
                ", createdAt=" + createdAt +
                '}';
    }
}
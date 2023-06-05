package com.example.demo;

public class Tours {
    private int id;
    private String username;
    private String city;
    private String postalCode;
    private String street;
    private String email;
    private String tour;

    /**
     * constructor
     *
     * @param id
     * @param username
     * @param city
     * @param postalCode
     * @param street
     * @param email
     * @param tour
     */
    public Tours(int id, String username, String city, String postalCode, String street, String email, String tour) {
        this.id = id;
        this.username = username;
        this.city = city;
        this.postalCode = postalCode;
        this.street = street;
        this.email = email;
        this.tour = tour;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getCity() {
        return city;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getStreet() {
        return street;
    }

    public String getEmail() {
        return email;
    }

    public String getTour() {
        return tour;
    }
}

package com.bengkel.booking.models;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {
    private String customerId;
    private String name;
    private String address;
    private String password;
    private List<Vehicle> vehicles;
    private int maxNumberOfService;
    private int numberOfServicesBooked;
    private List<ItemService> bookedServices;  // Ensure this field is defined

    public Customer(String customerId, String name, String address, String password, List<Vehicle> vehicles) {
        super();
        this.customerId = customerId;
        this.name = name;
        this.address = address;
        this.password = password;
        this.vehicles = vehicles;
        this.maxNumberOfService = 1;
        this.numberOfServicesBooked = 0;
        this.bookedServices = new ArrayList<>();  // Initialize the list
    }

    public String getCustomerName() {
        return name;
    }

    public int getNumberOfServicesBooked() {
        return numberOfServicesBooked;
    }

    public void incrementNumberOfServicesBooked() {
        numberOfServicesBooked++;
    }

    public List<ItemService> getBookedServices() {
        return bookedServices;
    }

    public void addService(ItemService service) {
        bookedServices.add(service);
    }
}

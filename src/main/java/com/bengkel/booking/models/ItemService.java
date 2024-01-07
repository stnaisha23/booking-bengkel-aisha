package com.bengkel.booking.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemService {
    private String serviceId;
    private String serviceName;
    private String applicableVehicleType;  // Updated to a single vehicle type
    private int price;

    // Getter and setter for applicableVehicleType
    public String getApplicableVehicleType() {
        return applicableVehicleType;
    }

    public void setApplicableVehicleType(String applicableVehicleType) {
        this.applicableVehicleType = applicableVehicleType;
    }
}

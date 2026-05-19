package com.travelbooking.api.car.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Car {
    private Long id;
    private String model;
    private String type;
    private double dailyRate;
    private boolean available;
}

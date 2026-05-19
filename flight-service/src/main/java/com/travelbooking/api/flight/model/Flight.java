package com.travelbooking.api.flight.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Flight {
    private String id;
    private String airline;
    private String origin;
    private String destination;
    private Double price;
}
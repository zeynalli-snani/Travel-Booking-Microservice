package com.travelbooking.api.car.model;

public class Car {
    private Long id;
    private String model;
    private String type;
    private double dailyRate;
    private boolean available;

    public Car() {
    }

    public Car(Long id, String model, String type, double dailyRate, boolean available) {
        this.id = id;
        this.model = model;
        this.type = type;
        this.dailyRate = dailyRate;
        this.available = available;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}

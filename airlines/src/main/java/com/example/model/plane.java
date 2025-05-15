package com.example.model;

public class Plane {
    private int id;
    private String model;
    private int capacity;
    private int manufacturerId;
    private int airlineId;

    public Plane() {}

    public Plane(int id, String model, int capacity, int manufacturerId, int airlineId) {
        this.id = id;
        this.model = model;
        this.capacity = capacity;
        this.manufacturerId = manufacturerId;
        this.airlineId = airlineId;
    }

    // ГЕТТЕРИ
    public int getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getManufacturerId() {
        return manufacturerId;
    }

    public int getAirlineId() {
        return airlineId;
    }

    // СЕТТЕРИ — якщо потрібно
    public void setId(int id) {
        this.id = id;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setManufacturerId(int manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public void setAirlineId(int airlineId) {
        this.airlineId = airlineId;
    }
}

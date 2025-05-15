package com.example.service;

import com.example.model.Plane;

import java.util.List;

public interface PlaneService {
    List<Plane> getAllPlanes();
    Plane getPlaneById(int id);
    void addPlane(Plane plane);
    void deletePlane(int id);
}


package com.example.controller;

import com.example.model.Plane;
import com.example.service.PlaneService;
import com.example.service.PlaneServiceImpl;

import java.util.List;

public class PlaneController {
    private PlaneService planeService = new PlaneServiceImpl();

    public void showAllPlanes() {
        List<Plane> planes = planeService.getAllPlanes();
        for (Plane plane : planes) {
            System.out.println("Plane: " + plane.getModel() + ", Capacity: " + plane.getCapacity());
        }
    }

    public void createPlane(Plane plane) {
        planeService.addPlane(plane);
        System.out.println("Plane created.");
    }

    public void deletePlane(int id) {
        planeService.deletePlane(id);
        System.out.println("Plane deleted.");
    }
}

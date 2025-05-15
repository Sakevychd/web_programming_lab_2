package com.example.service;

import com.example.dao.PlaneDao;
import com.example.model.Plane;

import java.util.List;

public class PlaneServiceImpl implements PlaneService {
    private PlaneDao planeDAO = new PlaneDao();

    @Override
    public List<Plane> getAllPlanes() {
        return planeDAO.findAll();
    }

    
    @Override
    public Plane getPlaneById(int id) {
        return planeDAO.findById(id);
    }

    @Override
    public void addPlane(Plane plane) {
        planeDAO.save(plane);
    }

    
    @Override
    public void deletePlane(int id) {
        planeDAO.delete(id);
    }
}

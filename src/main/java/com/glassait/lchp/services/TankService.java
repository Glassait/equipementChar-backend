package com.glassait.lchp.services;

import com.glassait.lchp.abstracts.tankData.Tank;
import com.glassait.lchp.model.tank.TankModel;
import com.glassait.lchp.repositories.TankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TankService {
    @Autowired
    private TankRepository tankRepository;

    public List<Tank> getTanks() {
        List<TankModel> tankModels = tankRepository.findAll();
        List<Tank> tanks = new ArrayList<>();

        tankModels.forEach(tankModel -> {
            tanks.add(new Tank(tankModel));
        });

        return tanks;
    }
}
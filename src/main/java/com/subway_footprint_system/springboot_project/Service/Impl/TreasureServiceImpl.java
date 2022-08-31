package com.subway_footprint_system.springboot_project.Service.Impl;

import com.subway_footprint_system.springboot_project.Dao.Impl.TreasureDaoImpl;
import com.subway_footprint_system.springboot_project.Pojo.Treasure;
import com.subway_footprint_system.springboot_project.Service.ITreasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TreasureServiceImpl implements ITreasureService {
    @Autowired
    private TreasureDaoImpl treasureDao;

    @Override
    public boolean insertTreasure(Treasure treasure) {
        return treasureDao.insertTreasure(treasure);
    }

    @Override
    public boolean deleteTreasure(String tid) {
        return treasureDao.deleteTreasure(tid);
    }

    @Override
    public boolean updateTreasure(Treasure treasure) {
        if (null != treasure.getTid())
            return treasureDao.updateTreasure(treasure);
        return false;
    }

    @Override
    public Treasure getTreasure(String tid) {
        return treasureDao.getTreasure(tid);
    }

    @Override
    public List<Treasure> getPositionTreasures(String pid) {
        return treasureDao.getPositionTreasures(pid);
    }

    @Override
    public List<Treasure> getAllTreasures() {
        return treasureDao.getAllTreasures();
    }

    @Override
    public List<Treasure> getUserTreasures(String uid2) {
        return treasureDao.getUserTreasures(uid2);
    }

    @Override
    public List<Treasure> getMerchantTreasures(String mid) {
        return treasureDao.getMerchantTreasures(mid);
    }
}

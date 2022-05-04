package com.subway_footprint_system.springboot_project.Service.Impl;

import com.subway_footprint_system.springboot_project.Dao.Impl.AwardDaoImpl;
import com.subway_footprint_system.springboot_project.Pojo.Award;
import com.subway_footprint_system.springboot_project.Service.IAwardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AwardServiceImpl implements IAwardService {
    @Autowired
    private AwardDaoImpl awardDao;

    @Override
    public boolean insertAward(Award award) {
        return awardDao.insertAward(award);
    }

    @Override
    public boolean deleteAward(String aid) {
        return awardDao.deleteAward(aid);
    }

    @Override
    public boolean updateAward(Award award) {
        return awardDao.updateAward(award);
    }

    @Override
    public Award getAward(String aid) {
        return awardDao.getAward(aid);
    }

    @Override
    public List<Award> getSomeAwards(Integer num) {
        return awardDao.getSomeAwards(num);
    }
}

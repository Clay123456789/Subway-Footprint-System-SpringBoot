package com.subway_footprint_system.springboot_project.Service;

import com.subway_footprint_system.springboot_project.Pojo.Award;

import java.util.List;

public interface IAwardService {

    //增删改查方法
    boolean insertAward(Award award);
    boolean deleteAward(String aid);
    boolean updateAward(Award award);
    Award getAward(String aid);
    //随机抽取若干奖品
    List<Award> getSomeAwards(Integer num);
    //获取指定商户上传的所有奖品
    List<Award> getMerchantAwards(String mid);
    //获取所有奖品
    List<Award> getAllAwards();
}

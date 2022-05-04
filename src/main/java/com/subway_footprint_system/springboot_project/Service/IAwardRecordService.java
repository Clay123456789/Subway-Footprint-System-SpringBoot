package com.subway_footprint_system.springboot_project.Service;

import com.subway_footprint_system.springboot_project.Pojo.AwardRecord;

public interface IAwardRecordService {

    boolean addBuryAwardRecord(String aid,String uid,int num,int credit);

}

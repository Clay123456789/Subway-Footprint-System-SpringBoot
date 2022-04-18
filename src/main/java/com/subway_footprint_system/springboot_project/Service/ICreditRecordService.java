package com.subway_footprint_system.springboot_project.Service;

import com.subway_footprint_system.springboot_project.Pojo.CreditRecord;

import java.util.List;

public interface ICreditRecordService {
    //增删查方法,无需改
    boolean insertCreditRecord(CreditRecord creditRecord);
    boolean deleteCreditRecord(String crid);
    CreditRecord getCreditRecord(String crid);
    List<CreditRecord> getUserCreditRecords(String uid);

}

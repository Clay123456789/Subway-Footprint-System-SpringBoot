package com.subway_footprint_system.springboot_project.Service;

import com.subway_footprint_system.springboot_project.Pojo.CreditRecord;

import java.util.List;

public interface ICreditRecordService {
    //增删查方法
    boolean insertIncreaseCredit(String uid, String way,int num);
    boolean insertReduceCredit(String uid, String way,int num);
    boolean insertCreditRecord(int operation,String uid, String way,int num);
    boolean deleteCreditRecord(String crid);
    CreditRecord getCreditRecord(String crid);
    List<CreditRecord> getUserCreditRecords(String uid,int group);

}

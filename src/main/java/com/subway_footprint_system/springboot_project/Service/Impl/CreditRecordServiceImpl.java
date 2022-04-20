package com.subway_footprint_system.springboot_project.Service.Impl;

import com.subway_footprint_system.springboot_project.Dao.Impl.CreditRecordDaoImpl;
import com.subway_footprint_system.springboot_project.Pojo.CreditRecord;
import com.subway_footprint_system.springboot_project.Service.ICreditRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreditRecordServiceImpl implements ICreditRecordService {
    @Autowired
    private CreditRecordDaoImpl creditRecordDao;

    @Override
    public boolean insertCreditRecord(CreditRecord creditRecord) {
        return creditRecordDao.insertCreditRecord(creditRecord);
    }

    @Override
    public boolean deleteCreditRecord(String crid) {
        return creditRecordDao.deleteCreditRecord(crid);
    }

    @Override
    public CreditRecord getCreditRecord(String crid) {
        return creditRecordDao.getCreditRecord(crid);
    }

    @Override
    public List<CreditRecord> getUserCreditRecords(String uid) {
        return creditRecordDao.getUserCreditRecords(uid);
    }
}

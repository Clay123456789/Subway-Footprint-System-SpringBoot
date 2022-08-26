package com.subway_footprint_system.springboot_project.Service.Impl;

import com.subway_footprint_system.springboot_project.Dao.Impl.MerchantDaoImpl;
import com.subway_footprint_system.springboot_project.Pojo.Merchant;
import com.subway_footprint_system.springboot_project.Pojo.MerchantVo;
import com.subway_footprint_system.springboot_project.Service.IMerchantService;
import com.subway_footprint_system.springboot_project.Utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Service
public class MerchantServiceImpl implements IMerchantService {
    @Autowired
    private MerchantDaoImpl merchantDao;


    @Override
    public boolean insertMerchant(MerchantVo merchantVo) {
        return merchantDao.insertMerchant(merchantVo);
    }

    @Override
    public boolean deleteMerchant(String mid) {
        return merchantDao.deleteMerchant(mid);
    }

    @Override
    public boolean updateMerchant(MerchantVo merchantVo) {
        if(null!=merchantVo.getMid())
            return merchantDao.updateMerchant(merchantVo);
        return false;
    }

    @Override
    public boolean updatePassword(MerchantVo merchantVo) {
        //先判断用户是否合法
        if(judgeByEmail(merchantVo)){
            //获取用户原信息
            Merchant merchant= getMerchantByEmail(merchantVo.getEmail());
            //修改密码
            return merchantDao.changePassword(merchant.getMid(),merchantVo.getNewPassword());
        }
        return false;
    }

    @Override
    public Merchant getMerchantByMid(String mid) {
        return merchantDao.getMerchantByMid(mid);
    }

    @Override
    public Merchant getMerchantByEmail(String email) {
        return merchantDao.getMerchantByEmail(email);
    }

    @Override
    public Merchant getMerchantByAccount(String account) {
        return merchantDao.getMerchantByAccount(account);
    }

    @Override
    public boolean judgeByMid(MerchantVo merchantVo) {
        //根据id查询该用户信息
        Merchant merchant=merchantDao.getMerchantByMid(merchantVo.getMid());
        //用户存在且密码相同，返回真
        if(merchant!=null&&merchantVo.getPassword()!=null){
            return merchant.getPassword().equals(merchantVo.getPassword());
        }
        return false;
    }

    @Override
    public boolean judgeByAccount(MerchantVo merchantVo) {
        //根据账号查询该用户信息
        Merchant merchant=merchantDao.getMerchantByAccount(merchantVo.getAccount());
        //用户存在且密码相同，返回真
        if(merchant!=null&&merchantVo.getPassword()!=null){
            return merchant.getPassword().equals(merchantVo.getPassword());
        }
        return false;
    }

    @Override
    public boolean judgeByEmail(MerchantVo merchantVo) {
        //根据账号查询该用户信息
        Merchant merchant=merchantDao.getMerchantByEmail(merchantVo.getEmail());
        //用户存在且密码相同，返回真
        if(merchant!=null&&merchantVo.getPassword()!=null){
            return merchant.getPassword().equals(merchantVo.getPassword());
        }
        return false;
    }

    @Override
    public boolean submitAuthentication(Merchant merchant) {
        if(null!=merchant.getMid()){
            merchant.setAuthenticated(0);
            merchant.setTime(JWTUtil.getNowTime());
            return merchantDao.updateAuthentication(merchant);
        }
        return false;
    }

    @Override
    public boolean checkAuthentication(String mid) throws ParseException {
        Merchant merchant=merchantDao.getMerchantByMid(mid);
        if(null!=merchant&&1==merchant.getAuthenticated()){
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
            long time=formatter.parse(JWTUtil.getNowTime()).getTime() - formatter.parse(merchant.getTime()).getTime();
            if(time / (24 * 60 * 60 * 1000)>365){//证书达到一年有效期
                merchant.setAuthenticated(2);//2表示过期
                merchantDao.updateAuthentication(merchant);
                return false;
            }else{
                return true;
            }
        }
        return false;
    }
}

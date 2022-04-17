package com.subway_footprint_system.springboot_project.Dao.Impl;

import com.subway_footprint_system.springboot_project.Dao.ILightedStationDao;
import com.subway_footprint_system.springboot_project.Pojo.LightedStation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LightedStationDaoImpl implements ILightedStationDao {


    /*
    * 对于该表数据而言，删改频率较高，查询同一数据频率较低，故不使用redis，直接对mysql操作
    * */
    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     * 添加一行数据
     */
    @Override
    public boolean insertLightedStation(LightedStation lightedStation) {
        try {
            //返回影响行数，为1即增加成功
            int result = jdbcTemplate.update("insert into lightedstation (uid,pid,time,credit) values(?,?,?,?)", lightedStation.getUid(), lightedStation.getPid(), lightedStation.getTime(), lightedStation.getCredit());
            if(1==result){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 删除一行数据
     */
    @Override
    public boolean deleteLightedStation(String uid,String pid) {
        try {
            //返回影响行数，为1即删除成功
            int result= jdbcTemplate.update("delete from lightedstation where uid=? and pid = ?",uid,pid);
            if(1==result){
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }
    /**
     * 修改一行数据
     */
    @Override
    public boolean updateLightedStation(LightedStation lightedStation) {
       try {
           //返回影响行数，为1表示修改成功
           int result = jdbcTemplate.update("update lightedstation set credit=? where uid=? and pid = ?",lightedStation.getCredit(),lightedStation.getUid(),lightedStation.getPid());
           if(1==result){
                return true;
           }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 获取一行数据
     */
    @Override
    public LightedStation getLightedStation(String uid,String pid) {
        RowMapper<LightedStation> rowMapper = new BeanPropertyRowMapper<LightedStation>(LightedStation.class);
        Object object = null;
        //queryForObject会抛出非检查性异常DataAccessException，同时对返回值进行requiredSingleResult操作
        //requiredSingleResult会在查询结果为空的时候抛出EmptyResultDataAccessException异常，需要捕获后进行处理
        try {
            object = jdbcTemplate.queryForObject("select * from lightedstation where uid=? and pid = ?",rowMapper,uid,pid);
        } catch (EmptyResultDataAccessException e1) {
            //查询结果为空，返回null
            return null;
        }
        return (LightedStation) object;
    }

    @Override
    public List<LightedStation> getUserLightedStations(String uid) {
        List<LightedStation> list=null;
        try {
            RowMapper<LightedStation> rowMapper = new BeanPropertyRowMapper<LightedStation>(LightedStation.class);
            list= jdbcTemplate.query("select * from lightedstation where uid=? order by time ASC",rowMapper,uid);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return list;
    }
}

package com.subway_footprint_system.springboot_project.Dao.Impl;


import com.subway_footprint_system.springboot_project.Dao.ITreasureDao;
import com.subway_footprint_system.springboot_project.Pojo.Treasure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class TreasureDaoImpl implements ITreasureDao {

    /*
     * 对于该表数据而言，更新频率较高，故不使用redis，直接对mysql操作
     * */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 添加一行数据
     */
    @Override
    public boolean insertTreasure(Treasure treasure) {
        try {
            //返回影响行数，为1即增加成功
            int result = jdbcTemplate.update("insert into treasure(tid,variety,content,credit,pid,fromdate,todate,status,uid,mid,uid2,getdate,message) values(?,?,?,?,?,?,?,?,?,?,?,?,?) ",
                    treasure.getTid(), treasure.getVariety(), treasure.getContent(), treasure.getCredit(), treasure.getPid(), treasure.getFromdate(), treasure.getTodate(), treasure.getStatus(), treasure.getUid(), treasure.getMid(), treasure.getUid2(), treasure.getGetdate(), treasure.getMessage());
            if (1 == result) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 删除一行数据
     */
    @Override
    public boolean deleteTreasure(String tid) {
        try {
            //返回影响行数，为1即删除成功
            int result = jdbcTemplate.update("delete from treasure where tid=?", tid);
            if (1 == result) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    @Override
    public boolean updateTreasure(Treasure treasure) {
        try {
            //返回影响行数，为1表示修改成功
            int result = jdbcTemplate.update("update treasure set variety=?,content=?,credit=?,pid=?,fromdate=?,todate=?,status=?,uid=?,mid=?,uid2=?,getdate=?,message=? where tid=?",
                    treasure.getVariety(), treasure.getContent(), treasure.getCredit(), treasure.getPid(), treasure.getFromdate(), treasure.getTodate(), treasure.getStatus(), treasure.getUid(), treasure.getMid(), treasure.getUid2(), treasure.getGetdate(), treasure.getMessage(), treasure.getTid());
            if (1 == result) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 获取一行数据
     */
    @Override
    public Treasure getTreasure(String tid) {
        RowMapper<Treasure> rowMapper = new BeanPropertyRowMapper<Treasure>(Treasure.class);
        Object object = null;
        try {
            object = jdbcTemplate.queryForObject("select * from treasure where tid=?", rowMapper, tid);
        } catch (EmptyResultDataAccessException e) {
            //查询结果为空
            return null;
        }
        return (Treasure) object;
    }

    /**
     * 获取指定站点pid的所有记录
     * 查询的是未被挖走的宝箱
     */
    @Override
    public List<Treasure> getPositionTreasure(String pid) {
        List<Treasure> list = null;
        try {
            RowMapper<Treasure> rowMapper = new BeanPropertyRowMapper<Treasure>(Treasure.class);
            list = jdbcTemplate.query("select * from treasure where pid=? and status=0 order by fromdate ASC", rowMapper, pid);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    /**
     * 获取所有记录
     * 查询的是未被挖走的宝箱
     */
    @Override
    public List<Treasure> getAllTreasure() {
        List<Treasure> list = null;
        try {
            RowMapper<Treasure> rowMapper = new BeanPropertyRowMapper<Treasure>(Treasure.class);
            list = jdbcTemplate.query("select * from treasure where status=0 order by fromdate ASC", rowMapper);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    @Override
    public List<Treasure> getUserTreasure(String uid2) {
        List<Treasure> list = null;
        try {
            RowMapper<Treasure> rowMapper = new BeanPropertyRowMapper<Treasure>(Treasure.class);
            list = jdbcTemplate.query("select * from treasure where uid2=? order by status ASC,getdate ASC", rowMapper, uid2);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    /**
     * 获取某一用户的记录
     * 查询的是未打开的宝箱
     */

}

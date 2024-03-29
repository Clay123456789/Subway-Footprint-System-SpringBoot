package com.subway_footprint_system.springboot_project.Dao.Impl;


import com.subway_footprint_system.springboot_project.Dao.ITreasureDao;
import com.subway_footprint_system.springboot_project.Pojo.Treasure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class TreasureDaoImpl implements ITreasureDao {

    /*
     * 对于该表数据而言，更新频率较高，故不使用redis，直接对mysql操作
     * */
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

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
    public List<Treasure> getPositionTreasures(String pid) {
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
    public List<Treasure> getAllTreasures() {
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
    public List<Treasure> getUserTreasures(String uid2) {
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

    @Override
    public List<Treasure> getMerchantTreasures(String mid) {
        List<Treasure> list = null;
        try {
            RowMapper<Treasure> rowMapper = new BeanPropertyRowMapper<Treasure>(Treasure.class);
            list = jdbcTemplate.query("select * from treasure where mid=? order by status ASC,getdate ASC", rowMapper, mid);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return list;
    }

    @Override
    public float getPositionTreasureProbability(String pid) {

        // 从缓存中 取出信息
        String key = "PositionTreasureProbability_" + pid;
        Boolean hasKey = redisTemplate.hasKey(key);

        ValueOperations operations = redisTemplate.opsForValue();
        // 缓存中存在
        if (hasKey) {
            return (float) operations.get(key);
        }
        return 1;
    }

    @Override
    public boolean changePositionTreasureProbability(String pid, float probability) {
        // 从缓存中 取出信息
        String key = "PositionTreasureProbability_" + pid;
        Boolean hasKey = redisTemplate.hasKey(key);
        ValueOperations operations = redisTemplate.opsForValue();
        // 缓存中存在，删掉
        if (hasKey) {
            redisTemplate.delete(key);
        }
        //插入新的
        operations.set(key, probability, 365, TimeUnit.DAYS);//向redis里存入数据,设置缓存时间为一年

        return true;
    }

    /**
     * 获取某一用户的记录
     * 查询的是未打开的宝箱
     */

}

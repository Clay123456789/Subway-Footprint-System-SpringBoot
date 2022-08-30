package com.subway_footprint_system.springboot_project.Utils;

import com.google.gson.Gson;
import com.subway_footprint_system.springboot_project.Pojo.AwardRecord;
import com.subway_footprint_system.springboot_project.Service.Impl.AwardRecordServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
  public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
    super(listenerContainer);
  }

  @Resource private AwardRecordServiceImpl awardRecordService;
  private Logger logger = LoggerFactory.getLogger(RedisKeyExpirationListener.class);
  /**
   * 针对redis数据失效事件，进行数据处理
   *
   * @param message
   * @param pattern
   */
  @Override
  public void onMessage(Message message, byte[] pattern) {
    // 用户做自己的业务处理即可,注意message.toString()可以获取失效的key
    String expiredKey = message.toString();
    try {
      AwardRecord awardRecord = new Gson().fromJson(expiredKey, AwardRecord.class);
      if (null != awardRecord) { // 订单失效
        logger.info(
            "用户"
                + awardRecord.getUid()
                + "商品订单"
                + awardRecord.getArid()
                + "过期，商品"
                + awardRecord.getAid()
                + "释放");
        awardRecordService.expireOrder(awardRecord.getArid());
      }
    } catch (Exception ignored) {

    }
  }
}

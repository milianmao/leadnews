package com.heima.kafka.listenner;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/26 0026 10:33
 * @Version 1.0
 */
@Component
public class HelloListener {
    @KafkaListener(topics = "itcast-topic")
    public void onMessage(String msg) {
        if (!StringUtils.isEmpty(msg)) {
            System.out.println(msg);
        }
    }
}

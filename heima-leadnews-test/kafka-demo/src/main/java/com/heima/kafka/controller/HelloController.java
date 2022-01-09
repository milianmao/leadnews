package com.heima.kafka.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/26 0026 10:30
 * @Version 1.0
 */
@RestController
public class HelloController {
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @GetMapping("/hello")
    public String hello() {
        kafkaTemplate.send("itcast-topic", "你好");
        return "ok";
    }
}

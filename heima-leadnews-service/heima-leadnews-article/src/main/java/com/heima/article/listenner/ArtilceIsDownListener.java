package com.heima.article.listenner;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.service.ApArticleConfigService;
import com.heima.article.service.ApArticleService;
import com.heima.common.constants.WmNewsMessageConstants;
import com.heima.model.article.pojos.ApArticle;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/26 0026 11:00
 * @Version 1.0
 */
@Component
public class ArtilceIsDownListener {
    @Autowired
    private ApArticleConfigService apArticleConfigService;

    @KafkaListener(topics = WmNewsMessageConstants.WM_NEWS_UP_OR_DOWN_TOPIC)
    public void articleUpOrDwon(String msg) {
        if (StringUtils.isNotBlank(msg)) {
            Map map = JSON.parseObject(msg, Map.class);
            apArticleConfigService.updateByMap(map);
        }
        // 解析msg获取消息


    }
}

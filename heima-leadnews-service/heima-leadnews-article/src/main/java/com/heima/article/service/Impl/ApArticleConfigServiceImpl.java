package com.heima.article.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.service.ApArticleConfigService;
import com.heima.model.article.pojos.ApArticleConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/26 0026 11:07
 * @Version 1.0
 */
@Service
@Transactional
public class ApArticleConfigServiceImpl extends ServiceImpl<ApArticleConfigMapper, ApArticleConfig> implements ApArticleConfigService {

    @Override
    public void updateByMap(Map map) {
        Object enable = map.get("enable");
        boolean isDown = true;
        if (enable.equals(1)) {
            isDown = false;
        }
        update(Wrappers.<ApArticleConfig>lambdaUpdate().set(ApArticleConfig::getIsDown, isDown)
                .eq(ApArticleConfig::getArticleId, map.get("articleId")));
    }
}

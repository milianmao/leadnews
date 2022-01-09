package com.heima.article.service.Impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.common.constants.ArticleConstants;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.search.vos.SearchArticleVo;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/24 0024 10:50
 * @Version 1.0
 */
@Service
@Transactional
@Slf4j
public class ArticleFreemarkerServiceImpl implements ArticleFreemarkerService {
    @Autowired
    private Configuration configuration;
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private ApArticleService apArticleService;
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @Async
    @Override
    public void buildArticleToMinIO(ApArticle apArticle, String content) {
        if (StringUtils.isNotBlank(content)) {
            Template template = null;
            StringWriter out = new StringWriter();
            try {
                template = configuration.getTemplate("article.ftl");
                Map<String, Object> contentDataModel = new HashMap<>();
                contentDataModel.put("content", JSONArray.parseArray(content));
                template.process(contentDataModel, out);
            } catch (IOException | TemplateException e) {
                e.printStackTrace();
            }
            InputStream in = new ByteArrayInputStream(out.toString().getBytes());
            // 上传至minio
            String filePath = fileStorageService.uploadHtmlFile("", apArticle.getId() + ".html", in);
            apArticleService.update(Wrappers.<ApArticle>lambdaUpdate().eq(ApArticle::getId, apArticle.getId())
                    .set(ApArticle::getStaticUrl, filePath));
            // 发送消息更新es
            SearchArticleVo searchArticleVo = new SearchArticleVo();
            BeanUtils.copyProperties(apArticle, searchArticleVo);
            searchArticleVo.setContent(content);
            searchArticleVo.setStaticUrl(filePath);
            kafkaTemplate.send(ArticleConstants.ARTICLE_ES_SYNC_TOPIC, JSON.toJSONString(searchArticleVo));
        }
    }
}

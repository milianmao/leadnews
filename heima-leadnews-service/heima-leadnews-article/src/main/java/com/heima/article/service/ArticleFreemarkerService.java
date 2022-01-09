package com.heima.article.service;

import com.heima.model.article.pojos.ApArticle;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/24 0024 10:49
 * @Version 1.0
 */
public interface ArticleFreemarkerService {
    /**
     * @return void
     * @Author milian
     * @Description 生成文字详情静态文件，上传minio
     * @Date 10:49
     * @Param [apArticle, content]
     **/
    public void buildArticleToMinIO(ApArticle apArticle, String content);
}

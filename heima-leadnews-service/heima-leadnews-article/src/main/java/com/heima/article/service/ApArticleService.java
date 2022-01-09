package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/20 0020 15:50
 * @Version 1.0
 */
public interface ApArticleService extends IService<ApArticle> {
    ResponseResult load(Short loadtype, ArticleHomeDto dto);

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description 保存app端文章
     * @Date 14:14
     * @Param [dto]
     **/
    ResponseResult saveArticle(ArticleDto dto);
}

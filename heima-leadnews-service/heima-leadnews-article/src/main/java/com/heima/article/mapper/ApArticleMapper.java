package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author milian
 * @version 1.0
 * @description: 文章
 * @date 2021/11/20 0020
 */
@Mapper
public interface ApArticleMapper extends BaseMapper<ApArticle> {
    /**
     * @return java.util.List<com.heima.model.article.pojos.ApArticle>
     * @Author milian
     * @Description 查询文章列表
     * @Date 16:54
     * @Param [dto, loadtype]dto实体 加载类型
     **/
    List<ApArticle> loadArticleList(@Param("dto") ArticleHomeDto dto, @Param("type") Short type);
}

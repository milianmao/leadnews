package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.NewsAuthDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/21 0021 20:44
 * @Version 1.0
 */
public interface WmNewsService extends IService<WmNews> {
    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description 查询文章列表
     * @Date 10:34
     * @Param [dto]
     **/
    ResponseResult findNewList(WmNewsPageReqDto dto);

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description 提交文章
     * @Date 13:49
     * @Param [dto]
     **/
    ResponseResult submit(WmNewsDto dto);

    /**
     * @return
     * @Author milian
     * @Description 文章下架
     * @Date 10:42
     * @Param
     **/
    public ResponseResult downOrUp(WmNewsDto dto);

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description //TODO 查询文章列表
     * @Date 9:44
     * @Param [newsAuthDto]
     **/
    ResponseResult listVo(NewsAuthDto newsAuthDto);

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description //TODO 查詢文章詳情
     * @Date 14:20
     * @Param [id]
     **/
    ResponseResult newsAuthOne(Long id);

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description //TODO 文章审核驳回
     * @Date 14:31
     * @Param [newsAuthDto]
     **/
    ResponseResult authFail(NewsAuthDto newsAuthDto);

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description //TODO 文章审核通过
     * @Date 15:21
     * @Param [newsAuthDto]
     **/
    ResponseResult authPass(NewsAuthDto newsAuthDto);
}

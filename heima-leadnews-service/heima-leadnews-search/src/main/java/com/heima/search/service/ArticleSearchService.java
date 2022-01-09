package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.UserSearchDto;

import java.io.IOException;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/26 0026 15:31
 * @Version 1.0
 */
public interface ArticleSearchService {
    /**
     * ES文章分页搜索
     *
     * @return
     */
    ResponseResult search(UserSearchDto userSearchDto) throws IOException;
}

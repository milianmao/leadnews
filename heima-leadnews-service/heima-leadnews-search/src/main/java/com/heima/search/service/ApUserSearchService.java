package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.HistorySearchDto;

public interface ApUserSearchService {
    public void insert(String keyword, Integer userId);

    /**
     * 查询搜索历史
     *
     * @return
     */
    ResponseResult findUserSearch();

    ResponseResult delUserSearch(HistorySearchDto historySearchDto);
}

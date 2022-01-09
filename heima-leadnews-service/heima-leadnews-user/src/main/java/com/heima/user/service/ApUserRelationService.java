package com.heima.user.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.UserRelationDto;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/28 0028 16:09
 * @Version 1.0
 */
public interface ApUserRelationService {
    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description //TODO 关注与取消关注
     * @Date 16:13
     * @Param [dto]
     **/
    ResponseResult userFollow(UserRelationDto dto);
}

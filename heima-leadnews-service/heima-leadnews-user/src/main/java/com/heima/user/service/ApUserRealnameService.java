package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.AuthDto;
import com.heima.model.user.pojos.ApUserRealname;

public interface ApUserRealnameService extends IService<ApUserRealname> {
    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description //TODO 用户审核列表
     * @Date 16:55
     * @Param [authDto]
     **/
    ResponseResult userAuthList(AuthDto authDto);

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description //TODO 驳回
     * @Date 16:55
     * @Param [authDto]
     **/
    ResponseResult userAuthFail(AuthDto authDto);

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description //TODO 通过审核
     * @Date 17:04
     * @Param [authDto]
     **/
    ResponseResult userAuthPass(AuthDto authDto);
}

package com.heima.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dtos.AdUserDto;
import com.heima.model.admin.pojos.AdUser;
import com.heima.model.common.dtos.ResponseResult;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/27 0027 10:36
 * @Version 1.0
 */
public interface AdUserService extends IService<AdUser> {
    ResponseResult login(AdUserDto dto);
}

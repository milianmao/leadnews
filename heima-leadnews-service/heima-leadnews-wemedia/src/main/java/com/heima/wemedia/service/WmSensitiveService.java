package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.wemedia.dtos.SensitiveDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmSensitive;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/27 0027 14:40
 * @Version 1.0
 */
public interface WmSensitiveService extends IService<WmSensitive> {
    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description //TODO 搜索敏感词列表
     * @Date 15:14
     * @Param [dto]
     **/
    ResponseResult sensitiveList(SensitiveDto dto);

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description //TODO 保存敏感词
     * @Date 15:14
     * @Param [wmSensitive]
     **/
    ResponseResult sensitiveSave(WmSensitive wmSensitive);

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description //TODO 修改敏感词
     * @Date 15:19
     * @Param [wmSensitive]
     **/
    ResponseResult sensitiveUpdate(WmSensitive wmSensitive);

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description //TODO 删除敏感词
     * @Date 15:24
     * @Param [id]
     **/
    ResponseResult sensitiveDelete(Long id);
}

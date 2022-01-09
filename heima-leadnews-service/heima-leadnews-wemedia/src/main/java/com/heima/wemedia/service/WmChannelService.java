package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.ChannelDto;
import com.heima.model.wemedia.pojos.WmChannel;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/21 0021 20:29
 * @Version 1.0
 */

public interface WmChannelService extends IService<WmChannel> {
    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description 查询所有频道
     * @Date 20:33
     * @Param []
     **/
    ResponseResult findAllChannels();

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description 查看详情
     * @Date 15:47
     * @Param [id]
     **/
    ResponseResult getArticleInfoById(String id);

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description //TODO 频道名称模糊分页查询
     * @Date 15:33
     * @Param [dto]
     **/
    ResponseResult channelsList(ChannelDto dto);

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description //TODO 保存频道
     * @Date 15:41
     * @Param [wmChannel]
     **/
    ResponseResult channelsSave(WmChannel wmChannel);

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description //TODO 更新频道
     * @Date 15:50
     * @Param [wmChannel]
     **/
    ResponseResult channelsUpdate(WmChannel wmChannel);

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description //TODO 删除频道
     * @Date 16:12
     * @Param [id]
     **/
    ResponseResult channelsDelete(Long id);

}

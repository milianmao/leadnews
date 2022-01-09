package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.ChannelDto;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.service.WmChannelService;
import com.heima.wemedia.service.WmNewsService;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/21 0021 20:30
 * @Version 1.0
 */
@Service
public class WmChannelServiceImpl extends ServiceImpl<WmChannelMapper, WmChannel> implements WmChannelService {
    @Autowired
    private WmNewsService wmNewsService;

    @Override
    public ResponseResult findAllChannels() {
        return ResponseResult.okResult(list());
    }

    @Override
    public ResponseResult getArticleInfoById(String id) {
        return null;
    }

    @Override
    public ResponseResult channelsList(ChannelDto dto) {
        dto.checkParam();
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        // 构建查询参数
        LambdaQueryWrapper<WmChannel> lqw = new LambdaQueryWrapper<>();
        Page<WmChannel> page = new Page<>(dto.getPage(), dto.getSize());
        if (StringUtils.isNotBlank(dto.getName())) {
            lqw.like(WmChannel::getName, dto.getName());
        }

        lqw.orderByDesc(WmChannel::getCreatedTime);
        page = page(page, lqw);
        ResponseResult responseResult = new PageResponseResult((int) page.getCurrent(), (int) page.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    @Override
    public ResponseResult channelsSave(WmChannel wmChannel) {
        if (wmChannel == null || StringUtils.isBlank(wmChannel.getName())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        WmChannel one = getOne(Wrappers.<WmChannel>lambdaQuery().eq(WmChannel::getName, wmChannel.getName()));
        if (one != null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_EXIST);
        }
        wmChannel.setCreatedTime(new Date());
        wmChannel.setIsDefault(false);
        save(wmChannel);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult channelsUpdate(WmChannel wmChannel) {
        if (wmChannel == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        // 查询频道是否被引用
        Map<String, Object> map = wmNewsService.getMap(Wrappers.<WmNews>lambdaQuery().eq(WmNews::getChannelId, wmChannel.getId()));
        if (!map.isEmpty() && wmChannel.getStatus() == false) {
            return ResponseResult.errorResult(AppHttpCodeEnum.CHANNEL_DONT_DISABLE);
        }
        WmChannel channel = getById(wmChannel.getId());
        if (channel == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        BeanUtils.copyProperties(wmChannel, channel);
        updateById(channel);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult channelsDelete(Long id) {
        if (id == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        // 查询频道状态
        WmChannel channel = getById(id);
        if (!channel.getStatus().equals("false")) {
            return ResponseResult.errorResult(AppHttpCodeEnum.CHANNEL_DONT_DELETE);
        }
        removeById(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}

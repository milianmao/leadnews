package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.ChannelDto;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.wemedia.service.WmChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/21 0021 20:28
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/v1/channel")
public class ChannelController {
    @Autowired
    private WmChannelService wmChannelService;

    @GetMapping("/channels")
    public ResponseResult findAllChannels() {
        return wmChannelService.findAllChannels();
    }

    @GetMapping("/one/{id}")
    public ResponseResult getArticleInfoById(@PathVariable String id) {
        return wmChannelService.getArticleInfoById(id);
    }

    @PostMapping("/list")
    public ResponseResult channelsList(@RequestBody ChannelDto dto) {
        return wmChannelService.channelsList(dto);
    }

    @PostMapping("/save")
    public ResponseResult channelsSave(@RequestBody WmChannel WmChannel) {
        return wmChannelService.channelsSave(WmChannel);
    }

    @PostMapping("/update")
    public ResponseResult channelsUpdate(@RequestBody WmChannel WmChannel) {
        return wmChannelService.channelsUpdate(WmChannel);
    }

    @DeleteMapping("/del/{id}")
    public ResponseResult channelsDelete(@PathVariable Long id) {
        return wmChannelService.channelsDelete(id);
    }
}
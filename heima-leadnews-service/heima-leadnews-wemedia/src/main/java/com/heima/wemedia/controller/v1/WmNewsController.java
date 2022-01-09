package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.NewsAuthDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.wemedia.service.WmNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/21 0021 20:47
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController {
    @Autowired
    private WmNewsService wmNewsService;

    @PostMapping("/list")
    public ResponseResult findNewList(@RequestBody WmNewsPageReqDto dto) {
        return wmNewsService.findNewList(dto);
    }

    @PostMapping("/submit")
    public ResponseResult submit(@RequestBody WmNewsDto dto) {
        return wmNewsService.submit(dto);
    }

    @PostMapping("/down_or_up")
    public ResponseResult downOrUp(@RequestBody WmNewsDto dto) {
        return wmNewsService.downOrUp(dto);
    }

    @PostMapping("/list_vo")
    public ResponseResult listVo(@RequestBody NewsAuthDto newsAuthDto) {
        return wmNewsService.listVo(newsAuthDto);
    }

    @GetMapping("/one_vo/{id}")
    public ResponseResult newsAuthOne(@PathVariable Long id) {
        return wmNewsService.newsAuthOne(id);
    }

    @PostMapping("/auth_fail")
    public ResponseResult authFail(@RequestBody NewsAuthDto newsAuthDto) {
        return wmNewsService.authFail(newsAuthDto);
    }

    @PostMapping("/auth_pass")
    public ResponseResult authPass(@RequestBody NewsAuthDto newsAuthDto) {
        return wmNewsService.authPass(newsAuthDto);
    }
}

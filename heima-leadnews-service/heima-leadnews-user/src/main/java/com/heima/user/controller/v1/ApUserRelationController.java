package com.heima.user.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.UserRelationDto;
import com.heima.user.service.ApUserRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/28 0028 16:00
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class ApUserRelationController {
    @Autowired
    private ApUserRelationService apUserRelationService;

    @PostMapping("/user_follow")
    public ResponseResult userFollow(@RequestBody UserRelationDto dto) {
        log.info("wdnm");
        return apUserRelationService.userFollow(dto);
    }
}

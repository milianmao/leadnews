package com.heima.user.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.AuthDto;
import com.heima.user.service.ApUserRealnameService;
import com.heima.user.service.ApUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/27 0027 16:29
 * @Version 1.0
 */
@RestController
@RequestMapping("/api/v1/auth")
public class ApUserAuthController {
    @Autowired
    private ApUserRealnameService apUserRealnameService;

    @PostMapping("/list")
    public ResponseResult userAuthList(@RequestBody AuthDto authDto) {
        return apUserRealnameService.userAuthList(authDto);
    }

    @PostMapping("/authFail")
    public ResponseResult userAuthFail(@RequestBody AuthDto authDto) {
        return apUserRealnameService.userAuthFail(authDto);
    }

    @PostMapping("/authPass")
    public ResponseResult userAuthPass(@RequestBody AuthDto authDto) {
        return apUserRealnameService.userAuthPass(authDto);
    }
}

package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.AdUserMapper;
import com.heima.admin.service.AdUserService;
import com.heima.model.admin.dtos.AdUserDto;
import com.heima.model.admin.pojos.AdUser;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.common.AppJwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/27 0027 10:39
 * @Version 1.0
 */
@Service
public class AdUserServiceImpl extends ServiceImpl<AdUserMapper, AdUser> implements AdUserService {
    @Override
    public ResponseResult login(AdUserDto dto) {
        // 参数校验
        if (dto == null || dto.getName() == null || dto.getPassword() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        String name = dto.getName();
        String password = dto.getPassword();
        // 查询用户
        AdUser adUser = getOne(Wrappers.<AdUser>lambdaQuery().eq(AdUser::getName, name));
        if (adUser == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "用户不存在！");
        }
        String pwd = DigestUtils.md5DigestAsHex((password + adUser.getSalt()).getBytes());
        if (!pwd.equals(adUser.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }
        // 登录成功生成token
        String token = AppJwtUtil.getToken(adUser.getId().longValue());
        // 封装响应结果
        adUser.setPassword("");
        adUser.setSalt("");
        HashMap<String, Object> map = new HashMap<>();
        map.put("token", token);
        map.put("user", adUser);
        return ResponseResult.okResult(map);
    }
}

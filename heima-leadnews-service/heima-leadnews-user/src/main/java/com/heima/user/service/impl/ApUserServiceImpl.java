package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.user.dtos.AuthDto;
import com.heima.user.mapper.ApUserMapper;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.LoginDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.AppJwtUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/20 0020 11:22
 * @Version 1.0
 */
@Service
@Transactional
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {
    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description //TODO 登录
     * @Date 11:27
     * @Param [dto]
     **/
    @Override
    public ResponseResult login(LoginDto dto) {
        String phone = dto.getPhone();
        String password = dto.getPassword();
        if (!StringUtils.isBlank(phone) && !StringUtils.isBlank(password)) {
            // 正常登录
            ApUser user = getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, phone));
            if (user == null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "用户不存在");
            }
            String pwd = DigestUtils.md5DigestAsHex((password + user.getSalt()).getBytes());
            if (!pwd.equals(user.getPassword())) {
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
            // 生成token
            String token = AppJwtUtil.getToken(user.getId().longValue());
            user.setPassword("");
            user.setSalt("");
            // 封装数据
            HashMap<String, Object> map = new HashMap(2);
            map.put("token", token);
            map.put("user", user);
            return ResponseResult.okResult(map);
        } else {
            // 游客登录
            HashMap<String, Object> map = new HashMap(1);
            map.put("token", AppJwtUtil.getToken(0L));
            return ResponseResult.okResult(map);
        }

    }


}

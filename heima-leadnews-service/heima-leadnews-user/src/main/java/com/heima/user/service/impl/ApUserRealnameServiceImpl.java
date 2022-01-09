package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.ApUserRealnameConstants;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.AuthDto;
import com.heima.model.user.pojos.ApUserRealname;
import com.heima.user.mapper.ApUserRealnameMapper;
import com.heima.user.service.ApUserRealnameService;
import org.springframework.stereotype.Service;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/27 0027 16:41
 * @Version 1.0
 */
@Service
public class ApUserRealnameServiceImpl extends ServiceImpl<ApUserRealnameMapper, ApUserRealname> implements ApUserRealnameService {
    @Override
    public ResponseResult userAuthList(AuthDto authDto) {
        if (authDto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        authDto.checkParam();
        // 构建查询条件
        LambdaQueryWrapper<ApUserRealname> lqw = new LambdaQueryWrapper<>();
        IPage<ApUserRealname> page = new Page<>(authDto.getPage(), authDto.getSize());
        if (authDto.getStatus() != null) {
            lqw.eq(ApUserRealname::getStatus, authDto.getStatus());
        }
        page = page(page, lqw);
        ResponseResult responseResult = new PageResponseResult((int) page.getCurrent(), (int) page.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    @Override
    public ResponseResult userAuthFail(AuthDto authDto) {
        if (authDto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        // 查询
        ApUserRealname userRealname = getById(authDto.getId());
        userRealname.setStatus(ApUserRealnameConstants.APUSERNAME_FAIL);
        userRealname.setReason(authDto.getMsg());
        updateById(userRealname);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult userAuthPass(AuthDto authDto) {
        if (authDto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        // 查询
        ApUserRealname userRealname = getById(authDto.getId());
        userRealname.setStatus(ApUserRealnameConstants.APUSERNAME_PASS);
        updateById(userRealname);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}

package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.wemedia.dtos.SensitiveDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.service.WmSensitiveService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/27 0027 14:41
 * @Version 1.0
 */
@Service
public class WmSensitiveServiceImpl extends ServiceImpl<WmSensitiveMapper, WmSensitive> implements WmSensitiveService {
    @Override
    public ResponseResult sensitiveList(SensitiveDto dto) {
        // 参数校验
        if (dto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        dto.checkParam();
        IPage<WmSensitive> page = new Page<>(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmSensitive> lqw = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(dto.getName())) {
            lqw.like(WmSensitive::getSensitives, dto.getName());
        }
        lqw.orderByDesc(WmSensitive::getCreatedTime);
        page = page(page, lqw);
        ResponseResult responseResult = new PageResponseResult((int) page.getPages(), (int) page.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    @Override
    public ResponseResult sensitiveSave(WmSensitive wmSensitive) {
        // 效验参数
        if (wmSensitive == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        // 查询敏感词是否存在
        WmSensitive sensitive = getOne(Wrappers.<WmSensitive>lambdaQuery().eq(WmSensitive::getSensitives, wmSensitive.getSensitives()));
        if (sensitive != null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_EXIST);
        }
        wmSensitive.setCreatedTime(new Date());
        save(wmSensitive);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult sensitiveUpdate(WmSensitive wmSensitive) {
        // 效验参数
        if (wmSensitive == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }

        wmSensitive.setCreatedTime(new Date());
        updateById(wmSensitive);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult sensitiveDelete(Long id) {
        if (id == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        removeById(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}

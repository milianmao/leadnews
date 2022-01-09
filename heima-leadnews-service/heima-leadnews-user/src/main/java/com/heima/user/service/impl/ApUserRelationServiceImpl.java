package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.common.exception.CustomException;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.UserRelationDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.pojos.ApUserFan;
import com.heima.model.user.pojos.ApUserFollow;
import com.heima.user.mapper.ApUserFanMapper;
import com.heima.user.mapper.ApUserFollowMapper;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserRelationService;
import com.heima.user.service.ApUserService;
import com.heima.utils.common.ApUserThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/28 0028 16:09
 * @Version 1.0
 */
@Service
@Transactional
public class ApUserRelationServiceImpl implements ApUserRelationService {
    @Autowired
    private ApUserService apUserService;
    @Autowired
    private ApUserMapper apUserMapper;
    @Autowired
    private ApUserFanMapper apUserFanMapper;
    @Autowired
    private ApUserFollowMapper apUserFollowMapper;

    @Override
    public ResponseResult userFollow(UserRelationDto dto) {
        if (dto == null || dto.getAuthorId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_REQUIRE);
        }
        if (dto.getOperation() == (short) 0) {
            saveFollow(dto);
        } else if (dto.getOperation() == (short) 1) {
            apUserFollowMapper.delete(Wrappers.<ApUserFollow>lambdaQuery().eq(ApUserFollow::getFollowId, dto.getAuthorId()));
            apUserFanMapper.delete(Wrappers.<ApUserFan>lambdaQuery().eq(ApUserFan::getFansId, ApUserThreadLocalUtil.getUser().getId()));
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    private void saveFollow(UserRelationDto dto) {
        ApUserFollow one = apUserFollowMapper.selectOne(Wrappers.<ApUserFollow>lambdaQuery()
                .eq(ApUserFollow::getUserId, ApUserThreadLocalUtil.getUser().getId())
                .eq(ApUserFollow::getFollowId, dto.getAuthorId())
        );
        if (one != null) {
            throw new CustomException(AppHttpCodeEnum.DATA_EXIST);
        }

        // 查询作者信息
        ApUser user = apUserService.getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getId, dto.getAuthorId()));
        // 查询用户信息
        ApUser apUser = apUserMapper.selectOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getId, ApUserThreadLocalUtil.getUser().getId()));
        // 构建双向实体
        ApUserFollow apUserFollow = new ApUserFollow();
        apUserFollow.setFollowId(dto.getAuthorId());
        apUserFollow.setFollowName(user.getName());
        apUserFollow.setUserId(ApUserThreadLocalUtil.getUser().getId());
        apUserFollow.setCreatedTime(new Date());
        apUserFollowMapper.insert(apUserFollow);
        ApUserFan apUserFan = new ApUserFan();
        apUserFan.setUserId(dto.getAuthorId());
        apUserFan.setFansId(ApUserThreadLocalUtil.getUser().getId());
        apUserFan.setFansName(apUser.getName());
        apUserFan.setCreatedTime(new Date());
        apUserFanMapper.insert(apUserFan);
    }
}

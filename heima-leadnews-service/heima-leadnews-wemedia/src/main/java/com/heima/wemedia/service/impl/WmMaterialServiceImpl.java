package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.WemediaConstants;
import com.heima.file.service.FileStorageService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.utils.common.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import io.minio.MinioClient;
import javafx.scene.paint.Material;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/21 0021 19:41
 * @Version 1.0
 */
@Service
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {
    @Autowired
    private FileStorageService fileStorageService;
    @Autowired
    private WmNewsMaterialMapper wmNewsMaterialMapper;

    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        // 检查参数
        if (multipartFile == null || multipartFile.getSize() == 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // 上传图片
        String filename = UUID.randomUUID().toString().replace("-", "");
        String originalFilename = multipartFile.getOriginalFilename();
        String postfix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String url = null;
        try {
            url = fileStorageService.uploadImgFile("", filename + postfix, multipartFile.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        }
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUrl(url);
        wmMaterial.setIsCollection((short) 0);
        wmMaterial.setType((short) 0);
        wmMaterial.setCreatedTime(new Date());
        wmMaterial.setUserId(WmThreadLocalUtil.getUser().getId());
        save(wmMaterial);
        return ResponseResult.okResult(wmMaterial);
    }

    @Override
    public ResponseResult list(WmMaterialDto dto) {
        // 效验参数
        dto.checkParam();
        Short isCollection = dto.getIsCollection() == null ? (short) 0 : dto.getIsCollection();
        if (isCollection != (short) 0 && isCollection != (short) 1) {
            isCollection = (short) 0;
        }
        // 建立查询条件
        IPage page = new Page(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmMaterial> lqw = new LambdaQueryWrapper();
        lqw.eq(WmMaterial::getUserId, WmThreadLocalUtil.getUser().getId());
        if (isCollection == (short) 1) {
            lqw.eq(WmMaterial::getIsCollection, isCollection);
        }
        lqw.orderByDesc(WmMaterial::getCreatedTime);
        IPage iPage = page(page, lqw);
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    @Override
    public ResponseResult delPicture(Integer id) {
        // 查询图片路径
        WmMaterial wmMaterial = getOne(Wrappers.<WmMaterial>lambdaQuery().eq(WmMaterial::getId, id));
        if (wmMaterial == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        // 查询图片是否被引用
        WmNewsMaterial wmNewsMaterial = wmNewsMaterialMapper.selectOne(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getMaterialId, id));
        if (wmNewsMaterial != null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.MATERIASL_DONT_DELETE_FAIL);
        }
        String url = wmMaterial.getUrl();
        // 删除minio的图片
        try {
            fileStorageService.delete(url);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.errorResult(AppHttpCodeEnum.MATERIASL_DELETE_FAIL);
        }
        // 删除数据库数据
        removeById(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult Collect(Integer id) {
        WmMaterial material = getOne(Wrappers.<WmMaterial>lambdaQuery().eq(WmMaterial::getId, id));
        if (material == null) {
            return ResponseResult.errorResult(501, "参数失效");
        }
        material.setIsCollection(WemediaConstants.COLLECT_MATERIAL);
        save(material);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult cancelCollect(Integer id) {
        WmMaterial material = getOne(Wrappers.<WmMaterial>lambdaQuery().eq(WmMaterial::getId, id));
        if (material == null) {
            return ResponseResult.errorResult(501, "参数失效");
        }
        material.setIsCollection(WemediaConstants.CANCEL_COLLECT_MATERIAL);
        save(material);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}

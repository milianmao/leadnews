package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

public interface WmMaterialService extends IService<WmMaterial> {
    /**
     * 图片上传
     *
     * @param multipartFile
     * @return
     */
    public ResponseResult uploadPicture(MultipartFile multipartFile);

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description 素材查询
     * @Date 20:02
     * @Param [dto]
     **/
    public ResponseResult list(WmMaterialDto dto);

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description 删除素材库图片
     * @Date 15:09
     * @Param [id]
     **/
    ResponseResult delPicture(Integer id);

    /**
     * @return com.heima.model.common.dtos.ResponseResult
     * @Author milian
     * @Description 收藏与取消收藏
     * @Date 15:35
     * @Param [id]
     **/
    ResponseResult Collect(Integer id);

    ResponseResult cancelCollect(Integer id);
}

package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.heima.model.wemedia.dtos.NewsAuthDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.vos.NewsAuthVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WmNewsMapper extends BaseMapper<WmNews> {
    IPage<NewsAuthVo> getListVo(IPage<NewsAuthVo> page, @Param("dto") NewsAuthDto newsAuthDto);

    NewsAuthVo getNewsAuthOne(@Param("id") Long id);
}

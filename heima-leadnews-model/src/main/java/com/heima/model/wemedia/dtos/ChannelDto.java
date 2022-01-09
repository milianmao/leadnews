package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/27 0027 15:31
 * @Version 1.0
 */
@Data
public class ChannelDto extends PageRequestDto {
    private String name;
}

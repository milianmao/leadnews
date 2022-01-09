package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/28 0028 9:40
 * @Version 1.0
 */
@Data
public class NewsAuthDto extends PageRequestDto {
    private Integer id;
    private String msg;
    private Integer status;
    private String title;
}

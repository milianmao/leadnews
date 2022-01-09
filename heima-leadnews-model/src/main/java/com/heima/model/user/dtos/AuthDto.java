package com.heima.model.user.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/27 0027 16:32
 * @Version 1.0
 */
@Data
public class AuthDto extends PageRequestDto {
    private Integer id;
    private String msg;
    private Integer status;
}

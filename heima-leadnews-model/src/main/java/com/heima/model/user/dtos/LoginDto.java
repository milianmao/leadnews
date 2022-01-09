package com.heima.model.user.dtos;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/20 0020 11:11
 * @Version 1.0
 */
@Data
public class LoginDto {
    /**
     * 手机号
     */
    @ApiModelProperty(value = "手机号", required = true)
    private String phone;
    /**
     * 密码
     */
    @ApiModelProperty(value = "密码", required = true)
    private String password;
}

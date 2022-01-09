package com.heima.model.user.dtos;

import lombok.Data;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/28 0028 16:05
 * @Version 1.0
 */
@Data
public class UserRelationDto {
    private Long articleId;
    private Integer authorId;
    private Short operation;
}

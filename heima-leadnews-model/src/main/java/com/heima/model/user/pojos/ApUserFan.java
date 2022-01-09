package com.heima.model.user.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.Date;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/28 0028 18:27
 * @Version 1.0
 */
@Data
public class ApUserFan {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 用户id
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 粉丝id
     */
    @TableField("fans_id")
    private Integer fansId;

    /**
     * 粉丝昵称
     */
    @TableField("fans_name")
    private String fansName;

    /**
     * 粉丝忠实度
     */
    @TableField("level")
    private short level = (short) 0;

    /**
     * 是否看见我动态
     */
    @TableField("is_display")
    private Boolean isDisplay = false;

    /**
     * 是否屏蔽私信
     */
    @TableField("is_shield_letter")
    private Boolean isShieldLetter = false;

    /**
     * 是否屏蔽评论
     */
    @TableField("is_shield_comment")
    private Boolean isShieldComment = false;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createdTime;
}

package com.heima.model.user.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/28 0028 18:13
 * @Version 1.0
 */
@Data
public class ApUserFollow implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 用户id
     */
    @TableField("user_id")
    private Integer userId;
    /**
     * 关注作者id
     */
    @TableField("follow_id")
    private Integer followId;

    /**
     * 作者昵称
     */
    @TableField("follow_name")
    private String followName;

    /**
     * 关注度
     */
    @TableField("level")
    private short level = (short) 0;

    /**
     * 是否动态通知
     */
    @TableField("is_notice")
    private Boolean isNotice = true;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createdTime;

}

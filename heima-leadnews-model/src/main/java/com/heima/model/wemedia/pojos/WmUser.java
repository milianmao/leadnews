package com.heima.model.wemedia.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author milian
 * @Description
 * @Date 2021/11/21 0021 16:46
 * @Version 1.0
 */
@Data
@TableName("wm_user")
public class WmUser {
    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("ap_user_id")
    private Integer userId;

    @TableField("ap_author_id")
    private Integer authorId;

    /**
     * 密码、通信等加密盐
     */
    @TableField("salt")
    private String salt;

    /**
     * 用户名
     */
    @TableField("name")
    private String name;

    /**
     * 密码,md5加密
     */
    @TableField("password")
    private String password;

    /**
     * 手机号
     */
    @TableField("phone")
    private String phone;

    /**
     * 头像
     */
    @TableField("image")
    private String image;

    /**
     * 昵称
     */
    @TableField("nickname")
    private String nickName;
    /**
     * 归属地
     */
    @TableField("location")
    private String location;

    /**
     * 状态
     */
    @TableField("status")
    private Integer status;
    /**
     * 邮件
     */
    @TableField("email")
    private String email;
    /**
     * 账号类型
     */
    @TableField("type")
    private Integer type;
    /**
     * 运营评分
     */
    @TableField("score")
    private Integer score;
    /**
     * 最后一次登录时间
     */
    @TableField("login_time")
    private Date loginTime;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createdTime;
}
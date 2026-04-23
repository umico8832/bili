package com.bili.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("user")
public class User {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String nickname;

    private String avatar;

    private String signature;

    /** 是否认证 UP 主：false-否 true-是 */
    private Boolean verified;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

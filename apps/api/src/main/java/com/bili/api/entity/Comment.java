package com.bili.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论实体
 */
@Data
@TableName("comment")
public class Comment {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long videoId;

    private Long userId;

    /** 父评论 ID，null 表示一级评论 */
    private Long parentId;

    private String content;

    private Long likeCount;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

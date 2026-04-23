package com.bili.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 视频合集实体
 */
@Data
@TableName("video")
public class Video {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private String title;

    private String cover;

    /** 作者 user.id */
    private Long authorId;

    private Integer categoryId;

    /** 分区名（冗余字段） */
    private String categoryName;

    private String description;

    /** 总时长（秒） */
    private Integer durationSec;

    private Long playCount;

    private Long danmakuCount;

    private Long likeCount;

    /** 状态：0-待审核 1-已发布 2-已下架 */
    private Integer status;

    @TableLogic
    private Integer deleted;

    private LocalDateTime publishedAt;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

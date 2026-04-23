package com.bili.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 视频分集实体
 */
@Data
@TableName("video_episode")
public class VideoEpisode {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /** 所属合集 video.id */
    private Long videoId;

    /** 分集序号，从 1 开始 */
    private Integer epIndex;

    private String title;

    /** 视频源 URL */
    private String src;

    /** 分集封面（null 则用合集封面） */
    private String cover;

    /** 时长（秒） */
    private Integer durationSec;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}

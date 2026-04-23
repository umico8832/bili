package com.bili.api.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 弹幕实体
 */
@Data
@TableName("danmaku")
public class Danmaku {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long videoId;

    private Long episodeId;

    private Long userId;

    /** 出现时间点（秒，支持小数） */
    private Double timeSec;

    private String content;

    /** 颜色 hex，如 #ffffff */
    private String color;

    /**
     * 弹幕模式：1-滚动 2-顶部 3-底部
     */
    private Integer mode;

    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}

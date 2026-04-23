package com.bili.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 合并分片请求体
 */
@Data
public class MergeRequest {

    /** 文件 MD5（全量 Hash，用于唯一标识文件） */
    @NotBlank(message = "md5 不能为空")
    private String md5;

    /** 原始文件名（含扩展名） */
    @NotBlank(message = "filename 不能为空")
    private String filename;

    /** 总分片数 */
    @NotNull(message = "totalChunks 不能为空")
    @Min(value = 1, message = "totalChunks 至少为 1")
    private Integer totalChunks;

    /** 关联的视频合集 ID（video.id） */
    @NotNull(message = "videoId 不能为空")
    private Long videoId;

    /** 分集序号（从 1 开始） */
    @NotNull(message = "epIndex 不能为空")
    @Min(value = 1, message = "epIndex 至少为 1")
    private Integer epIndex;

    /** 分集标题 */
    @NotBlank(message = "title 不能为空")
    private String title;
}

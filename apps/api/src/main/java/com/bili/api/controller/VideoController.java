package com.bili.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.bili.api.common.Result;
import com.bili.api.dto.VideoVO;
import com.bili.api.service.VideoService;
import org.springframework.web.bind.annotation.*;

/**
 * 视频合集接口
 *
 * <pre>
 * GET /api/videos             首页视频分页列表
 *   ?page=1&size=20           分页参数（默认 page=1, size=20）
 *   &categoryId=1             分区过滤（可选）
 *   &keyword=vue              全文检索（可选）
 *
 * GET /api/videos/{id}        （占位）视频详情（下一阶段实现）
 * </pre>
 */
@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    /**
     * 首页视频分页列表
     *
     * @param page       页码，从 1 开始，默认 1
     * @param size       每页条数，默认 20，最大 50
     * @param categoryId 分区 ID，null 表示全部
     * @param keyword    搜索关键词，null 表示不过滤
     */
    @GetMapping
    public Result<IPage<VideoVO>> list(
            @RequestParam(defaultValue = "1")  int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false)    Integer categoryId,
            @RequestParam(required = false)    String keyword
    ) {
        // 防止恶意大 size
        if (size > 50) size = 50;
        IPage<VideoVO> data = videoService.pageVideoVO(page, size, categoryId, keyword);
        return Result.ok(data);
    }

    /**
     * 视频详情（占位，下一阶段实现）
     */
    @GetMapping("/{id}")
    public Result<VideoVO> detail(@PathVariable Long id) {
        // TODO: 实现详情查询（含分集列表）
        return Result.fail(501, "尚未实现，敬请期待");
    }
}

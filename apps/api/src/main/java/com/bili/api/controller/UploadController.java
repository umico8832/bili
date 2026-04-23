package com.bili.api.controller;

import com.bili.api.common.Result;
import com.bili.api.dto.MergeRequest;
import com.bili.api.entity.VideoEpisode;
import com.bili.api.mapper.VideoEpisodeMapper;
import com.bili.api.service.FileStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 大文件分片上传接口
 *
 * <pre>
 * GET  /api/upload/check              秒传校验（根据 MD5 判断文件是否已存在）
 * POST /api/upload/chunk              上传单个分片
 * POST /api/upload/merge              合并分片 + 写入 video_episode 记录
 * </pre>
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {

    private final FileStorageService fileStorageService;
    private final VideoEpisodeMapper videoEpisodeMapper;

    /**
     * 秒传校验
     *
     * @param md5      文件完整 MD5
     * @param filename 原始文件名（含扩展名）
     * @return { exist: true, url: "..." } 或 { exist: false }
     */
    @GetMapping("/check")
    public Result<Map<String, Object>> check(
            @RequestParam String md5,
            @RequestParam String filename) {

        String url = fileStorageService.checkExist(md5, filename);
        Map<String, Object> result = new HashMap<>();
        if (url != null) {
            result.put("exist", true);
            result.put("url", url);
            log.info("秒传命中: md5={}, url={}", md5, url);
        } else {
            result.put("exist", false);
        }
        return Result.ok(result);
    }

    /**
     * 上传单个分片
     *
     * @param md5        文件完整 MD5（用于目录隔离）
     * @param chunkIndex 当前分片序号（从 0 开始）
     * @param file       分片二进制数据
     */
    @PostMapping("/chunk")
    public Result<Void> uploadChunk(
            @RequestParam String md5,
            @RequestParam int chunkIndex,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return Result.fail(400, "分片内容为空");
        }
        try {
            fileStorageService.saveChunk(md5, chunkIndex, file);
            return Result.ok();
        } catch (IOException e) {
            log.error("分片上传失败: md5={}, index={}", md5, chunkIndex, e);
            return Result.error("分片上传失败: " + e.getMessage());
        }
    }

    /**
     * 合并分片并将记录写入 video_episode 表
     *
     * @param req 合并请求（含 md5、filename、totalChunks、videoId、epIndex、title）
     */
    @PostMapping("/merge")
    public Result<Map<String, Object>> mergeChunks(@Valid @RequestBody MergeRequest req) {
        try {
            // 1. 合并分片文件
            String videoUrl = fileStorageService.mergeChunks(
                    req.getMd5(), req.getFilename(), req.getTotalChunks());

            // 2. 获取文件大小
            long fileSize = fileStorageService.getFileSize(req.getMd5(), req.getFilename());

            // 3. 写入 video_episode 记录
            LocalDateTime now = LocalDateTime.now();
            VideoEpisode episode = new VideoEpisode();
            episode.setVideoId(req.getVideoId());
            episode.setEpIndex(req.getEpIndex());
            episode.setTitle(req.getTitle());
            episode.setSrc(videoUrl);
            episode.setDurationSec(0); // 服务端无法直接解析视频时长，默认 0，后续可异步补充
            episode.setCreatedAt(now);
            episode.setUpdatedAt(now);

            videoEpisodeMapper.insert(episode);

            log.info("分集已入库: videoId={}, epIndex={}, url={}", req.getVideoId(), req.getEpIndex(), videoUrl);

            // 4. 返回结果
            Map<String, Object> data = new HashMap<>();
            data.put("url", videoUrl);
            data.put("size", fileSize);
            data.put("episodeId", episode.getId());
            return Result.ok(data);

        } catch (IllegalStateException e) {
            log.warn("合并失败（分片不完整）: {}", e.getMessage());
            return Result.fail(400, e.getMessage());
        } catch (Exception e) {
            log.error("合并分片失败: md5={}", req.getMd5(), e);
            return Result.error("合并失败: " + e.getMessage());
        }
    }
}

package com.bili.api.service;

import com.bili.api.config.UploadProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Comparator;

/**
 * 文件存储服务
 *
 * <p>职责：</p>
 * <ul>
 *   <li>分片暂存：{rootDir}/chunks/{md5}/{chunkIndex}</li>
 *   <li>文件合并：{rootDir}/videos/{md5}.{ext}</li>
 *   <li>秒传校验：判断最终文件是否已存在</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final UploadProperties uploadProperties;

    // ----------------------------------------------------------------
    // 公开 API
    // ----------------------------------------------------------------

    /**
     * 校验文件是否已存在（秒传）
     *
     * @param md5      文件 MD5
     * @param filename 原始文件名（用于推断扩展名）
     * @return 若文件已存在，返回可访问的相对路径；否则返回 null
     */
    public String checkExist(String md5, String filename) {
        String ext = getExtension(filename);
        Path target = getVideoPath(md5, ext);
        if (Files.exists(target)) {
            // 返回相对于 rootDir 的 URL 路径，供前端访问
            return "/uploads/videos/" + md5 + (ext.isEmpty() ? "" : "." + ext);
        }
        return null;
    }

    /**
     * 保存单个分片到临时目录
     *
     * @param md5        文件 MD5（用作目录名）
     * @param chunkIndex 分片序号（从 0 开始）
     * @param file       分片文件
     */
    public void saveChunk(String md5, int chunkIndex, MultipartFile file) throws IOException {
        Path chunkDir = getChunkDir(md5);
        Files.createDirectories(chunkDir);
        Path chunkFile = chunkDir.resolve(String.valueOf(chunkIndex));
        file.transferTo(chunkFile.toFile());
        log.info("分片已保存: md5={}, index={}, size={}", md5, chunkIndex, file.getSize());
    }

    /**
     * 合并所有分片为完整文件
     *
     * @param md5        文件 MD5
     * @param filename   原始文件名（用于推断扩展名）
     * @param totalChunks 总分片数
     * @return 合并后文件的相对 URL 路径
     */
    public String mergeChunks(String md5, String filename, int totalChunks) throws IOException {
        String ext = getExtension(filename);
        Path targetDir = getVideoDir();
        Files.createDirectories(targetDir);
        Path target = getVideoPath(md5, ext);

        // 如果已存在（并发合并保护），直接返回
        if (Files.exists(target)) {
            log.info("文件已存在，跳过合并: {}", target);
            return buildVideoUrl(md5, ext);
        }

        Path chunkDir = getChunkDir(md5);

        // 按序号排序分片文件
        File[] chunks = chunkDir.toFile().listFiles();
        if (chunks == null || chunks.length != totalChunks) {
            throw new IllegalStateException(
                    "分片数量不匹配: 期望=" + totalChunks + ", 实际=" + (chunks == null ? 0 : chunks.length));
        }
        Arrays.sort(chunks, Comparator.comparingInt(f -> Integer.parseInt(f.getName())));

        // 顺序写入合并文件
        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(target))) {
            for (File chunk : chunks) {
                Files.copy(chunk.toPath(), out);
            }
        }

        log.info("文件合并完成: md5={}, path={}, size={}B", md5, target, Files.size(target));

        // 清理临时分片目录
        deleteDirectory(chunkDir);

        return buildVideoUrl(md5, ext);
    }

    /**
     * 获取合并后文件的大小（字节）
     */
    public long getFileSize(String md5, String filename) throws IOException {
        String ext = getExtension(filename);
        Path target = getVideoPath(md5, ext);
        return Files.exists(target) ? Files.size(target) : 0L;
    }

    // ----------------------------------------------------------------
    // 路径工具方法
    // ----------------------------------------------------------------

    private Path getRootDir() {
        return Paths.get(uploadProperties.getRootDir()).toAbsolutePath();
    }

    private Path getChunkDir(String md5) {
        return getRootDir().resolve("chunks").resolve(md5);
    }

    private Path getVideoDir() {
        return getRootDir().resolve("videos");
    }

    private Path getVideoPath(String md5, String ext) {
        String fileName = ext.isEmpty() ? md5 : md5 + "." + ext;
        return getVideoDir().resolve(fileName);
    }

    private String buildVideoUrl(String md5, String ext) {
        return "/uploads/videos/" + md5 + (ext.isEmpty() ? "" : "." + ext);
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }

    /** 递归删除目录 */
    private void deleteDirectory(Path dir) {
        try {
            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            log.warn("临时分片目录清理失败: {}", dir, e);
        }
    }
}

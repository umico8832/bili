package com.bili.api.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 文件上传配置属性
 * 对应 application.yml 中的 upload.* 节点
 */
@Data
@Component
@ConfigurationProperties(prefix = "upload")
public class UploadProperties {

    /** 上传根目录（相对于项目运行目录） */
    private String rootDir = "uploads";

    /** 单个分片大小（字节），默认 5MB */
    private long chunkSize = 5242880L;
}

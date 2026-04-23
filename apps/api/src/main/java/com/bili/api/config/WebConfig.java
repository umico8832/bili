package com.bili.api.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * Spring MVC 全局配置
 *
 * <p>通过 {@link WebMvcConfigurer#addCorsMappings} 注册 CORS 规则，
 * 允许前端开发服务器（默认 http://localhost:5173）跨域访问所有 /api/** 接口。</p>
 *
 * <p>同时将磁盘上的 uploads/ 目录映射为静态资源，可通过 /uploads/** 访问。</p>
 *
 * <p>生产环境上线前，请将 {@code allowedOrigins} 替换为实际域名。</p>
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final UploadProperties uploadProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                // 前端 Vite 开发服务器地址
                .allowedOrigins(
                        "http://localhost:5173",
                        "http://127.0.0.1:5173"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 将磁盘上的 uploads/ 目录映射为可通过 /uploads/** 访问的静态资源
     * 例如：/uploads/videos/abc123.mp4 -> {rootDir}/videos/abc123.mp4
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Paths.get(uploadProperties.getRootDir())
                .toAbsolutePath()
                .toUri()
                .toString();
        // 确保路径末尾有斜杠
        if (!uploadPath.endsWith("/")) {
            uploadPath = uploadPath + "/";
        }
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}

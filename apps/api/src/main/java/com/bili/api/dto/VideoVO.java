package com.bili.api.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 视频列表 / 详情 VO
 *
 * <p>字段结构与前端 TypeScript {@code Video} 接口完全对齐：
 * <ul>
 *   <li>{@code author}   → {@code UpUser}（嵌套对象）</li>
 *   <li>{@code category} → {@code Category}（嵌套对象）</li>
 * </ul>
 * Jackson 序列化后可直接被前端消费，无需任何转换。
 * </p>
 */
@Data
public class VideoVO {

    /** 稿件 id（bvid 风格，Long 序列化为 String 避免 JS 精度丢失） */
    private String id;

    private String title;

    private String cover;

    /** 作者信息（对应前端 UpUser） */
    private AuthorDTO author;

    /** 分区信息（对应前端 Category） */
    private CategoryDTO category;

    private String description;

    /** 总时长（秒） */
    private Integer durationSec;

    private Long playCount;

    private Long danmakuCount;

    private Long likeCount;

    /** 发布时间（Jackson 序列化为 "yyyy-MM-dd HH:mm:ss"） */
    private LocalDateTime publishedAt;

    // ─── 内嵌 DTO ──────────────────────────────────────────

    /** 对应前端 UpUser 接口 */
    @Data
    public static class AuthorDTO {
        private String id;
        private String nickname;
        private String avatar;
        private String signature;
        private Boolean verified;
    }

    /** 对应前端 Category 接口 */
    @Data
    public static class CategoryDTO {
        private String id;
        private String name;
        /** 顶级分区 parentId 为 null */
        private String parentId;
    }
}

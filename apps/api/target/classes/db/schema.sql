-- =============================================================
-- Bili 视频平台 · 核心表结构
-- 字符集：utf8mb4 + utf8mb4_unicode_ci（完整 Emoji 支持）
-- 执行前请先创建数据库：CREATE DATABASE bili CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- =============================================================

-- -----------------------------------------------------------
-- 1. 用户表 (user)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `user`
(
    `id`         BIGINT       NOT NULL COMMENT '雪花 ID',
    `nickname`   VARCHAR(64)  NOT NULL COMMENT '昵称',
    `avatar`     VARCHAR(512) NOT NULL DEFAULT '' COMMENT '头像 URL',
    `signature`  VARCHAR(256)          DEFAULT NULL COMMENT '个性签名',
    `verified`   TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否认证UP主：0-否 1-是',
    `deleted`    TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常 1-已删',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_nickname` (`nickname`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '用户表';

-- -----------------------------------------------------------
-- 2. 视频合集表 (video)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `video`
(
    `id`            BIGINT       NOT NULL COMMENT '雪花 ID（BVID 风格）',
    `title`         VARCHAR(256) NOT NULL COMMENT '标题',
    `cover`         VARCHAR(512) NOT NULL DEFAULT '' COMMENT '封面图 URL',
    `author_id`     BIGINT       NOT NULL COMMENT '作者 user.id',
    `category_id`   INT          NOT NULL DEFAULT 0 COMMENT '分区 ID（对应字典）',
    `category_name` VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '分区名称（冗余，避免频繁 JOIN）',
    `description`   TEXT                  DEFAULT NULL COMMENT '简介',
    `duration_sec`  INT          NOT NULL DEFAULT 0 COMMENT '总时长（秒）',
    `play_count`    BIGINT       NOT NULL DEFAULT 0 COMMENT '播放量',
    `danmaku_count` BIGINT       NOT NULL DEFAULT 0 COMMENT '弹幕数',
    `like_count`    BIGINT       NOT NULL DEFAULT 0 COMMENT '点赞数',
    `status`        TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '状态：0-待审核 1-已发布 2-已下架',
    `deleted`       TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常 1-已删',
    `published_at`  DATETIME              DEFAULT NULL COMMENT '发布时间',
    `created_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_author_id` (`author_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_published_at` (`published_at`),
    KEY `idx_play_count` (`play_count`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '视频合集表';

-- -----------------------------------------------------------
-- 3. 分集表 (video_episode)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `video_episode`
(
    `id`           BIGINT       NOT NULL COMMENT '雪花 ID',
    `video_id`     BIGINT       NOT NULL COMMENT '所属合集 video.id',
    `ep_index`     INT          NOT NULL DEFAULT 1 COMMENT '分集序号，从 1 开始',
    `title`        VARCHAR(256) NOT NULL COMMENT '分集标题',
    `src`          VARCHAR(512) NOT NULL DEFAULT '' COMMENT '视频源 URL（mp4/m3u8）',
    `cover`        VARCHAR(512)          DEFAULT NULL COMMENT '分集封面，NULL 则回退到合集封面',
    `duration_sec` INT          NOT NULL DEFAULT 0 COMMENT '时长（秒）',
    `deleted`      TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常 1-已删',
    `created_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_video_id` (`video_id`),
    UNIQUE KEY `uk_video_ep_index` (`video_id`, `ep_index`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '视频分集表';

-- -----------------------------------------------------------
-- 4. 弹幕表 (danmaku)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `danmaku`
(
    `id`         BIGINT       NOT NULL COMMENT '雪花 ID',
    `video_id`   BIGINT       NOT NULL COMMENT '所属合集 video.id',
    `episode_id` BIGINT       NOT NULL COMMENT '所属分集 video_episode.id',
    `user_id`    BIGINT       NOT NULL COMMENT '发送者 user.id',
    `time_sec`   DOUBLE       NOT NULL DEFAULT 0 COMMENT '出现时间点（秒，支持小数）',
    `content`    VARCHAR(200) NOT NULL COMMENT '弹幕内容',
    `color`      VARCHAR(7)   NOT NULL DEFAULT '#ffffff' COMMENT '颜色 hex，如 #ffffff',
    `mode`       TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '模式：1-滚动 2-顶部 3-底部',
    `deleted`    TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常 1-已删',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    PRIMARY KEY (`id`),
    KEY `idx_episode_id` (`episode_id`),
    KEY `idx_video_id` (`video_id`),
    KEY `idx_user_id` (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '弹幕表';

-- -----------------------------------------------------------
-- 5. 评论表 (comment)
-- -----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `comment`
(
    `id`         BIGINT   NOT NULL COMMENT '雪花 ID',
    `video_id`   BIGINT   NOT NULL COMMENT '所属合集 video.id',
    `user_id`    BIGINT   NOT NULL COMMENT '评论者 user.id',
    `parent_id`  BIGINT            DEFAULT NULL COMMENT '父评论 ID（NULL 表示一级评论）',
    `content`    TEXT     NOT NULL COMMENT '评论内容',
    `like_count` BIGINT   NOT NULL DEFAULT 0 COMMENT '点赞数',
    `deleted`    TINYINT  NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-正常 1-已删',
    `created_at` DATETIME          DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    `updated_at` DATETIME          DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_video_id` (`video_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '评论表';

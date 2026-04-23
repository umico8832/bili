/**
 * 视频领域数据契约
 *
 * 这些接口刻意做成与未来真实后端字段一致的形态，
 * 前端无论是接 mock 还是真接口，都可以直接复用。
 */

/** 分区（一级 + 二级） */
export interface Category {
  /** 分区 id，对应后端枚举/字典 */
  id: string
  /** 分区名称，例如「番剧」「鬼畜」「科技」 */
  name: string
  /** 父级分区 id；顶级分区为 null */
  parentId: string | null
}

/** UP 主（视频作者）信息 */
export interface UpUser {
  /** 用户 id */
  id: string
  /** 用户昵称 */
  nickname: string
  /** 头像 URL */
  avatar: string
  /** 个性签名（可选） */
  signature?: string
  /** 是否大会员 / 认证 UP */
  verified?: boolean
}

/**
 * 分集（Episode）—— 一个视频合集中的单个可播放单元
 *
 * 注意：单 P 视频也会被表示为「只有一集的合集」，
 * 这样前端组件可以统一处理。
 */
export interface Episode {
  /** 分集 id */
  id: string
  /** 在合集中的序号，从 1 开始 */
  index: number
  /** 分集标题，例如「P1 开篇」 */
  title: string
  /** 时长（秒） */
  durationSec: number
  /** 视频源 URL（mp4 / m3u8） */
  src: string
  /** 该分集的封面，缺省则回退到合集封面 */
  cover?: string
}

/**
 * 视频（聚合根）—— 对应一个稿件 / 合集
 *
 * 这是首页卡片、详情页头部展示的主要数据结构。
 */
export interface Video {
  /** 稿件 id（bvid 风格） */
  id: string
  /** 稿件标题 */
  title: string
  /** 封面图 URL */
  cover: string
  /** 作者信息 */
  author: UpUser
  /** 分区 */
  category: Category
  /** 简介 / 描述 */
  description?: string
  /** 总时长（秒），= sum(episodes.durationSec) */
  durationSec: number
  /** 播放量 */
  playCount: number
  /** 弹幕数 */
  danmakuCount: number
  /** 点赞数 */
  likeCount: number
  /** 发布时间（ISO 字符串） */
  publishedAt: string
  /** 分集列表，至少 1 条 */
  episodes: Episode[]
}

/** 弹幕 */
export interface Danmaku {
  /** 弹幕 id */
  id: string
  /** 出现时间点（秒） */
  time: number
  /** 弹幕文字 */
  text: string
  /** 颜色 hex，例如 #ffffff */
  color: string
  /** 模式：滚动 / 顶部 / 底部 */
  mode: 'scroll' | 'top' | 'bottom'
  /** 发送者昵称 */
  author?: string
}

/** 评论 */
export interface Comment {
  /** 评论 id */
  id: string
  /** 评论作者 */
  author: UpUser
  /** 评论内容 */
  content: string
  /** 发布时间（ISO 字符串） */
  publishedAt: string
  /** 点赞数 */
  likeCount: number
  /** 回复数 */
  replyCount: number
}

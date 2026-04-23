import type { Category, Comment, Danmaku, Episode, UpUser, Video } from '../types/video'

/* ------------------------------------------------------------------ *
 *  确定性 mock 数据生成
 *  - 不依赖第三方 faker，纯函数生成，便于复现 / SSR 一致性
 * ------------------------------------------------------------------ */

const CATEGORIES: Category[] = [
  { id: 'tech', name: '科技', parentId: null },
  { id: 'anime', name: '番剧', parentId: null },
  { id: 'game', name: '游戏', parentId: null },
  { id: 'music', name: '音乐', parentId: null },
  { id: 'life', name: '生活', parentId: null },
  { id: 'kichiku', name: '鬼畜', parentId: null },
  { id: 'food', name: '美食', parentId: null },
  { id: 'movie', name: '影视', parentId: null },
]

const TITLE_TEMPLATES = [
  '【硬核】{topic} 完全指南，看这一个就够了',
  '我用 30 天做了一个 {topic}，结果……',
  '{topic} 终极教程：从入门到入土',
  '当代年轻人的 {topic} 生存现状',
  '一口气看完 {topic} 的 100 个冷知识',
  '【4K 修复】经典 {topic} 神级名场面合集',
  '手把手教你 {topic}，新手也能学会',
  '为什么 {topic} 突然火了？深度解析',
  '这可能是 B 站最全的 {topic} 教程',
  '{topic} 翻车实录，建议反复观看',
]

const TOPICS = [
  'Vue3 源码',
  'TypeScript 高级技巧',
  '原神 4.0',
  '塞尔达：王国之泪',
  '深夜食堂',
  'AI 绘画',
  '机械键盘',
  '复古游戏机',
  '日漫 OP 合集',
  'Linux 内核',
  'Rust 编程',
  '咖啡拉花',
  '骑行川藏线',
  '数码相机评测',
  '太空探索',
  '街头篮球',
  '自制小家电',
  '桌面整理',
]

const NICKNAMES = [
  '电子羊驼',
  '夜行书生',
  '咕咕咕咕咕',
  '爱看动画的胖虎',
  '不想上班的程序猿',
  '会唱歌的工程师',
  '深海鲸落',
  '猫猫虫咖波',
  '一只柴犬',
  '吃瓜小能手',
  '凌晨三点',
  '风吹过山岗',
]

/** 简单的可复现伪随机：基于种子的 mulberry32 */
function createRng(seed: number): () => number {
  let a = seed >>> 0
  return () => {
    a |= 0
    a = (a + 0x6d2b79f5) | 0
    let t = a
    t = Math.imul(t ^ (t >>> 15), t | 1)
    t ^= t + Math.imul(t ^ (t >>> 7), t | 61)
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296
  }
}

function pick<T>(rng: () => number, arr: readonly T[]): T {
  return arr[Math.floor(rng() * arr.length)]!
}

function randInt(rng: () => number, min: number, max: number): number {
  return Math.floor(rng() * (max - min + 1)) + min
}

function makeAuthor(rng: () => number, idx: number): UpUser {
  const nickname = pick(rng, NICKNAMES)
  // 使用 pravatar 提供稳定头像
  const avatarIdx = (idx % 70) + 1
  return {
    id: `up_${idx}`,
    nickname: `${nickname}${idx % 9 === 0 ? ' 官方' : ''}`,
    avatar: `https://i.pravatar.cc/120?img=${avatarIdx}`,
    verified: idx % 5 === 0,
  }
}

function makeEpisodes(rng: () => number, videoId: string, cover: string): Episode[] {
  const count = randInt(rng, 1, 4)
  const list: Episode[] = []
  for (let i = 1; i <= count; i++) {
    list.push({
      id: `${videoId}_p${i}`,
      index: i,
      title: count === 1 ? '正片' : `P${i} 第${i}集`,
      durationSec: randInt(rng, 90, 60 * 25),
      src: `https://example.com/video/${videoId}/p${i}.mp4`,
      cover,
    })
  }
  return list
}

/**
 * 生成一批 mock 视频数据
 *
 * @param count   生成多少条
 * @param offset  起始索引（用于无限滚动续接，保证 id 不重复）
 */
export function generateMockVideos(count: number, offset = 0): Video[] {
  const list: Video[] = []
  for (let i = 0; i < count; i++) {
    const idx = offset + i
    const rng = createRng(idx * 9301 + 49297)

    const topic = pick(rng, TOPICS)
    const titleTpl = pick(rng, TITLE_TEMPLATES)
    const title = titleTpl.replace('{topic}', topic)

    // picsum 提供稳定但「看起来不同」的封面（通过 seed）
    const cover = `https://picsum.photos/seed/bili-${idx}/640/360`

    const id = `BV${(idx + 1).toString(36).padStart(6, '0')}`
    const author = makeAuthor(rng, idx + 1)
    const category = pick(rng, CATEGORIES)
    const episodes = makeEpisodes(rng, id, cover)
    const durationSec = episodes.reduce((sum, ep) => sum + ep.durationSec, 0)

    // 发布时间：近 30 天随机
    const publishedAt = new Date(
      Date.now() - randInt(rng, 0, 30 * 24 * 60 * 60 * 1000),
    ).toISOString()

    list.push({
      id,
      title,
      cover,
      author,
      category,
      description: `关于 ${topic} 的精彩内容，欢迎一键三连~`,
      durationSec,
      playCount: randInt(rng, 1_000, 5_000_000),
      danmakuCount: randInt(rng, 10, 50_000),
      likeCount: randInt(rng, 100, 200_000),
      publishedAt,
      episodes,
    })
  }
  return list
}

/* ------------------------------------------------------------------ *
 *  展示层格式化工具
 * ------------------------------------------------------------------ */

/** 把秒格式化成 mm:ss 或 hh:mm:ss */
export function formatDuration(totalSec: number): string {
  const s = Math.max(0, Math.floor(totalSec))
  const h = Math.floor(s / 3600)
  const m = Math.floor((s % 3600) / 60)
  const sec = s % 60
  const pad = (n: number) => n.toString().padStart(2, '0')
  return h > 0 ? `${h}:${pad(m)}:${pad(sec)}` : `${pad(m)}:${pad(sec)}`
}

/** 把数字格式化成「12.3万 / 1.2亿」等中文计量 */
export function formatCount(n: number): string {
  if (n < 10_000) return n.toString()
  if (n < 100_000_000) return `${(n / 10_000).toFixed(1)}万`
  return `${(n / 100_000_000).toFixed(1)}亿`
}

/** 把 ISO 时间格式化成「3 分钟前 / 2 小时前 / 5 天前 / yyyy-MM-dd」 */
export function formatRelativeTime(iso: string): string {
  const t = new Date(iso).getTime()
  if (Number.isNaN(t)) return iso
  const diff = Date.now() - t
  const min = 60_000
  const hour = 60 * min
  const day = 24 * hour
  if (diff < min) return '刚刚'
  if (diff < hour) return `${Math.floor(diff / min)} 分钟前`
  if (diff < day) return `${Math.floor(diff / hour)} 小时前`
  if (diff < 30 * day) return `${Math.floor(diff / day)} 天前`
  const d = new Date(t)
  const pad = (n: number) => n.toString().padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`
}

/* ------------------------------------------------------------------ *
 *  详情页 mock：根据 id 还原一条视频；评论；弹幕
 * ------------------------------------------------------------------ */

/** 把 BV id 反解析回 idx，从而复用确定性生成 */
function decodeIdx(id: string): number {
  const raw = id.replace(/^BV/, '').toLowerCase()
  const n = Number.parseInt(raw, 36)
  return Number.isFinite(n) && n > 0 ? n - 1 : 0
}

/**
 * 公开测试视频源（详情页演示用）
 * - 每集都给一条真实可播放的 mp4
 */
const PUBLIC_TEST_SOURCES = [
  'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4',
  'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4',
  'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4',
  'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4',
  'https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4',
]

/**
 * 根据 id 取一条 mock 视频（详情页用）
 * - 自动把分集 src 替换为公开测试 mp4
 * - 若 id 解析失败，也返回 idx=0 的占位视频
 */
export function findMockVideoById(id: string): Video {
  const idx = decodeIdx(id)
  const [base] = generateMockVideos(1, idx)
  const video = base!
  // 用真实可播放源替换
  video.episodes = video.episodes.map((ep, i) => ({
    ...ep,
    src: PUBLIC_TEST_SOURCES[i % PUBLIC_TEST_SOURCES.length]!,
  }))
  return video
}

const COMMENT_TEMPLATES = [
  '前排吃瓜🍉',
  'UP 主太强了，已三连！',
  '看完直接去面试，谢谢 UP',
  '这个 BGM 是什么名字？',
  '我直接好家伙',
  '建议直接保送 B 站百大 UP',
  '我是从 {topic} 关键词搜过来的',
  '一口气看完毫无压力',
  '同感同感，我也是这么想的',
  '终于等到更新了 TT',
  '催更催更！',
  '这剪辑真丝滑',
]

const DANMAKU_TEMPLATES = [
  '前方高能！',
  '哈哈哈哈哈哈',
  '这也行？',
  '233333',
  'UP 真有你的',
  '泪目了',
  '我直接好家伙',
  'awsl',
  '草（一种植物）',
  '名场面 +1',
  '蹲一个后续',
  '路过~',
  '这段神剪辑',
  '梦回 2018',
]

const DANMAKU_COLORS = ['#ffffff', '#ffd166', '#ef476f', '#06d6a0', '#118ab2', '#a78bfa']

/** 根据视频 id 生成确定性评论列表 */
export function generateMockComments(videoId: string, count = 12): Comment[] {
  const idx = decodeIdx(videoId)
  const rng = createRng(idx * 31 + 7)
  const list: Comment[] = []
  for (let i = 0; i < count; i++) {
    const tpl = pick(rng, COMMENT_TEMPLATES)
    const topic = pick(rng, TOPICS)
    const content = tpl.replace('{topic}', topic)
    const author = makeAuthor(rng, idx * 100 + i + 1)
    list.push({
      id: `${videoId}_c${i + 1}`,
      author,
      content,
      publishedAt: new Date(
        Date.now() - randInt(rng, 60_000, 30 * 24 * 60 * 60 * 1000),
      ).toISOString(),
      likeCount: randInt(rng, 0, 8_000),
      replyCount: randInt(rng, 0, 200),
    })
  }
  return list
}

/** 根据视频 id 生成确定性弹幕列表 */
export function generateMockDanmaku(videoId: string, count = 30): Danmaku[] {
  const idx = decodeIdx(videoId)
  const rng = createRng(idx * 17 + 11)
  const modes: Danmaku['mode'][] = ['scroll', 'scroll', 'scroll', 'top', 'bottom']
  const list: Danmaku[] = []
  for (let i = 0; i < count; i++) {
    list.push({
      id: `${videoId}_d${i + 1}`,
      time: randInt(rng, 0, 60 * 8),
      text: pick(rng, DANMAKU_TEMPLATES),
      color: pick(rng, DANMAKU_COLORS),
      mode: pick(rng, modes),
      author: pick(rng, NICKNAMES),
    })
  }
  // 按时间排序
  list.sort((a, b) => a.time - b.time)
  return list
}

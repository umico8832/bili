<template>
  <main class="mx-auto w-full max-w-[1400px] px-4 py-6 lg:px-6">
    <!-- 双栏布局：>=lg 左 2/3 右 1/3，小屏堆叠 -->
    <div class="grid grid-cols-1 gap-6 lg:grid-cols-[minmax(0,1fr)_360px]">
      <!-- ========== 左列 ========== -->
      <div class="flex min-w-0 flex-col gap-4">
        <!-- 标题 + 元数据 -->
        <header class="space-y-2">
          <h1 class="text-xl font-bold leading-snug text-zinc-900 sm:text-2xl dark:text-zinc-50">
            {{ video.title }}
          </h1>
          <div
            class="flex flex-wrap items-center gap-x-4 gap-y-1 text-xs text-zinc-500
              tabular-nums dark:text-zinc-400"
          >
            <span class="inline-flex items-center gap-1">
              <svg
                viewBox="0 0 24 24"
                class="h-3.5 w-3.5"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                aria-hidden="true"
              >
                <polygon points="6 4 20 12 6 20 6 4" />
              </svg>
              {{ formatCount(video.playCount) }} 播放
            </span>
            <span class="inline-flex items-center gap-1">
              <svg
                viewBox="0 0 24 24"
                class="h-3.5 w-3.5"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                aria-hidden="true"
              >
                <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z" />
              </svg>
              {{ formatCount(video.danmakuCount) }} 弹幕
            </span>
            <span>· 发布于 {{ formatRelativeTime(video.publishedAt) }}</span>
            <span
              class="rounded bg-zinc-100 px-1.5 py-0.5 text-[10px] font-medium text-zinc-600
                dark:bg-zinc-800 dark:text-zinc-300"
            >
              {{ video.category.name }}
            </span>
            <span class="font-mono text-[10px] text-zinc-400">{{ video.id }}</span>
          </div>
        </header>

        <!-- 播放器 + 弹幕输入 -->
        <VideoPlayer
          :src="activeEpisode.src"
          :poster="activeEpisode.cover ?? video.cover"
          :danmaku="initialDanmaku"
          @send="onSendDanmaku"
        />

        <!-- 简介 -->
        <section
          class="rounded-2xl border border-zinc-200 bg-white p-4 text-sm leading-relaxed
            text-zinc-700 shadow-sm
            dark:border-zinc-800 dark:bg-zinc-900 dark:text-zinc-300"
        >
          <h2 class="mb-1 text-sm font-semibold text-zinc-900 dark:text-zinc-50">视频简介</h2>
          <p class="whitespace-pre-line">
            {{ video.description ?? '这位 UP 主很神秘，没有写简介~' }}
          </p>
        </section>

        <!-- 评论区 -->
        <CommentList :comments="comments" @submit="onSubmitComment" />
      </div>

      <!-- ========== 右列 ========== -->
      <aside class="flex flex-col gap-4">
        <!-- UP 主信息（复用阶段一组件） -->
        <UserProfileCard
          :avatar="video.author.avatar"
          :nickname="video.author.nickname"
          :signature="video.author.signature ?? '一个热爱分享的 UP 主'"
        >
          <template #actions>
            <button
              type="button"
              class="flex w-full items-center justify-center gap-1 rounded-xl
                bg-gradient-to-r from-rose-500 to-fuchsia-500 px-4 py-2 text-sm font-medium text-white shadow
                transition-all duration-300 ease-spring motion-reduce:transition-none
                hover:-translate-y-0.5 hover:shadow-lg
                active:scale-95"
            >
              <svg
                viewBox="0 0 24 24"
                class="h-4 w-4"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                aria-hidden="true"
              >
                <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
              </svg>
              + 关注
            </button>
          </template>
        </UserProfileCard>

        <!-- 弹幕滚动面板（限定高度） -->
        <div class="h-[320px] min-h-[260px]">
          <DanmakuPanel :danmaku="liveDanmaku" />
        </div>

        <!-- 选集 -->
        <EpisodeList
          :episodes="video.episodes"
          :active-id="activeEpisode.id"
          @select="onSelectEpisode"
        />
      </aside>
    </div>
  </main>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, ref, watch } from 'vue'
import {
  findMockVideoById,
  formatCount,
  formatRelativeTime,
  generateMockComments,
  generateMockDanmaku,
} from '../mocks/videos'
import type { Comment, Danmaku, Episode } from '../types/video'
import VideoPlayer from '../components/player/VideoPlayer.vue'
import DanmakuPanel from '../components/detail/DanmakuPanel.vue'
import EpisodeList from '../components/detail/EpisodeList.vue'
import CommentList from '../components/detail/CommentList.vue'
import UserProfileCard from '../components/base/UserProfileCard.vue'

/**
 * 通过 router 的 props:true 注入：当前视频 id
 */
const props = defineProps<{
  id: string
}>()

/* ----------- 数据加载（mock） ----------- */
const video = computed(() => findMockVideoById(props.id))
const comments = ref<Comment[]>([])
/** 初始弹幕（用于灌进 DPlayer 时间轴） */
const initialDanmaku = ref<Danmaku[]>([])
/** 实时滚动面板里看到的弹幕（动态追加） */
const liveDanmaku = ref<Danmaku[]>([])
/** 当前播放的分集 */
const activeEpisode = ref<Episode>(video.value.episodes[0]!)

function loadMocks() {
  comments.value = generateMockComments(video.value.id, 12)
  initialDanmaku.value = generateMockDanmaku(video.value.id, 30)
  liveDanmaku.value = [...initialDanmaku.value]
  activeEpisode.value = video.value.episodes[0]!
}
loadMocks()

// 路由切换 id 时，重新加载
watch(
  () => props.id,
  () => loadMocks(),
)

/* ----------- 模拟「实时弹幕」自动滚动 ----------- */
const NEW_DANMAKU_POOL = [
  '路过~',
  '233333',
  'awsl',
  '高能预警',
  '楼下接好',
  '前方核能',
  '名场面+1',
  '蹲一个',
  '泪目了',
  'UP 太强了',
]
let timer: ReturnType<typeof setInterval> | null = null

function startLiveTicker() {
  // 每 2.5s 注入一条「直播弹幕」
  timer = setInterval(() => {
    const id = `${video.value.id}_live_${Date.now()}`
    liveDanmaku.value.push({
      id,
      time: 0,
      text: NEW_DANMAKU_POOL[Math.floor(Math.random() * NEW_DANMAKU_POOL.length)]!,
      color: '#ffffff',
      mode: 'scroll',
      author: '路人甲',
    })
    // 上限，避免内存膨胀
    if (liveDanmaku.value.length > 300) {
      liveDanmaku.value.splice(0, liveDanmaku.value.length - 300)
    }
  }, 2500)
}
startLiveTicker()

onBeforeUnmount(() => {
  if (timer) clearInterval(timer)
})

/* ----------- 用户交互 ----------- */
function onSelectEpisode(ep: Episode) {
  activeEpisode.value = ep
}

function onSendDanmaku(payload: { text: string; color: string }) {
  liveDanmaku.value.push({
    id: `${video.value.id}_me_${Date.now()}`,
    time: 0,
    text: payload.text,
    color: payload.color,
    mode: 'scroll',
    author: '我',
  })
}

function onSubmitComment(content: string) {
  comments.value.unshift({
    id: `${video.value.id}_me_${Date.now()}`,
    author: {
      id: 'me',
      nickname: '我',
      avatar: 'https://i.pravatar.cc/120?img=12',
    },
    content,
    publishedAt: new Date().toISOString(),
    likeCount: 0,
    replyCount: 0,
  })
}
</script>

<template>
  <article
    tabindex="0"
    role="link"
    :aria-label="`${video.title} · UP主 ${video.author.nickname}`"
    @click="goDetail"
    @keydown.enter.prevent="goDetail"
    @keydown.space.prevent="goDetail"
    class="group relative flex cursor-pointer flex-col overflow-hidden rounded-2xl
      border border-zinc-200 bg-white shadow-sm
      will-change-transform
      transition-all duration-300 ease-spring motion-reduce:transition-none
      hover:-translate-y-2 hover:shadow-xl
      active:scale-[0.98]
      focus-visible:outline-none focus-visible:ring focus-visible:ring-zinc-400 focus-visible:ring-offset-2 focus-visible:ring-offset-zinc-50
      dark:border-zinc-800 dark:bg-zinc-900 dark:hover:shadow-white/5
      dark:focus-visible:ring-zinc-700 dark:focus-visible:ring-offset-zinc-950"
  >
    <!-- 封面 -->
    <div class="relative aspect-video w-full overflow-hidden bg-zinc-100 dark:bg-zinc-800">
      <img
        v-if="!coverFailed"
        :src="video.cover"
        :alt="video.title"
        loading="lazy"
        class="h-full w-full object-cover
          transition-transform duration-500 ease-spring motion-reduce:transition-none
          group-hover:scale-110"
        @error="coverFailed = true"
      />
      <div
        v-else
        class="flex h-full w-full items-center justify-center
          bg-gradient-to-br from-zinc-200 to-zinc-300 text-xs text-zinc-500
          dark:from-zinc-800 dark:to-zinc-700 dark:text-zinc-400"
      >
        封面加载失败
      </div>

      <!-- 顶部分区角标 -->
      <span
        class="absolute left-2 top-2 rounded-md bg-black/55 px-2 py-0.5 text-[11px] font-medium text-white
          backdrop-blur-sm
          transition-all duration-300 ease-spring motion-reduce:transition-none
          group-hover:-translate-y-0.5 group-hover:bg-black/70"
      >
        {{ video.category.name }}
      </span>

      <!-- 右下时长 -->
      <span
        class="absolute bottom-2 right-2 inline-flex items-center gap-1 rounded-md
          bg-black/65 px-1.5 py-0.5 text-[11px] font-medium text-white tabular-nums
          backdrop-blur-sm
          transition-all duration-300 ease-spring motion-reduce:transition-none
          group-hover:translate-x-[-2px] group-hover:bg-black/80"
      >
        <svg
          class="h-3 w-3 transition-transform duration-300 group-hover:scale-110 group-active:scale-95"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
          aria-hidden="true"
        >
          <polygon points="6 4 20 12 6 20 6 4" />
        </svg>
        {{ formatDuration(video.durationSec) }}
      </span>

      <!-- 悬浮遮罩（中间播放按钮） -->
      <div
        class="pointer-events-none absolute inset-0 flex items-center justify-center
          bg-black/0 opacity-0
          transition-all duration-300 ease-spring motion-reduce:transition-none
          group-hover:bg-black/20 group-hover:opacity-100"
        aria-hidden="true"
      >
        <span
          class="flex h-12 w-12 items-center justify-center rounded-full
            bg-white/90 text-zinc-900 shadow-lg
            transition-transform duration-300 ease-spring motion-reduce:transition-none
            scale-75 group-hover:scale-100 group-active:scale-95"
        >
          <svg viewBox="0 0 24 24" class="h-5 w-5" fill="currentColor" aria-hidden="true">
            <polygon points="6 4 20 12 6 20 6 4" />
          </svg>
        </span>
      </div>
    </div>

    <!-- 文本信息 -->
    <div class="flex flex-1 flex-col gap-2 p-3">
      <h3
        class="line-clamp-2 text-sm font-semibold leading-snug text-zinc-900
          transition-colors duration-300 motion-reduce:transition-none
          group-hover:text-indigo-600
          dark:text-zinc-50 dark:group-hover:text-indigo-400"
        :title="video.title"
      >
        {{ video.title }}
      </h3>

      <!-- UP 主 -->
      <div class="flex items-center gap-2 text-xs text-zinc-600 dark:text-zinc-400">
        <img
          :src="video.author.avatar"
          :alt="video.author.nickname"
          loading="lazy"
          class="h-5 w-5 rounded-full object-cover ring-1 ring-zinc-200
            transition-transform duration-300 ease-spring motion-reduce:transition-none
            group-hover:scale-110
            dark:ring-zinc-700"
        />
        <span class="truncate">{{ video.author.nickname }}</span>
        <svg
          v-if="video.author.verified"
          class="h-3 w-3 shrink-0 text-amber-500"
          viewBox="0 0 24 24"
          fill="currentColor"
          aria-label="认证用户"
        >
          <path
            d="M12 2l2.39 2.51 3.46-.45.44 3.46L20.8 9.6l-1.51 3.16 1.51 3.16-2.51 2.08-.44 3.46-3.46-.45L12 23.5l-2.39-2.49-3.46.45-.44-3.46L3.2 15.92l1.51-3.16L3.2 9.6l2.51-2.08.44-3.46 3.46.45L12 2zm-1 13.5l6-6-1.41-1.41L11 12.67 8.41 10.09 7 11.5l4 4z"
          />
        </svg>
      </div>

      <!-- 播放/弹幕统计 -->
      <div
        class="mt-auto flex items-center gap-3 text-[11px] text-zinc-500 tabular-nums
          dark:text-zinc-500"
      >
        <span class="inline-flex items-center gap-1">
          <svg
            class="h-3 w-3 transition-transform duration-300 group-hover:translate-x-0.5"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            aria-hidden="true"
          >
            <polygon points="6 4 20 12 6 20 6 4" />
          </svg>
          {{ formatCount(video.playCount) }}
        </span>
        <span class="inline-flex items-center gap-1">
          <svg
            class="h-3 w-3"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            aria-hidden="true"
          >
            <path
              d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"
            />
          </svg>
          {{ formatCount(video.danmakuCount) }}
        </span>
      </div>
    </div>
  </article>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import type { Video } from '../../types/video'
import { formatCount, formatDuration } from '../../mocks/videos'

const props = defineProps<{
  video: Video
}>()

const router = useRouter()
const coverFailed = ref(false)

function goDetail() {
  void router.push({ name: 'video-detail', params: { id: props.video.id } })
}
</script>

<template>
  <section class="space-y-4">
    <CategoryTabs />

    <!-- 视频网格 -->
    <div
      class="grid gap-4 grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5"
    >
      <VideoCard v-for="v in videos" :key="v.id" :video="v" />

      <!-- 加载中：骨架屏 -->
      <template v-if="isLoading">
        <VideoCardSkeleton v-for="i in PAGE_SIZE" :key="`sk-${i}`" />
      </template>
    </div>

    <!-- 触底哨兵 / 状态文案 -->
    <div
      ref="sentinelRef"
      class="flex items-center justify-center py-8 text-sm text-zinc-500 dark:text-zinc-400"
    >
      <span v-if="isLoading" class="inline-flex items-center gap-2">
        <svg
          class="h-4 w-4 animate-spin text-indigo-500"
          viewBox="0 0 24 24"
          fill="none"
          aria-hidden="true"
        >
          <circle cx="12" cy="12" r="10" stroke="currentColor" stroke-width="3" opacity="0.25" />
          <path
            d="M22 12a10 10 0 0 1-10 10"
            stroke="currentColor"
            stroke-width="3"
            stroke-linecap="round"
          />
        </svg>
        正在加载更多…
      </span>
      <span v-else-if="!hasMore">— 已经到底啦 —</span>
      <span v-else class="opacity-0">scroll trigger</span>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onBeforeUnmount, ref, useTemplateRef } from 'vue'
import { useIntersectionObserver } from '@vueuse/core'
import CategoryTabs from '../../components/layout/CategoryTabs.vue'
import VideoCard from '../../components/base/VideoCard.vue'
import VideoCardSkeleton from '../../components/base/VideoCardSkeleton.vue'
import type { Video } from '../../types/video'
import { generateMockVideos } from '../../mocks/videos'

const INITIAL_SIZE = 15
const PAGE_SIZE = 10
/** 模拟网络延迟（ms） */
const FAKE_LATENCY = 600
/** 总条数上限，避免无限增长 */
const MAX_TOTAL = 80

const videos = ref<Video[]>(generateMockVideos(INITIAL_SIZE))
const isLoading = ref(false)
const hasMore = ref(true)

let timer: number | undefined

async function loadMore() {
  if (isLoading.value || !hasMore.value) return
  isLoading.value = true

  await new Promise<void>((resolve) => {
    timer = window.setTimeout(resolve, FAKE_LATENCY)
  })

  const next = generateMockVideos(PAGE_SIZE, videos.value.length)
  videos.value.push(...next)

  if (videos.value.length >= MAX_TOTAL) {
    hasMore.value = false
  }

  isLoading.value = false
}

// 触底自动加载
const sentinelRef = useTemplateRef<HTMLElement>('sentinelRef')
const { stop } = useIntersectionObserver(sentinelRef, ([entry]) => {
  if (entry.isIntersecting) loadMore()
})

onBeforeUnmount(() => {
  stop()
  clearTimeout(timer)
})
</script>

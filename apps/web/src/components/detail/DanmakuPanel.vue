<template>
  <section
    class="flex h-full flex-col overflow-hidden rounded-2xl border border-zinc-200 bg-white shadow-sm
      dark:border-zinc-800 dark:bg-zinc-900"
    aria-label="实时弹幕列表"
  >
    <header
      class="flex items-center justify-between border-b border-zinc-200 px-4 py-2.5 text-sm
        dark:border-zinc-800"
    >
      <div class="inline-flex items-center gap-2 font-medium text-zinc-700 dark:text-zinc-200">
        <span class="relative flex h-2 w-2">
          <span class="absolute inline-flex h-full w-full animate-ping rounded-full bg-rose-400 opacity-75" />
          <span class="relative inline-flex h-2 w-2 rounded-full bg-rose-500" />
        </span>
        实时弹幕
      </div>
      <span class="text-xs tabular-nums text-zinc-500 dark:text-zinc-500">
        {{ visible.length }} 条
      </span>
    </header>

    <ul
      ref="listRef"
      class="flex-1 space-y-1.5 overflow-y-auto px-3 py-2 text-sm scroll-smooth"
    >
      <li
        v-for="d in visible"
        :key="d.id"
        class="group/d flex items-start gap-2 rounded-lg px-2 py-1
          transition-colors duration-200 hover:bg-zinc-100 dark:hover:bg-zinc-800/60"
      >
        <span class="mt-0.5 shrink-0 rounded bg-zinc-100 px-1 font-mono text-[10px] tabular-nums text-zinc-500
          dark:bg-zinc-800 dark:text-zinc-400">
          {{ formatDuration(d.time) }}
        </span>
        <span
          class="break-all leading-relaxed"
          :style="{ color: d.color === '#ffffff' ? '' : d.color }"
        >
          {{ d.text }}
        </span>
      </li>
      <li v-if="visible.length === 0" class="py-8 text-center text-xs text-zinc-400">
        暂时还没有弹幕，快来发第一条吧～
      </li>
    </ul>
  </section>
</template>

<script setup lang="ts">
import { computed, nextTick, useTemplateRef, watch } from 'vue'
import type { Danmaku } from '../../types/video'
import { formatDuration } from '../../mocks/videos'

const props = defineProps<{
  /** 弹幕列表（按时间升序） */
  danmaku: Danmaku[]
  /** 最大显示条数（避免列表无限增长） */
  max?: number
}>()

const listRef = useTemplateRef<HTMLElement>('listRef')

const visible = computed<Danmaku[]>(() => {
  const max = props.max ?? 200
  return props.danmaku.slice(-max)
})

// 新弹幕进入时滚到底部
watch(
  () => props.danmaku.length,
  async () => {
    await nextTick()
    const el = listRef.value
    if (el) el.scrollTop = el.scrollHeight
  },
)

</script>

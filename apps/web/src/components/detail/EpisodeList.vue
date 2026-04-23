<template>
  <section
    class="overflow-hidden rounded-2xl border border-zinc-200 bg-white shadow-sm
      dark:border-zinc-800 dark:bg-zinc-900"
    aria-label="分集选集"
  >
    <header
      class="flex items-center justify-between border-b border-zinc-200 px-4 py-2.5 text-sm
        dark:border-zinc-800"
    >
      <span class="font-medium text-zinc-700 dark:text-zinc-200">选集</span>
      <span class="text-xs tabular-nums text-zinc-500">共 {{ episodes.length }} 集</span>
    </header>

    <ul class="grid grid-cols-2 gap-2 p-3 sm:grid-cols-3 lg:grid-cols-2">
      <li v-for="ep in episodes" :key="ep.id">
        <button
          type="button"
          @click="$emit('select', ep)"
          :aria-current="ep.id === activeId ? 'true' : undefined"
          class="group/ep relative flex w-full flex-col items-start gap-1 rounded-xl border px-3 py-2 text-left text-xs
            transition-all duration-300 ease-spring motion-reduce:transition-none
            will-change-transform
            hover:-translate-y-1 hover:shadow-md
            active:scale-[0.97]
            focus-visible:outline-none focus-visible:ring focus-visible:ring-indigo-400 focus-visible:ring-offset-2
            dark:focus-visible:ring-offset-zinc-950"
          :class="
            ep.id === activeId
              ? 'border-indigo-500 bg-indigo-500/10 text-indigo-700 shadow-inner ring-1 ring-indigo-400/40 dark:text-indigo-300'
              : 'border-zinc-200 bg-white text-zinc-700 hover:border-indigo-300 dark:border-zinc-700 dark:bg-zinc-900 dark:text-zinc-300 dark:hover:border-indigo-500'
          "
        >
          <span class="inline-flex items-center gap-1 font-medium">
            <span
              class="rounded bg-zinc-100 px-1 font-mono text-[10px] tabular-nums text-zinc-600
                transition-colors duration-300 group-hover/ep:bg-indigo-100 group-hover/ep:text-indigo-700
                dark:bg-zinc-800 dark:text-zinc-300 dark:group-hover/ep:bg-indigo-500/20"
            >
              P{{ ep.index }}
            </span>
            <span class="line-clamp-1">{{ ep.title }}</span>
          </span>
          <span class="text-[10px] tabular-nums text-zinc-500">
            {{ formatDuration(ep.durationSec) }}
          </span>
        </button>
      </li>
    </ul>
  </section>
</template>

<script setup lang="ts">
import type { Episode } from '../../types/video'
import { formatDuration } from '../../mocks/videos'

defineProps<{
  episodes: Episode[]
  activeId: string
}>()

defineEmits<{
  select: [ep: Episode]
}>()
</script>

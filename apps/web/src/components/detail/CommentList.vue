<template>
  <section
    class="rounded-2xl border border-zinc-200 bg-white p-4 shadow-sm
      dark:border-zinc-800 dark:bg-zinc-900"
    aria-label="评论区"
  >
    <header class="mb-3 flex items-center justify-between">
      <h2 class="text-base font-semibold text-zinc-900 dark:text-zinc-50">
        评论
        <span class="ml-1 text-sm font-normal text-zinc-500">{{ comments.length }}</span>
      </h2>
    </header>

    <!-- 输入框 -->
    <form
      class="mb-5 flex items-start gap-3 rounded-xl border border-zinc-200 bg-zinc-50 p-3
        transition-all duration-300 ease-spring motion-reduce:transition-none
        focus-within:-translate-y-0.5 focus-within:border-indigo-400 focus-within:bg-white focus-within:shadow-sm
        dark:border-zinc-700 dark:bg-zinc-800/40 dark:focus-within:bg-zinc-900"
      @submit.prevent="onSubmit"
    >
      <div
        class="flex h-9 w-9 shrink-0 items-center justify-center rounded-full
          bg-gradient-to-br from-indigo-500 to-fuchsia-500 text-sm font-medium text-white shadow"
        aria-hidden="true"
      >
        我
      </div>
      <div class="flex flex-1 flex-col gap-2">
        <textarea
          v-model.trim="draft"
          rows="2"
          maxlength="200"
          placeholder="发一条友善的评论~"
          class="w-full resize-none rounded-lg bg-transparent px-1 py-0.5 text-sm
            text-zinc-900 outline-none placeholder:text-zinc-400
            dark:text-zinc-50 dark:placeholder:text-zinc-500"
        />
        <div class="flex items-center justify-between text-xs text-zinc-500">
          <span class="tabular-nums">{{ draft.length }} / 200</span>
          <button
            type="submit"
            :disabled="!draft"
            class="inline-flex items-center gap-1 rounded-lg bg-indigo-600 px-3 py-1.5 text-sm font-medium text-white shadow
              transition-all duration-300 ease-spring motion-reduce:transition-none
              hover:-translate-y-0.5 hover:bg-indigo-500 hover:shadow-lg
              active:scale-95
              disabled:cursor-not-allowed disabled:opacity-50 disabled:hover:translate-y-0 disabled:hover:bg-indigo-600"
          >
            发布
          </button>
        </div>
      </div>
    </form>

    <!-- 评论列表 -->
    <ul class="divide-y divide-zinc-100 dark:divide-zinc-800">
      <li
        v-for="c in comments"
        :key="c.id"
        class="group/c flex gap-3 py-4 transition-colors duration-300 hover:bg-zinc-50 dark:hover:bg-zinc-800/30"
      >
        <img
          :src="c.author.avatar"
          :alt="c.author.nickname"
          loading="lazy"
          class="h-9 w-9 shrink-0 rounded-full object-cover ring-1 ring-zinc-200
            transition-transform duration-300 ease-spring motion-reduce:transition-none
            group-hover/c:scale-110
            dark:ring-zinc-700"
        />
        <div class="flex min-w-0 flex-1 flex-col gap-1">
          <div class="flex items-center gap-2 text-sm">
            <span class="font-medium text-zinc-700 dark:text-zinc-200">
              {{ c.author.nickname }}
            </span>
            <span class="text-xs text-zinc-400">{{ formatRelativeTime(c.publishedAt) }}</span>
          </div>
          <p class="text-sm leading-relaxed text-zinc-800 dark:text-zinc-200">
            {{ c.content }}
          </p>
          <div class="mt-1 flex items-center gap-4 text-xs text-zinc-500">
            <button
              type="button"
              class="inline-flex items-center gap-1 rounded-md px-1.5 py-0.5
                transition-all duration-200 ease-spring
                hover:-translate-y-0.5 hover:bg-rose-50 hover:text-rose-500
                active:scale-95
                dark:hover:bg-rose-500/10"
            >
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
                <path
                  d="M14 9V5a3 3 0 0 0-3-3l-4 9v11h11.28a2 2 0 0 0 2-1.7l1.38-9A2 2 0 0 0 19.66 9H14z"
                />
                <line x1="7" y1="22" x2="7" y2="11" />
              </svg>
              {{ c.likeCount }}
            </button>
            <button
              type="button"
              class="inline-flex items-center gap-1 rounded-md px-1.5 py-0.5
                transition-all duration-200 ease-spring
                hover:-translate-y-0.5 hover:bg-indigo-50 hover:text-indigo-600
                active:scale-95
                dark:hover:bg-indigo-500/10"
            >
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
                <path d="M21 11.5a8.38 8.38 0 0 1-9 8.5 8.5 8.5 0 0 1-7-3.5L3 21l4.5-2A8.38 8.38 0 0 1 3 11.5a9 9 0 0 1 18 0z" />
              </svg>
              回复 {{ c.replyCount }}
            </button>
          </div>
        </div>
      </li>
      <li v-if="comments.length === 0" class="py-8 text-center text-sm text-zinc-400">
        还没有评论，快来抢沙发～
      </li>
    </ul>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import type { Comment } from '../../types/video'
import { formatRelativeTime } from '../../mocks/videos'

defineProps<{
  comments: Comment[]
}>()

const emit = defineEmits<{
  submit: [content: string]
}>()

const draft = ref('')

function onSubmit() {
  if (!draft.value) return
  emit('submit', draft.value)
  draft.value = ''
}
</script>

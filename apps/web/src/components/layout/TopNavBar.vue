<template>
  <header class="sticky top-0 z-50 border-b border-zinc-200/80 bg-white/70 backdrop-blur dark:border-zinc-800/80 dark:bg-zinc-950/60">
    <div class="mx-auto flex h-16 w-full max-w-6xl items-center gap-3 px-4">
      <RouterLink
        to="/"
        class="group inline-flex items-center gap-2 rounded-xl px-2 py-2 text-sm font-semibold tracking-tight
          cursor-pointer focus-visible:ring focus-visible:ring-offset-2 motion-reduce:transition-none will-change-transform
          transition-all duration-300 ease-spring
          hover:-translate-y-2 hover:shadow-lg dark:hover:shadow-white/5
          active:scale-[0.98]
          focus-visible:ring-zinc-400 focus-visible:ring-offset-zinc-50
          dark:focus-visible:ring-zinc-700 dark:focus-visible:ring-offset-zinc-950"
      >
        <span
          class="grid size-9 place-items-center rounded-xl bg-zinc-900 text-white transition-transform duration-300 group-hover:scale-110 group-hover:translate-x-1 group-active:scale-95 dark:bg-zinc-100 dark:text-zinc-900"
          aria-hidden="true"
        >
          B
        </span>
        <span class="hidden sm:inline">bili-clone</span>
      </RouterLink>

      <div class="flex-1">
        <label class="sr-only" for="global-search">搜索</label>
        <div class="relative">
          <input
            id="global-search"
            v-model="q"
            type="search"
            placeholder="搜索视频 / UP 主"
            class="h-11 w-full rounded-2xl border border-zinc-200 bg-white px-4 pr-10 text-sm outline-none
              transition-all duration-300 ease-spring
              focus:border-zinc-400 focus:ring-4 focus:ring-zinc-200/60
              dark:border-zinc-800 dark:bg-zinc-900 dark:focus:border-zinc-600 dark:focus:ring-zinc-800/60"
          />
          <div class="pointer-events-none absolute inset-y-0 right-3 grid place-items-center text-zinc-500 dark:text-zinc-400" aria-hidden="true">
            ⌕
          </div>
        </div>
      </div>

      <UiButton variant="ghost" class="hidden sm:inline-flex" @click="goUpload">
        <template #icon>⇪</template>
        上传
      </UiButton>

      <button
        type="button"
        class="group grid size-11 place-items-center rounded-2xl border border-zinc-200 bg-white
          cursor-pointer focus-visible:ring focus-visible:ring-offset-2 motion-reduce:transition-none will-change-transform
          transition-all duration-300 ease-spring
          hover:-translate-y-2 hover:shadow-lg dark:hover:shadow-white/5
          active:scale-[0.98]
          dark:border-zinc-800 dark:bg-zinc-900
          focus-visible:ring-zinc-400 focus-visible:ring-offset-zinc-50
          dark:focus-visible:ring-zinc-700 dark:focus-visible:ring-offset-zinc-950"
        aria-label="用户中心（占位）"
      >
        <span class="transition-transform duration-300 group-hover:scale-110 group-hover:translate-x-1 group-active:scale-95" aria-hidden="true">🙂</span>
      </button>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter, RouterLink } from 'vue-router'
import UiButton from '../base/UiButton.vue'

const router = useRouter()
const q = ref('')

function goUpload() {
  router.push('/upload')
}
</script>


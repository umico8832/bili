<template>
  <nav class="flex flex-wrap items-center gap-2">
    <RouterLink v-for="t in tabs" :key="t.to" :to="t.to" custom v-slot="{ href, navigate, isActive }">
      <a
        :href="href"
        @click="navigate"
        class="group inline-flex items-center rounded-2xl px-4 py-2 text-sm font-medium
          cursor-pointer focus-visible:ring focus-visible:ring-offset-2 motion-reduce:transition-none will-change-transform
          transition-all duration-300 ease-spring
          hover:-translate-y-2 hover:shadow-lg dark:hover:shadow-white/5
          active:scale-[0.98]
          focus-visible:ring-zinc-400 focus-visible:ring-offset-zinc-50
          dark:focus-visible:ring-zinc-700 dark:focus-visible:ring-offset-zinc-950"
        :class="
          isActive
            ? 'bg-zinc-900 text-white dark:bg-zinc-100 dark:text-zinc-900'
            : 'bg-white text-zinc-800 ring-1 ring-zinc-200 hover:bg-zinc-50 dark:bg-zinc-900 dark:text-zinc-100 dark:ring-zinc-800 dark:hover:bg-zinc-800'
        "
      >
        <span class="transition-transform duration-300 group-hover:scale-110 group-hover:translate-x-1 group-active:scale-95" aria-hidden="true">
          {{ t.icon }}
        </span>
        <span class="ml-2">{{ t.label }}</span>
      </a>
    </RouterLink>
  </nav>
</template>

<script setup lang="ts">
import { RouterLink } from 'vue-router'

const tabs = [
  { to: '/', label: '首页', icon: '🏠' },
  { to: '/hot', label: '热门', icon: '🔥' },
  { to: '/following', label: '正在关注', icon: '⭐' },
] as const
</script>


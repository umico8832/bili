<template>
  <div
    class="flex flex-col overflow-hidden rounded-2xl border border-zinc-200 bg-white shadow-sm
      dark:border-zinc-800 dark:bg-zinc-900"
    aria-hidden="true"
  >
    <div class="aspect-video w-full animate-pulse bg-zinc-200 dark:bg-zinc-800" />
    <div class="flex flex-col gap-2 p-3">
      <div class="h-4 w-11/12 animate-pulse rounded bg-zinc-200 dark:bg-zinc-800" />
      <div class="h-4 w-3/5 animate-pulse rounded bg-zinc-200 dark:bg-zinc-800" />
      <div class="mt-1 flex items-center gap-2">
        <div class="h-5 w-5 animate-pulse rounded-full bg-zinc-200 dark:bg-zinc-800" />
        <div class="h-3 w-24 animate-pulse rounded bg-zinc-200 dark:bg-zinc-800" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts"></script>

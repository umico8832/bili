<template>
  <div class="min-h-dvh">
    <TopNavBar />
    <main class="mx-auto w-full max-w-6xl px-4 pb-12 pt-4">
      <RouterView />
    </main>
  </div>
</template>

<script setup lang="ts">
import { RouterView } from 'vue-router'
import TopNavBar from './components/layout/TopNavBar.vue'
</script>


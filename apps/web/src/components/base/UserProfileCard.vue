<template>
  <article
    class="group relative flex w-full max-w-sm flex-col items-center gap-4 overflow-hidden
      rounded-2xl border border-zinc-200 bg-white p-6 shadow-sm
      transition-all duration-300 ease-spring
      hover:-translate-y-1 hover:shadow-xl
      dark:border-zinc-800 dark:bg-zinc-900 dark:hover:shadow-white/5"
  >
    <!-- 顶部装饰渐变 -->
    <div
      class="pointer-events-none absolute inset-x-0 top-0 h-20
        bg-gradient-to-br from-indigo-400/30 via-fuchsia-400/20 to-transparent
        dark:from-indigo-500/20 dark:via-fuchsia-500/10"
      aria-hidden="true"
    />

    <!-- 头像 -->
    <div class="relative z-10 mt-2">
      <img
        v-if="avatar && !avatarFailed"
        :src="avatar"
        :alt="`${nickname} 的头像`"
        class="h-20 w-20 rounded-full object-cover ring-4 ring-white shadow-md
          transition-transform duration-300 group-hover:scale-105
          dark:ring-zinc-900"
        @error="avatarFailed = true"
      />
      <div
        v-else
        class="flex h-20 w-20 items-center justify-center rounded-full
          bg-gradient-to-br from-indigo-500 to-fuchsia-500
          text-2xl font-semibold text-white ring-4 ring-white shadow-md
          transition-transform duration-300 group-hover:scale-105
          dark:ring-zinc-900"
        :aria-label="`${nickname} 的头像占位`"
      >
        {{ initials }}
      </div>
    </div>

    <!-- 昵称 -->
    <h3 class="relative z-10 text-lg font-semibold text-zinc-900 dark:text-zinc-50">
      {{ nickname }}
    </h3>

    <!-- 签名 -->
    <p
      class="relative z-10 line-clamp-3 text-center text-sm leading-relaxed
        text-zinc-600 dark:text-zinc-400"
    >
      {{ signature || '这个人很神秘，什么也没留下~' }}
    </p>

    <!-- 底部插槽（可放按钮等） -->
    <div v-if="$slots.actions" class="relative z-10 mt-2 w-full">
      <slot name="actions" />
    </div>
  </article>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

const props = withDefaults(
  defineProps<{
    /** 头像 URL */
    avatar?: string
    /** 昵称 */
    nickname: string
    /** 个性签名 */
    signature?: string
  }>(),
  {
    avatar: '',
    signature: '',
  },
)

const avatarFailed = ref(false)

/** 头像加载失败时的占位首字母 */
const initials = computed(() => {
  const name = props.nickname?.trim() ?? ''
  if (!name) return '?'
  // 中文取第一个字，英文取首字母（最多 2 个）
  const isAscii = /^[\x00-\x7F]+$/.test(name)
  if (isAscii) {
    return name
      .split(/\s+/)
      .map((w) => w[0])
      .join('')
      .slice(0, 2)
      .toUpperCase()
  }
  return name.slice(0, 1)
})
</script>

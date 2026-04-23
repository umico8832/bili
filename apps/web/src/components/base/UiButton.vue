<template>
  <button
    :type="type"
    :disabled="disabled"
    class="group inline-flex items-center justify-center gap-2 rounded-xl px-3 py-2 text-sm font-medium
      cursor-pointer focus-visible:ring focus-visible:ring-offset-2 motion-reduce:transition-none will-change-transform
      transition-all duration-300 ease-spring
      hover:-translate-y-2 hover:shadow-lg dark:hover:shadow-white/5
      active:scale-[0.98]
      disabled:pointer-events-none disabled:opacity-50
      focus-visible:ring-zinc-400 focus-visible:ring-offset-zinc-50
      dark:focus-visible:ring-zinc-700 dark:focus-visible:ring-offset-zinc-950"
    :class="variantClass"
  >
    <span
      v-if="$slots.icon"
      class="inline-flex transition-transform duration-300 group-hover:scale-110 group-hover:translate-x-1 group-active:scale-95"
      aria-hidden="true"
    >
      <slot name="icon" />
    </span>
    <slot />
  </button>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = withDefaults(
  defineProps<{
    variant?: 'primary' | 'ghost'
    type?: 'button' | 'submit' | 'reset'
    disabled?: boolean
  }>(),
  { variant: 'primary', type: 'button', disabled: false },
)

const variantClass = computed(() => {
  switch (props.variant) {
    case 'ghost':
      return 'bg-transparent text-zinc-800 hover:bg-zinc-100 dark:text-zinc-100 dark:hover:bg-zinc-800'
    default:
      return 'bg-zinc-900 text-white hover:bg-zinc-800 dark:bg-zinc-100 dark:text-zinc-900 dark:hover:bg-white'
  }
})
</script>


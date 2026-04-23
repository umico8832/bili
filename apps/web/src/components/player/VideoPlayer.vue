<template>
  <div class="space-y-3">
    <!-- 播放器挂载点 -->
    <div
      ref="containerRef"
      class="aspect-video w-full overflow-hidden rounded-2xl bg-black shadow-lg"
    />

    <!-- 发送弹幕输入区 -->
    <form
      class="flex items-center gap-2 rounded-xl border border-zinc-200 bg-white p-2 shadow-sm
        transition-all duration-300 ease-spring motion-reduce:transition-none
        focus-within:-translate-y-0.5 focus-within:shadow-md focus-within:ring-2 focus-within:ring-indigo-400
        dark:border-zinc-800 dark:bg-zinc-900 dark:focus-within:ring-indigo-500"
      @submit.prevent="onSend"
    >
      <span
        class="hidden shrink-0 select-none rounded-md bg-zinc-100 px-2 py-1 text-xs text-zinc-500 sm:inline-block
          dark:bg-zinc-800 dark:text-zinc-400"
      >
        弹幕
      </span>
      <input
        v-model.trim="text"
        type="text"
        maxlength="80"
        placeholder="发个友善的弹幕见证当下吧 ~"
        class="min-w-0 flex-1 bg-transparent px-2 py-1 text-sm text-zinc-900 outline-none
          placeholder:text-zinc-400 dark:text-zinc-50 dark:placeholder:text-zinc-500"
        :disabled="disabled"
      />
      <input
        v-model="color"
        type="color"
        class="h-8 w-8 cursor-pointer rounded border border-zinc-200 bg-transparent
          transition-transform duration-300 ease-spring hover:scale-110 active:scale-95
          dark:border-zinc-700"
        title="弹幕颜色"
        aria-label="弹幕颜色"
      />
      <button
        type="submit"
        :disabled="disabled || !text"
        class="inline-flex shrink-0 items-center gap-1 rounded-lg bg-indigo-600 px-3 py-1.5 text-sm font-medium text-white shadow
          transition-all duration-300 ease-spring motion-reduce:transition-none
          hover:-translate-y-0.5 hover:bg-indigo-500 hover:shadow-lg
          active:scale-95
          disabled:cursor-not-allowed disabled:opacity-50 disabled:hover:translate-y-0 disabled:hover:bg-indigo-600"
      >
        <svg
          viewBox="0 0 24 24"
          class="h-4 w-4"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
          stroke-linecap="round"
          stroke-linejoin="round"
          aria-hidden="true"
        >
          <line x1="22" y1="2" x2="11" y2="13" />
          <polygon points="22 2 15 22 11 13 2 9 22 2" />
        </svg>
        发送
      </button>
    </form>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref, useTemplateRef, watch } from 'vue'
import DPlayer from 'dplayer'
// 注意：dplayer 的样式已 inline 到 dist/DPlayer.min.js 里，无需单独引入 css
import type { Danmaku } from '../../types/video'

const props = defineProps<{
  /** 视频源 URL */
  src: string
  /** 封面 */
  poster?: string
  /** 初始弹幕列表 */
  danmaku?: Danmaku[]
}>()

const emit = defineEmits<{
  send: [payload: { text: string; color: string }]
}>()

const containerRef = useTemplateRef<HTMLElement>('containerRef')
let player: DPlayer | null = null

const text = ref('')
const color = ref('#ffffff')
const disabled = ref(false)

function buildPlayer() {
  if (!containerRef.value) return
  player = new DPlayer({
    container: containerRef.value,
    autoplay: false,
    theme: '#6366f1',
    screenshot: true,
    hotkey: true,
    preload: 'metadata',
    volume: 0.7,
    video: {
      url: props.src,
      pic: props.poster,
      type: 'auto',
    },
    // 关闭远端弹幕请求，纯本地绘制
    danmaku: false,
  })

  // 把初始弹幕画到时间轴上
  drainDanmaku()
}

/** 把 props.danmaku 一次性绘到播放器上 */
function drainDanmaku() {
  if (!player?.danmaku || !props.danmaku?.length) return
  for (const d of props.danmaku) {
    player.danmaku.draw({
      text: d.text,
      color: d.color,
      type: d.mode === 'top' ? 'top' : d.mode === 'bottom' ? 'bottom' : 'right',
      time: d.time,
      author: d.author,
    })
  }
}

function onSend() {
  if (!text.value) return
  const payload = { text: text.value, color: color.value }
  // 立即在播放器上发送（飞过屏幕）
  player?.danmaku?.send({
    text: payload.text,
    color: payload.color,
    type: 'right',
  })
  emit('send', payload)
  text.value = ''
}

onMounted(() => {
  buildPlayer()
})

// 切集时重建
watch(
  () => props.src,
  (next) => {
    if (player && next) {
      player.switchVideo({ url: next, pic: props.poster, type: 'auto' }, false)
      drainDanmaku()
    }
  },
)

onBeforeUnmount(() => {
  player?.destroy()
  player = null
})

defineExpose({
  /** 外部追加一条弹幕（例如别处发送） */
  pushDanmaku(d: Pick<Danmaku, 'text' | 'color' | 'mode'>) {
    player?.danmaku?.send({
      text: d.text,
      color: d.color,
      type: d.mode === 'top' ? 'top' : d.mode === 'bottom' ? 'bottom' : 'right',
    })
  },
})
</script>

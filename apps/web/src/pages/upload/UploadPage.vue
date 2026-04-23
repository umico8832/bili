<template>
  <div class="mx-auto max-w-2xl space-y-6 p-4">
    <!-- 标题 -->
    <div>
      <h1 class="text-2xl font-bold tracking-tight">上传视频</h1>
      <p class="mt-1 text-sm text-zinc-500 dark:text-zinc-400">支持大文件分片上传，相同文件秒传</p>
    </div>

    <!-- 拖拽上传区 -->
    <div
      v-if="status === 'idle'"
      class="relative flex flex-col items-center justify-center rounded-2xl border-2 border-dashed border-zinc-300 bg-zinc-50 px-6 py-14 transition-colors dark:border-zinc-700 dark:bg-zinc-900"
      :class="{ 'border-pink-400 bg-pink-50 dark:bg-pink-950/30': isDragging }"
      @dragover.prevent="isDragging = true"
      @dragleave.prevent="isDragging = false"
      @drop.prevent="onDrop"
      @click="fileInputRef?.click()"
    >
      <!-- 图标 -->
      <div class="mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-pink-100 dark:bg-pink-900/40">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-8 w-8 text-pink-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5"
            d="M3 16.5v2.25A2.25 2.25 0 005.25 21h13.5A2.25 2.25 0 0021 18.75V16.5m-13.5-9L12 3m0 0l4.5 4.5M12 3v13.5" />
        </svg>
      </div>
      <p class="text-base font-medium text-zinc-700 dark:text-zinc-200">
        {{ isDragging ? '松手即可上传' : '点击或拖拽视频到此处' }}
      </p>
      <p class="mt-1 text-sm text-zinc-400">支持 MP4、MOV、AVI、MKV 等格式，单文件不限大小</p>
      <!-- 隐藏 input -->
      <input
        ref="fileInputRef"
        type="file"
        accept="video/*"
        class="absolute inset-0 cursor-pointer opacity-0"
        @change="onFileChange"
      />
    </div>

    <!-- 已选文件信息 -->
    <div
      v-if="selectedFile && status === 'idle'"
      class="flex items-center gap-3 rounded-xl border border-zinc-200 bg-white p-4 dark:border-zinc-700 dark:bg-zinc-900"
    >
      <div class="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-pink-100 dark:bg-pink-900/40">
        <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-pink-500" fill="none" viewBox="0 0 24 24" stroke="currentColor">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M15 10l4.553-2.276A1 1 0 0121 8.723v6.554a1 1 0 01-1.447.894L15 14M3 8a2 2 0 012-2h8a2 2 0 012 2v8a2 2 0 01-2 2H5a2 2 0 01-2-2V8z" />
        </svg>
      </div>
      <div class="min-w-0 flex-1">
        <p class="truncate text-sm font-medium text-zinc-800 dark:text-zinc-100">{{ selectedFile.name }}</p>
        <p class="text-xs text-zinc-400">{{ formatSize(selectedFile.size) }}・{{ chunkCount }} 个分片</p>
      </div>
      <button
        class="text-xs text-zinc-400 hover:text-red-500"
        @click.stop="clearFile"
      >
        移除
      </button>
    </div>

    <!-- 表单 -->
    <div
      v-if="selectedFile && status === 'idle'"
      class="space-y-4 rounded-2xl border border-zinc-200 bg-white p-6 dark:border-zinc-700 dark:bg-zinc-900"
    >
      <h2 class="text-sm font-semibold text-zinc-700 dark:text-zinc-200">视频信息</h2>

      <div class="space-y-1">
        <label class="text-xs font-medium text-zinc-500">分集标题 <span class="text-red-500">*</span></label>
        <input
          v-model="form.title"
          type="text"
          placeholder="请输入分集标题"
          class="w-full rounded-lg border border-zinc-200 bg-zinc-50 px-3 py-2 text-sm outline-none transition focus:border-pink-400 focus:ring-2 focus:ring-pink-100 dark:border-zinc-700 dark:bg-zinc-800 dark:text-zinc-100 dark:focus:ring-pink-900/40"
        />
      </div>

      <div class="grid grid-cols-2 gap-4">
        <div class="space-y-1">
          <label class="text-xs font-medium text-zinc-500">视频合集 ID <span class="text-red-500">*</span></label>
          <input
            v-model.number="form.videoId"
            type="number"
            placeholder="如：1001"
            class="w-full rounded-lg border border-zinc-200 bg-zinc-50 px-3 py-2 text-sm outline-none transition focus:border-pink-400 focus:ring-2 focus:ring-pink-100 dark:border-zinc-700 dark:bg-zinc-800 dark:text-zinc-100 dark:focus:ring-pink-900/40"
          />
          <p class="text-xs text-zinc-400">视频合集的数据库 ID</p>
        </div>
        <div class="space-y-1">
          <label class="text-xs font-medium text-zinc-500">分集序号 <span class="text-red-500">*</span></label>
          <input
            v-model.number="form.epIndex"
            type="number"
            min="1"
            placeholder="如：1"
            class="w-full rounded-lg border border-zinc-200 bg-zinc-50 px-3 py-2 text-sm outline-none transition focus:border-pink-400 focus:ring-2 focus:ring-pink-100 dark:border-zinc-700 dark:bg-zinc-800 dark:text-zinc-100 dark:focus:ring-pink-900/40"
          />
          <p class="text-xs text-zinc-400">从 1 开始</p>
        </div>
      </div>

      <!-- 提交按钮 -->
      <button
        :disabled="!canSubmit"
        class="w-full rounded-xl bg-pink-500 py-2.5 text-sm font-semibold text-white transition hover:bg-pink-600 disabled:cursor-not-allowed disabled:opacity-40"
        @click="startUpload"
      >
        开始上传
      </button>
    </div>

    <!-- 上传进度 -->
    <div
      v-if="status !== 'idle' && status !== 'done'"
      class="space-y-4 rounded-2xl border border-zinc-200 bg-white p-6 dark:border-zinc-700 dark:bg-zinc-900"
    >
      <!-- 状态文字 -->
      <div class="flex items-center justify-between">
        <span class="text-sm font-medium text-zinc-700 dark:text-zinc-200">{{ statusLabel }}</span>
        <span class="text-sm font-semibold text-pink-500">{{ progress }}%</span>
      </div>

      <!-- 进度条 -->
      <div class="h-2 w-full overflow-hidden rounded-full bg-zinc-100 dark:bg-zinc-800">
        <div
          class="h-full rounded-full bg-gradient-to-r from-pink-400 to-pink-600 transition-all duration-300"
          :style="{ width: `${progress}%` }"
        />
      </div>

      <!-- 分片详情 -->
      <p class="text-xs text-zinc-400">
        文件：{{ selectedFile?.name }}（{{ formatSize(selectedFile?.size ?? 0) }}）
      </p>

      <!-- 错误信息 -->
      <div v-if="status === 'error'" class="rounded-lg bg-red-50 p-3 dark:bg-red-900/20">
        <p class="text-xs text-red-600 dark:text-red-400">{{ errorMsg }}</p>
        <button
          class="mt-2 text-xs font-medium text-red-600 underline hover:text-red-800 dark:text-red-400"
          @click="retryUpload"
        >
          重新上传
        </button>
      </div>
    </div>

    <!-- 上传成功 -->
    <div
      v-if="status === 'done'"
      class="rounded-2xl border border-emerald-200 bg-emerald-50 p-6 dark:border-emerald-800/50 dark:bg-emerald-900/20"
    >
      <div class="flex items-center gap-3">
        <div class="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-emerald-100 dark:bg-emerald-900/60">
          <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5 text-emerald-600" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
          </svg>
        </div>
        <div>
          <p class="text-sm font-semibold text-emerald-700 dark:text-emerald-300">上传成功！</p>
          <p class="text-xs text-emerald-600/70 dark:text-emerald-400/70">视频已入库，分集 ID：{{ result?.episodeId || '（秒传）' }}</p>
        </div>
      </div>

      <div class="mt-4 rounded-lg bg-white/60 p-3 dark:bg-black/20">
        <p class="text-xs text-zinc-500">视频地址：</p>
        <p class="mt-0.5 break-all text-xs font-mono text-zinc-700 dark:text-zinc-300">
          http://localhost:8080{{ result?.url }}
        </p>
      </div>

      <button
        class="mt-4 w-full rounded-xl border border-emerald-300 py-2 text-sm font-medium text-emerald-700 transition hover:bg-emerald-100 dark:border-emerald-700 dark:text-emerald-300 dark:hover:bg-emerald-900/40"
        @click="resetAll"
      >
        继续上传
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useChunkUpload, type MergeResult } from '../../composables/useChunkUpload'

const CHUNK_SIZE = 5 * 1024 * 1024

// ── 状态 ────────────────────────────────────────────────────────────
const fileInputRef = ref<HTMLInputElement | null>(null)
const isDragging = ref(false)
const selectedFile = ref<File | null>(null)
const result = ref<MergeResult | null>(null)

const form = ref({
  title: '',
  videoId: null as number | null,
  epIndex: 1,
})

const { upload, progress, status, errorMsg, reset } = useChunkUpload()

// ── 计算属性 ─────────────────────────────────────────────────────────
const chunkCount = computed(() =>
  selectedFile.value ? Math.ceil(selectedFile.value.size / CHUNK_SIZE) : 0
)

const canSubmit = computed(() =>
  !!selectedFile.value &&
  form.value.title.trim().length > 0 &&
  !!form.value.videoId &&
  form.value.videoId > 0 &&
  form.value.epIndex >= 1
)

const statusLabel = computed(() => {
  const map: Record<string, string> = {
    hashing:  '正在计算文件指纹（MD5）…',
    checking: '正在校验秒传…',
    uploading: '正在上传分片…',
    merging:  '正在合并分片…',
    error:    '上传出错',
  }
  return map[status.value] ?? ''
})

// ── 文件选择 ─────────────────────────────────────────────────────────
function onFileChange(e: Event) {
  const input = e.target as HTMLInputElement
  if (input.files?.[0]) setFile(input.files[0])
}

function onDrop(e: DragEvent) {
  isDragging.value = false
  const file = e.dataTransfer?.files[0]
  if (file?.type.startsWith('video/')) {
    setFile(file)
  }
}

function setFile(file: File) {
  selectedFile.value = file
  // 用文件名（去扩展名）预填标题
  if (!form.value.title) {
    form.value.title = file.name.replace(/\.[^.]+$/, '')
  }
}

function clearFile() {
  selectedFile.value = null
  if (fileInputRef.value) fileInputRef.value.value = ''
}

// ── 上传 ─────────────────────────────────────────────────────────────
async function startUpload() {
  if (!canSubmit.value || !selectedFile.value) return
  result.value = null
  try {
    result.value = await upload(selectedFile.value, {
      videoId: form.value.videoId!,
      epIndex: form.value.epIndex,
      title: form.value.title.trim(),
    })
  } catch {
    // 错误已由 composable 记录
  }
}

async function retryUpload() {
  reset()
  await startUpload()
}

function resetAll() {
  reset()
  selectedFile.value = null
  result.value = null
  form.value = { title: '', videoId: null, epIndex: 1 }
  if (fileInputRef.value) fileInputRef.value.value = ''
}

// ── 工具函数 ─────────────────────────────────────────────────────────
function formatSize(bytes: number): string {
  if (bytes === 0) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return (bytes / Math.pow(1024, i)).toFixed(1) + ' ' + units[i]
}
</script>

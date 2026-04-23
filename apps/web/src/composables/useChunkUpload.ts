import { ref } from 'vue'
import SparkMD5 from 'spark-md5'

// ── 常量 ────────────────────────────────────────────────────────────
const CHUNK_SIZE = 5 * 1024 * 1024 // 5MB
const CONCURRENT = 3               // 并发上传数
const MAX_RETRY = 3                // 单片最大重试次数
const API_BASE = 'http://localhost:8080/api/upload'

// ── 类型 ────────────────────────────────────────────────────────────
export interface ChunkTask {
  index: number
  blob: Blob
  status: 'pending' | 'uploading' | 'done' | 'error'
  retryCount: number
}

export interface MergeOptions {
  md5: string
  filename: string
  totalChunks: number
  videoId: number
  epIndex: number
  title: string
}

export interface MergeResult {
  url: string
  size: number
  episodeId: number
}

// ── Composable ──────────────────────────────────────────────────────
export function useChunkUpload() {
  const progress = ref(0)        // 0 ~ 100
  const status = ref<'idle' | 'hashing' | 'checking' | 'uploading' | 'merging' | 'done' | 'error'>('idle')
  const errorMsg = ref('')

  // ── 计算文件 MD5 ──────────────────────────────────────────────────
  function calcMd5(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const spark = new SparkMD5.ArrayBuffer()
      const reader = new FileReader()
      const chunkCount = Math.ceil(file.size / CHUNK_SIZE)
      let current = 0

      function loadNext() {
        const start = current * CHUNK_SIZE
        const end = Math.min(start + CHUNK_SIZE, file.size)
        reader.readAsArrayBuffer(file.slice(start, end))
      }

      reader.onload = (e) => {
        spark.append(e.target!.result as ArrayBuffer)
        current++
        if (current < chunkCount) {
          loadNext()
        } else {
          resolve(spark.end())
        }
      }
      reader.onerror = () => reject(new Error('MD5 计算失败'))
      loadNext()
    })
  }

  // ── 秒传检查 ──────────────────────────────────────────────────────
  async function checkExist(md5: string, filename: string): Promise<string | null> {
    const res = await fetch(`${API_BASE}/check?md5=${encodeURIComponent(md5)}&filename=${encodeURIComponent(filename)}`)
    const json = await res.json()
    if (json.data?.exist) return json.data.url as string
    return null
  }

  // ── 上传单个分片（含重试） ────────────────────────────────────────
  async function uploadChunk(task: ChunkTask, md5: string): Promise<void> {
    while (task.retryCount < MAX_RETRY) {
      try {
        task.status = 'uploading'
        const form = new FormData()
        form.append('md5', md5)
        form.append('chunkIndex', String(task.index))
        form.append('file', task.blob, `chunk_${task.index}`)
        const res = await fetch(`${API_BASE}/chunk`, { method: 'POST', body: form })
        const json = await res.json()
        if (json.code !== 200) throw new Error(json.msg)
        task.status = 'done'
        return
      } catch (e) {
        task.retryCount++
        task.status = task.retryCount >= MAX_RETRY ? 'error' : 'pending'
        if (task.retryCount >= MAX_RETRY) throw e
        // 指数退避：200ms, 400ms, 800ms
        await new Promise(r => setTimeout(r, 200 * Math.pow(2, task.retryCount - 1)))
      }
    }
  }

  // ── 并发上传队列 ──────────────────────────────────────────────────
  async function uploadAllChunks(tasks: ChunkTask[], md5: string, onProgress: (done: number) => void): Promise<void> {
    let done = 0
    const queue = [...tasks]

    async function worker() {
      while (queue.length > 0) {
        const task = queue.shift()!
        await uploadChunk(task, md5)
        done++
        onProgress(done)
      }
    }

    const workers = Array.from({ length: Math.min(CONCURRENT, tasks.length) }, () => worker())
    await Promise.all(workers)

    const failed = tasks.filter(t => t.status === 'error')
    if (failed.length > 0) {
      throw new Error(`${failed.length} 个分片上传失败（已重试 ${MAX_RETRY} 次）`)
    }
  }

  // ── 合并分片 ──────────────────────────────────────────────────────
  async function mergeChunks(opts: MergeOptions): Promise<MergeResult> {
    const res = await fetch(`${API_BASE}/merge`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(opts),
    })
    const json = await res.json()
    if (json.code !== 200) throw new Error(json.msg)
    return json.data as MergeResult
  }

  // ── 主入口 ────────────────────────────────────────────────────────
  /**
   * 完整的上传流程：哈希 → 秒传检查 → 分片上传 → 合并
   *
   * @param file     要上传的文件
   * @param opts     合并选项（videoId / epIndex / title）
   * @returns        合并结果（url / size / episodeId）；若秒传直接返回 { url, size:0, episodeId:0 }
   */
  async function upload(
    file: File,
    opts: Pick<MergeOptions, 'videoId' | 'epIndex' | 'title'>
  ): Promise<MergeResult> {
    try {
      errorMsg.value = ''
      progress.value = 0

      // Step 1: 计算 MD5
      status.value = 'hashing'
      const md5 = await calcMd5(file)

      // Step 2: 秒传检查
      status.value = 'checking'
      const existUrl = await checkExist(md5, file.name)
      if (existUrl) {
        progress.value = 100
        status.value = 'done'
        return { url: existUrl, size: file.size, episodeId: 0 }
      }

      // Step 3: 切片
      const chunkCount = Math.ceil(file.size / CHUNK_SIZE)
      const tasks: ChunkTask[] = Array.from({ length: chunkCount }, (_, i) => ({
        index: i,
        blob: file.slice(i * CHUNK_SIZE, Math.min((i + 1) * CHUNK_SIZE, file.size)),
        status: 'pending',
        retryCount: 0,
      }))

      // Step 4: 并发上传
      status.value = 'uploading'
      await uploadAllChunks(tasks, md5, (done) => {
        // 上传阶段占 90%（留 10% 给合并）
        progress.value = Math.round((done / chunkCount) * 90)
      })

      // Step 5: 合并
      status.value = 'merging'
      const result = await mergeChunks({
        md5,
        filename: file.name,
        totalChunks: chunkCount,
        ...opts,
      })
      progress.value = 100
      status.value = 'done'
      return result

    } catch (e: unknown) {
      status.value = 'error'
      errorMsg.value = e instanceof Error ? e.message : String(e)
      throw e
    }
  }

  function reset() {
    progress.value = 0
    status.value = 'idle'
    errorMsg.value = ''
  }

  return { upload, progress, status, errorMsg, reset }
}

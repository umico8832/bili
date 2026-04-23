/**
 * DPlayer 的最简类型声明
 * 仅覆盖项目中实际用到的 API；完整类型请见 https://dplayer.diygod.dev/
 */
declare module 'dplayer' {
  export interface DPlayerDanmakuItem {
    text: string
    color?: string
    type?: 'right' | 'top' | 'bottom'
    time?: number
    author?: string
  }

  export interface DPlayerOptions {
    container: HTMLElement
    autoplay?: boolean
    theme?: string
    loop?: boolean
    lang?: string
    screenshot?: boolean
    hotkey?: boolean
    preload?: 'none' | 'metadata' | 'auto'
    volume?: number
    mutex?: boolean
    video: {
      url: string
      pic?: string
      thumbnails?: string
      type?: string
    }
    danmaku?:
      | boolean
      | {
          id: string
          api?: string
          token?: string
          maximum?: number
          addition?: string[]
          user?: string
          bottom?: string
          unlimited?: boolean
        }
  }

  export default class DPlayer {
    constructor(options: DPlayerOptions)
    play(): void
    pause(): void
    seek(time: number): void
    switchVideo(video: DPlayerOptions['video'], danmaku?: DPlayerOptions['danmaku']): void
    destroy(): void
    danmaku?: {
      draw(item: DPlayerDanmakuItem): void
      send(item: DPlayerDanmakuItem, callback?: () => void): void
      clear(): void
    }
    notice(text: string, time?: number, opacity?: number): void
    on(event: string, handler: (...args: unknown[]) => void): void
  }
}

import vue from '@vitejs/plugin-vue'
import { defineConfig } from 'vite'
import path from 'path'
import { fileURLToPath } from 'url'

// 兼容 ESM 环境的 __dirname
const __dirname = path.dirname(fileURLToPath(import.meta.url))

// Vite 构建配置：开发服务器代理后端接口，避免跨域
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5173,
    host: '0.0.0.0',
    proxy: {
      // 接口请求代理到 Spring Boot 后端
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true
      },
      // 上传文件访问代理
      '/uploads': {
        target: 'http://localhost:8081',
        changeOrigin: true
      }
    }
  }
})

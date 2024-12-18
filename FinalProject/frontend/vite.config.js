import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server : {
    host : '0.0.0.0',
    port : 5173,
  },
  // https할 땐 아래 꺼.
  // server: {
  //   port: 5173,
  //   https: {
  //     key: fs.readFileSync('./localhost-key.pem'),
  //     cert: fs.readFileSync('./localhost.pem'),
  //   },
  //   host: '0.0.0.0', // 이 설정을 통해 외부 기기에서도 접근 가능하게 함
  // },
})

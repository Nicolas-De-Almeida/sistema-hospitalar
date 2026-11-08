import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [
    react(), 
    tailwindcss()
  ],
  server: {
    // Tudo que o front pedir em /api é repassado para o Spring Boot (porta 8080)
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
})
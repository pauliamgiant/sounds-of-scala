import { defineConfig } from "vite";

export default defineConfig({
  plugins: [],

  server: {
    port: 8080,
    open: true,
    watch: {
      include: ["js/target/**/*.js"],
    },
  },

  build: {
    outDir: "dist",
  },

  publicDir: "js",
});

import { cloudflare } from "@cloudflare/vite-plugin";
import { defineConfig } from "vite";

// El plugin de Cloudflare lee cloudflare.config.ts y empaqueta el Worker para `cf deploy`.
export default defineConfig({
  plugins: [cloudflare()],
});

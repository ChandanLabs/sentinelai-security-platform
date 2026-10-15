import type { Config } from "tailwindcss";

const config: Config = {
  content: [
    "./src/pages/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/components/**/*.{js,ts,jsx,tsx,mdx}",
    "./src/app/**/*.{js,ts,jsx,tsx,mdx}",
  ],
  theme: {
    extend: {
      colors: {
        background: "#080d1a",
        surface: "#0e1628",
        "surface-2": "#131e35",
        border: "#1e2d4a",
        primary: "#6366f1",
        success: "#22c55e",
        warning: "#f59e0b",
        danger: "#ef4444",
        text: "#e8edf5",
        muted: "#64748b",
        "muted-2": "#475569",
      },
    },
  },
  plugins: [],
};
export default config;

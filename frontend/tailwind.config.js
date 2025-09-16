/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      fontFamily: {
        sans: ["'Noto Sans SC'", 'sans-serif'],
        serif: ["'Noto Serif SC'", 'serif'],
      },
      colors: {
        primary: {
          DEFAULT: '#ff6b35',
          light: '#ff8e53',
        },
        dark: '#0f2b46',
        accent: '#1a508b',
      },
      boxShadow: {
        card: '0 8px 30px rgba(0, 33, 71, 0.08)',
        cardHover: '0 12px 40px rgba(0, 33, 71, 0.15)',
      },
    },
  },
  plugins: [],
}


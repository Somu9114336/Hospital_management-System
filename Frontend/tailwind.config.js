/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        ink: '#173a43',
        muted: '#637c82',
        teal: {
          50: '#effaf8',
          100: '#d8f2ed',
          500: '#1e9b8a',
          600: '#167d70',
          700: '#12665c',
        },
      },
      boxShadow: {
        card: '0 12px 38px rgba(28, 76, 80, 0.08)',
      },
    },
  },
  plugins: [],
};

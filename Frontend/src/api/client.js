import axios from 'axios';

const TOKEN_KEY = 'hms_patient_token';
const USER_KEY = 'hms_patient_user';
const PUBLIC_ENDPOINTS = new Set(['/user/register', '/user/login']);

export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY);
  if (token && !PUBLIC_ENDPOINTS.has(config.url)) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && localStorage.getItem(TOKEN_KEY)) {
      localStorage.removeItem(TOKEN_KEY);
      localStorage.removeItem(USER_KEY);
      window.dispatchEvent(new Event('hms:unauthorized'));
    }
    return Promise.reject(error);
  },
);

export const authStorage = { TOKEN_KEY, USER_KEY };

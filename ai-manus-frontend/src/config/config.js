


const isDev = process.env.NODE_ENV === 'development';

const API_BASE_URL = isDev
  ? 'http://localhost:8123/api'
  : 'https://your-production-api.com/api';

export default API_BASE_URL;
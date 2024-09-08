import axios from 'axios';
import Cookies from 'js-cookie';

const API_URL = process.env.NEXT_PUBLIC_API_URL;
const AUTH_TOKEN = 'authToken';

const axiosInstance = axios.create({
    baseURL: API_URL,
});

axiosInstance.interceptors.request.use(
    (config) => {
        const token = Cookies.get(AUTH_TOKEN);
        if (token && !config.url?.startsWith('/authentication/')) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error: Error) => {
        return Promise.reject(error);
    }
);

export default axiosInstance;
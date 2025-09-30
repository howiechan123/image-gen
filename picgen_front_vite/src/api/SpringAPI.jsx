import axios from "axios";

import { useToken } from "../Components/TokenContext";

const SpringAPI = axios.create({
  baseURL: import.meta.env.VITE_API_SPRING_BASE_URL,
  withCredentials: true,
});

const noInterceptor = axios.create({
  baseURL: import.meta.env.VITE_API_SPRING_BASE_URL,
  withCredentials: true,
});

let requestInterceptor;
let responseInterceptor;

export const setupInterceptors = (tokenContext) => {
  // Eject old interceptors to avoid stacking
  if (requestInterceptor !== undefined) {
    SpringAPI.interceptors.request.eject(requestInterceptor);
  }
  if (responseInterceptor !== undefined) {
    SpringAPI.interceptors.response.eject(responseInterceptor);
  }

  // Request interceptor
  requestInterceptor = SpringAPI.interceptors.request.use((config) => {
    const token = tokenContext.token;
    if (token) config.headers["Authorization"] = `Bearer ${token}`;
    return config;
  });

  // Response interceptor
  responseInterceptor = SpringAPI.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config;
      if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;
        console.log("XXXXX");
        try {
          const refreshRes = await noInterceptor.post("/public/auth/refresh");
          const newToken = refreshRes.data.token;

          tokenContext.changeToken(newToken);

        // Retry original request with new token
        originalRequest.headers = {
          ...originalRequest.headers,
          Authorization: `Bearer ${newToken}`,
        };
          console.log(refreshRes.data);
          return SpringAPI(originalRequest);
        } catch (refreshError) {
          console.log(refreshError);
          window.location.href = "/login";
          return Promise.reject(refreshError);
        }
      }
      return Promise.reject(error);
    }
  );
};

export default SpringAPI;

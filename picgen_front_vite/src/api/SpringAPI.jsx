import axios from "axios";

import { useNavigate } from "react-router-dom";

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
let curToken = null;

export const setupInterceptors = (tokenContext, navigate) => {

  if (requestInterceptor !== undefined) {
    SpringAPI.interceptors.request.eject(requestInterceptor);
  }
  if (responseInterceptor !== undefined) {
    SpringAPI.interceptors.response.eject(responseInterceptor);
  }

  requestInterceptor = SpringAPI.interceptors.request.use((config) => {
    
    if (curToken) {
      config.headers["Authorization"] = `Bearer ${curToken}`;
    }
    return config;
  });


  responseInterceptor = SpringAPI.interceptors.response.use(
    (response) => response,
    async (error) => {
      const originalRequest = error.config;
      if (error.response?.status === 401 && !originalRequest._retry) {
        originalRequest._retry = true;

        try {
          const refreshRes = await noInterceptor.post("/public/auth/refresh");
          
          const newToken = refreshRes.data.token;
          const user = refreshRes.data.user;

          curToken = newToken;
          tokenContext.changeToken(newToken, user);
          originalRequest.headers["Authorization"] = `Bearer ${newToken}`;
          return SpringAPI(originalRequest);
        } catch (refreshError) {
          navigate("/login");
          return Promise.reject(refreshError);
        }
      }
      console.log("sfgdfdf")
      return Promise.reject(error);
    }
  );


  curToken = tokenContext.token;
};


export default SpringAPI;

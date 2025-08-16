import axios from "axios";
import { ElMessage } from 'element-plus';

const request = axios.create({
  baseURL: '/api',
  timeout: 600000,
});

// 添加请求拦截器
request.interceptors.request.use(
  (config) => {
    // 获取 token
    const loginUser = JSON.parse(localStorage.getItem('loginUser')); 
    if (loginUser && loginUser.token) {
      // 将 token 添加到请求头
      config.headers.token = loginUser.token;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    if (error.response && error.response.status === 401) {
      // 提示登录状态异常
      localStorage.removeItem('loginUser');
      
      // 使用 window.location 进行页面跳转
      window.location.href = '/login';
      
      ElMessage.error('登录状态异常，请重新登录');
    }
    
    return Promise.reject(error);
  }
);

export default request;
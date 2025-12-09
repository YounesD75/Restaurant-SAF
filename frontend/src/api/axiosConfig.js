import axios from 'axios';

// Configuration pour l'inventory service
export const inventoryAxios = axios.create({
  baseURL: 'http://localhost:8083/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Configuration pour l'order service
export const orderAxios = axios.create({
  baseURL: 'http://localhost:8081/api',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Intercepteur pour logging
inventoryAxios.interceptors.request.use(config => {
  console.log(`Making ${config.method?.toUpperCase()} request to ${config.url}`);
  return config;
});

orderAxios.interceptors.request.use(config => {
  console.log(`Making ${config.method?.toUpperCase()} request to ${config.url}`);
  return config;
});
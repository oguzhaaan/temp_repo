import axios, { AxiosInstance } from "axios";
import toast from "react-hot-toast";

export const reservationApi = axios.create({
  baseURL: import.meta.env.VITE_RESERVATION_API_BASE_URL,
  headers: { "Content-Type": "application/json" },
});

export const userApi = axios.create({
  baseURL: import.meta.env.VITE_USER_API_BASE_URL,
  headers: { "Content-Type": "application/json" },
});

export const paymentApi = axios.create({
  baseURL: import.meta.env.VITE_PAYMENT_API_BASE_URL,
  headers: { "Content-Type": "application/json" },
});

// Error interceptor
const attachErrorInterceptor = (instance: AxiosInstance) => {
  instance.interceptors.response.use(
    (res) => res,
    (err) => {
      const message =
        err.response?.data?.detail || err.message || "Unexpected error";
      toast.error(message);
      console.error(err);
      return Promise.reject(new Error(message));
    },
  );
};

// Apply to instances
[reservationApi, userApi, paymentApi].forEach(attachErrorInterceptor);

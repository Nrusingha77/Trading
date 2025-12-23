import axios from "axios";


export const API_BASE_URL = "http://localhost:5454";


const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});


api.interceptors.request.use(
  (config) => {
    // Get JWT from localStorage
    let jwt = localStorage.getItem("jwt");

    // FIX: Remove quotes if token was stored as a JSON string (Common root cause of 403)
    if (jwt && jwt.startsWith('"') && jwt.endsWith('"')) {
      jwt = jwt.slice(1, -1);
    }

    // FIX: Remove "Bearer " prefix if it exists in the stored token (Prevents "Bearer Bearer token")
    if (jwt && jwt.startsWith("Bearer ")) {
      jwt = jwt.substring(7);
    }
    
    // Check if token is structurally valid
    if (jwt && jwt.split(".").length === 3 && jwt !== "undefined" && jwt !== "null") {
      // Add Authorization header
      config.headers.Authorization = `Bearer ${jwt}`;
    } else {
      // CRITICAL: Ensure no garbage header is sent
      delete config.headers["Authorization"];
      delete config.headers.Authorization;
    }
    
    return config;
  },
  (error) => {
    console.error("Request interceptor error:", error);
    return Promise.reject(error);
  }
);


api.interceptors.response.use(
  (response) => {
    // Failsafe: Auto-save token if the backend returns one (fixes login issues)
    const token = response.data?.jwt || response.data?.token || response.data?.accessToken;
    if (token) {
      localStorage.setItem("jwt", token);
    }
    return response;
  },
  (error) => {
    if (error.response?.status === 403) {
      console.error("403 Forbidden - Access Denied (Role Mismatch or Invalid Token)");
      // DO NOT remove token here. 403 means authenticated but not authorized.
      // Optionally redirect to a "Not Authorized" page instead of login.
    } else if (error.response?.status === 401) {
      console.error("401 Unauthorized - Please login again");
      if (!window.location.pathname.includes("/auth")) {
        localStorage.removeItem("jwt");
        window.location.href = "/auth";
      }
    }
    return Promise.reject(error);
  }
);


export default api;

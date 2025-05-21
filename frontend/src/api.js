import axios from "axios";
import { auth } from "./firebase";

const baseURL = process.env.REACT_APP_API_BASE_URL;

const API = axios.create({
  baseURL,
});

API.interceptors.request.use(async (config) => {
  const user = auth.currentUser;
  if (user) {
    const token = await user.getIdToken();
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export { API };

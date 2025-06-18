import axios from "axios";
import { API_URL } from "./dataProvider";

interface LoginParams {
  username: string;
  password: string;
}
type GoogleLoginParams = {
  id_token: string;
};
interface CheckParamsErr {
  status: number;
  response?: {
    data?: {
      message?: string;
    };
  };
  message?: string;
}
// authProvider.ts

const authProvider = {
  login: async (params: LoginParams | GoogleLoginParams) => {
    const { username, password, id_token } = params as LoginParams &
      GoogleLoginParams;
    try {
      let response;
      if (id_token) {
        response = await axios.post(
          `${API_URL}/auth/google`,
          {
            token: id_token,
          },
          {
            headers: {
              "Content-Type": "application/json",
            },
            withCredentials: true,
          },
        );
      } else {
        response = await axios.post(
          `${API_URL}/auth/login`,
          {
            username: username,
            password: password,
          },
          {
            headers: {
              "Content-Type": "application/json",
            },
            withCredentials: true,
          },
        );
      }
      // Store the JWT token in local storage
      const token = response.data["jwt-token"];
      localStorage.setItem("token", token);
      const responseUser = await axios.get(`${API_URL}/public/users/infor`, {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        withCredentials: true,
      });
      const roles = responseUser.data.roles;
      const roleArray = roles.map((role: any) => role.roleName);
      console.log("roles:: ", roleArray);
      localStorage.setItem("role", JSON.stringify(roleArray));
      return Promise.resolve();
    } catch (error) {
      console.error("Login error:   ", error);
      return Promise.reject(new Error("Đăng nhập thất bại. Vui lòng thử lại."));
      //   return Promise.reject(error);
    }
  },

  logout: () => {
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    return Promise.resolve();
  },

  checkAuth: () => {
    const token = localStorage.getItem("token");
    const roles = JSON.parse(localStorage.getItem("role") || "[]");

    if (
      token &&
      roles &&
      (roles.includes("STAFF") || roles.includes("ADMIN"))
    ) {
      return Promise.resolve();
    }

    return Promise.reject("Tài khoản không có quyền truy cập");
  },

  getPermissions: () => {
    const roleString = localStorage.getItem("role");
    if (!roleString) return Promise.reject();

    try {
      const roles = JSON.parse(roleString); // dạng ["ADMIN", "STAFF"]
      return Promise.resolve(roles);
    } catch (error) {
      return Promise.reject();
    }
  },
  checkError: (error: CheckParamsErr) => {
    if (error.status === 401) {
      console.error("Error refreshing token:", error);
      localStorage.removeItem("token");
      localStorage.removeItem("role");
      return Promise.reject();
    } else if (error.status === 403) {
      throw new Error("Bạn không có quyền truy cập thông tin này");
    } else if (error.status === 400) {
      const message =
        error.response?.data?.message || "Dữ liệu không hợp lệ hoặc trùng lặp";
      throw new Error(message);
    }
    return Promise.resolve();
  },
};

export default authProvider;

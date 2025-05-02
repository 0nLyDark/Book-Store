import axios from "axios";
import { API_URL, httpClient } from "./dataProvider";

interface LoginParams {
  username: string;
  password: string;
}
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
  login: async ({ username, password }: LoginParams) => {
    try {
      const response = await axios.post(
        "http://localhost:8080/api/auth/login",
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
      // Store the JWT token in local storage
      const token = response.data["jwt-token"];
      localStorage.setItem("token", token);
      const responseUser = await axios.get(
        "http://localhost:8080/api/public/users/infor",
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          withCredentials: true,
        },
      );
      const roles = responseUser.data.roles;
      const roleArray = roles.map((role: any) => role.roleName);
      console.log("roles:: ", roleArray);
      localStorage.setItem("role", JSON.stringify(roleArray));
      return Promise.resolve();
    } catch (error) {
      return Promise.reject(
        new Error("Sai tài khoản hoặc mật khẩu. Vui lòng thử lại."),
      );
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
    return Promise.reject();
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
      throw new Error("Dữ liệu không hợp lệ hoặc trùng lặp");
    }
    return Promise.resolve();
  },
};

export default authProvider;

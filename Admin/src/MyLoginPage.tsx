import { Login, LoginForm, useLogin, useNotify } from "react-admin";
import { GoogleLogin } from "@react-oauth/google";
import { Box, Typography } from "@mui/material";

const MyLoginPage = () => {
  const notify = useNotify();
  const login = useLogin();

  return (
    <Login
      sx={{
        backgroundImage: "url('/image/bg.jpg')",
        backgroundSize: "cover",
        backgroundPosition: "center",
        height: "100vh",
      }}
    >
      <Box
        display="flex"
        flexDirection="column"
        alignItems="center"
        gap={2}
        p={2}
      >
        {/* Giữ form login truyền thống */}
        <LoginForm />

        {/* Hoặc gắn thêm đoạn text chia cách */}
        <Typography variant="body2" color="textSecondary">
          hoặc đăng nhập bằng
        </Typography>

        {/* Nút đăng nhập Google */}
        <GoogleLogin
          onSuccess={async (credentialResponse) => {
            const id_token = credentialResponse.credential;
            if (id_token) {
              await login({
                id_token: id_token,
              });
            } else {
              console.error("ID token is undefined");
            }
          }}
          onError={() => {
            console.error("Đăng nhập Google thất bại");
          }}
        />
      </Box>
    </Login>
  );
};

export default MyLoginPage;

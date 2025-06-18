import {
  Box,
  Dialog,
  DialogContent,
  DialogTitle,
  IconButton,
  MenuItem,
  Select,
  Typography,
} from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import {
  Button,
  Datagrid,
  DateField,
  FunctionField,
  ImageField,
  Labeled,
  ListContextProvider,
  ListControllerSuccessResult,
  NumberField,
  Show,
  SimpleShowLayout,
  TextField,
  useGetOne,
  useNotify,
  useRecordContext,
  useRefresh,
} from "react-admin";
import axiosInstance from "../../api";
import { useEffect, useState } from "react";
const ROLES = [
  { id: 101, name: "ADMIN" },
  { id: 102, name: "USER" },
  { id: 103, name: "STAFF" },
];

const CartByUser = ({ userId }: { userId: number }) => {
  const { data, isLoading, error } = useGetOne("carts", { id: userId });
  if (isLoading) return <div>Loading...</div>;
  if (error) return <div>Error: {error.message}</div>;

  const listContext: ListControllerSuccessResult<any> = {
    data: data.cartItems, // array
    total: data.cartItems.length,
    error: null,
    isPending: false,
    resource: "cartItems",
    selectedIds: [],
    onSelect: () => {},
    onToggleItem: () => {},
    onUnselectItems: () => {},
    setSort: () => {},
    setPage: () => {},
    setPerPage: () => {},
    filterValues: {},
    displayedFilters: {},
    showFilter: () => {},
    hideFilter: () => {},
    setFilters: () => {},
    page: 1,
    perPage: 10,
    onSelectAll: () => {},
    refetch: () => {},
    sort: { field: "id", order: "ASC" as "ASC" | "DESC" },
  };

  return (
    <ListContextProvider value={listContext}>
      <Datagrid
        rowClick={false}
        sx={{
          "& .RaDatagrid-headerCell": {
            textAlign: "center",
          },
        }}
      >
        <TextField source="product.isbn" label="Mã ISBN" />
        <TextField source="product.productName" label="Tên sản phẩm" />
        <ImageField source="product.images[0]" label="Hình ảnh" />
        <NumberField
          source="product.price"
          options={{ style: "currency", currency: "VND" }}
          locales="vi-VN"
          label="Giá"
        />
        <FunctionField
          label="Giảm giá"
          render={(record: any) => (
            <Box sx={{ textAlign: "center", width: "100%" }}>
              {record.product.discount} %
            </Box>
          )}
        />
        <FunctionField
          label="Số lượng"
          render={(record: any) => (
            <Box sx={{ textAlign: "center", width: "100%" }}>
              {record.quantity}
            </Box>
          )}
        />
        <FunctionField
          label="Tổng giá"
          render={(record) => (
            <Box sx={{ textAlign: "right", width: "100%" }}>
              {(
                (record.quantity *
                  record.product.price *
                  (100 - record.product.discount)) /
                100
              ).toLocaleString("vi-VN", {
                style: "currency",
                currency: "VND",
              })}
            </Box>
          )}
        />
      </Datagrid>
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          p: 2,
          borderTop: "1px solid #ccc",
        }}
      >
        <Typography variant="h5">Tổng tiền :</Typography>
        <Typography variant="h5">
          {data?.totalPrice?.toLocaleString("vi-VN", {
            style: "currency",
            currency: "VND",
          }) || "0 ₫"}
        </Typography>
      </Box>
    </ListContextProvider>
  );
};

const UserEdit = () => {
  const record = useRecordContext();
  const refresh = useRefresh();
  const [open, setOpen] = useState(false);
  const [openCart, setOpenCart] = useState(false);
  const [openRole, setOpenRole] = useState(false);
  const [status, setStatus] = useState(record?.enabled);
  const [roleId, setRoleId] = useState<number | null>(null);
  const [newPassword, setNewPassword] = useState("");

  const notify = useNotify();

  const changeStatusAccount = () => {
    const data = {
      userId: record?.userId,
      status: !status,
    };
    axiosInstance
      .post(`/admin/users/status`, data)
      .then((res) => {
        notify("ignore_key", {
          type: "success",
          messageArgs: {
            _: status ? "Khóa tài khoản thành công" : "Mở tài khoản thành công",
          },
        });

        setStatus(!status);
      })
      .catch((error) => {
        console.log(error);
        const message = error.response?.data?.message || "Có lỗi xảy ra";
        notify(message, { type: "warning" });
      });
  };
  const changeRoleAccount = () => {
    const data = {
      userId: record?.userId,
      roleId: roleId,
    };
    axiosInstance
      .put(`/admin/users/role`, data)
      .then((res) => {
        const message = res.data.message || "Thay đổi quyền hạn thành công";
        notify(message, { type: "success" });
        refresh();
        setOpenRole(false);
      })
      .catch((error) => {
        console.log(error);
        const message = error.response?.data?.message || "Có lỗi xảy ra";
        notify(message, { type: "warning" });
      });
  };

  const handleResetPassword = () => {
    if (newPassword.trim().length < 1) {
      notify("ignore_key", {
        type: "error",
        messageArgs: {
          _: "Mật khẩu không hợp lệ, phải có ít nhất 1 ký tự",
        },
      });
      return;
    }
    const data = {
      userId: record?.userId,
      newPassword: newPassword,
    };
    axiosInstance
      .post(`/admin/users/password`, data)
      .then((res) => {
        notify("ignore_key", {
          type: "success",
          messageArgs: {
            _: "Đặt lại mật khẩu thành công",
          },
        });
        setNewPassword("");
        setOpen(false);
      })
      .catch((error) => {
        console.log(error);
        const message = error.response?.data?.message || "Có lỗi xảy ra";
        notify(message, { type: "warning" });
      });
  };
  useEffect(() => {
    document.title = "Thông tin tài khoản";
  }, []);
  return (
    <>
      <Box>
        <Labeled label="Quyền hạn">
          <FunctionField
            render={() =>
              Array.isArray(record?.roles) && record.roles.length > 0
                ? record.roles
                    .map((role: any) => role.roleName || role)
                    .join(" | ")
                : "Không có quyền"
            }
          />
        </Labeled>
      </Box>
      <Labeled label="Trạng thái">
        <FunctionField render={() => (status ? "Hoạt động" : "Bị khóa")} />
      </Labeled>
      <Box mt={1}>
        <Button onClick={() => setOpenCart(true)} label="Xem giỏ hàng" />
      </Box>
      <Box mt={1}>
        <Button
          onClick={changeStatusAccount}
          label={status ? "Khóa tài khoản" : "Mở tài khoản"}
        />
      </Box>
      <Box mt={1}>
        <Button onClick={() => setOpenRole(true)} label="Thay đổi quyền" />
      </Box>
      <Box mt={1}>
        <Button onClick={() => setOpen(true)} label="Đặt lại mật khẩu" />
      </Box>
      <Dialog
        open={open}
        onClose={() => setOpen(false)}
        fullWidth
        maxWidth="sm"
        sx={{ borderRadius: 25 }}
      >
        <DialogTitle sx={{ textAlign: "center" }}>
          Đặt lại mật khẩu mới
          <IconButton
            aria-label="close"
            onClick={() => setOpen(false)}
            sx={{
              position: "absolute",
              right: 8,
              top: 8,
              color: (theme) => theme.palette.grey[500],
            }}
          >
            <CloseIcon />
          </IconButton>
        </DialogTitle>
        <DialogContent>
          <Box
            display="flex"
            justifyContent="center"
            alignItems="center"
            flexDirection="column"
            sx={{ mt: 2 }}
          >
            <input
              type="password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              minLength={1}
              placeholder="Nhập mật khẩu mới"
              style={{
                padding: "10px",
                width: "100%",
                maxWidth: "400px",
                borderRadius: "8px",
                border: "1px solid #ccc",
                fontSize: "16px",
              }}
            />
            <Button
              sx={{ mt: 2 }}
              onClick={handleResetPassword}
              label="Xác nhận"
            />
          </Box>
        </DialogContent>
      </Dialog>
      <Dialog
        open={openRole}
        onClose={() => setOpenRole(false)}
        fullWidth
        maxWidth="sm"
        sx={{ borderRadius: 25 }}
      >
        <DialogTitle sx={{ textAlign: "center" }}>
          Thay đổi quyền hạn
          <IconButton
            aria-label="close"
            onClick={() => setOpenRole(false)}
            sx={{
              position: "absolute",
              right: 8,
              top: 8,
              color: (theme) => theme.palette.grey[500],
            }}
          >
            <CloseIcon />
          </IconButton>
        </DialogTitle>
        <DialogContent>
          <Box
            display="flex"
            justifyContent="center"
            alignItems="center"
            flexDirection="column"
            sx={{ mt: 2 }}
          >
            <Select
              value={roleId}
              onChange={(e) => setRoleId(Number(e.target.value))}
              sx={{ width: "100%", maxWidth: 400 }}
            >
              {ROLES.map((role) => (
                <MenuItem key={role.id} value={role.id}>
                  {role.name}
                </MenuItem>
              ))}
            </Select>
            <Button
              sx={{ mt: 2 }}
              onClick={changeRoleAccount}
              label="Xác nhận"
            />
          </Box>
        </DialogContent>
      </Dialog>
      <Dialog
        open={openCart}
        onClose={() => setOpenCart(false)}
        fullWidth
        PaperProps={{
          sx: {
            width: "100%",
            minWidth: 1200,
            borderRadius: 3,
          },
        }}
      >
        <DialogTitle sx={{ textAlign: "center", fontWeight: "bold" }}>
          Thông tin giỏ hàng
          <IconButton
            aria-label="close"
            onClick={() => setOpenCart(false)}
            sx={{
              position: "absolute",
              right: 8,
              top: 8,
              color: (theme) => theme.palette.grey[500],
            }}
          >
            <CloseIcon />
          </IconButton>
        </DialogTitle>
        <DialogContent>
          <CartByUser userId={record?.userId} />
        </DialogContent>
      </Dialog>
    </>
  );
};

const UserShow = () => (
  <Show>
    <Box display="flex" flexDirection="row" gap={2} flexWrap="wrap" p={5}>
      <SimpleShowLayout flex={1} minWidth={250}>
        <TextField source="userId" label="User ID" />
        <TextField source="fullName" label="Họ tên" />
        <TextField source="email" label="Email" />
        <TextField source="mobileNumber" label="Số điện thoại" />
        <ImageField source="avatar" label="ảnh đại diện" />
        <FunctionField
          label="Địa chỉ"
          render={(record) =>
            record.address
              ? `${record.address.buildingName}, ${record.address.ward}, ${record.address.district}, ${record.address.city}`
              : "Không có địa chỉ"
          }
        />
      </SimpleShowLayout>
      <SimpleShowLayout flex={1} minWidth={250}>
        <TextField source="accountType" label="Kiểu tài khoản" />
        <DateField source="createdAt" label="Ngày tạo" />
        <FunctionField
          label="Xác thực tài khoản"
          render={(record) =>
            record.verified ? "Đã xác thực" : "Chưa xác thực"
          }
        />
        <UserEdit />
      </SimpleShowLayout>
    </Box>
  </Show>
);
export default UserShow;

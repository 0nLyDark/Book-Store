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
import {
  ArrayField,
  Button,
  Datagrid,
  DateField,
  FunctionField,
  ImageField,
  Labeled,
  NumberField,
  Show,
  SimpleShowLayout,
  TextField,
  TopToolbar,
  useNotify,
  useRecordContext,
  useRefresh,
} from "react-admin";
import ExportPDFButton from "../../components/ExportPDFButton";
import { formatOrderStatus } from "../../utils/OrderStatus";
import { useEffect, useState } from "react";
import CloseIcon from "@mui/icons-material/Close";
import axiosInstance from "../../api";

const PaymentCodeField = () => {
  const record = useRecordContext();
  if (!record || record.payment?.paymentMethod === "COD") {
    return null;
  }

  return <TextField source="payment.paymentCode" label="Mã thanh toán" />;
};

const CustomPDFButton = () => {
  const record = useRecordContext();
  if (!record) {
    return <span>Loading ...</span>;
  }

  if (!record.id) {
    return <span>No ImportReceipt ID</span>;
  }
  console.log("record", record);

  return <ExportPDFButton type="order" data={record} />;
};
const ShowActions = () => (
  <TopToolbar>
    <CustomPDFButton />
  </TopToolbar>
);
const ORDER_STATUSES = ["COMPLETED", "SHIPPED", "PAID", "FAILED"];

const OrderStatus = () => {
  useEffect(() => {
    document.title = "Chi tiết đơn hàng";
  }, []);
  const record = useRecordContext();
  const [open, setOpen] = useState(false);
  const [selectedStatus, setSelectedStatus] = useState(record?.status || "");
  const notify = useNotify();
  const refresh = useRefresh();

  const handleChangeStatus = async () => {
    const data = {
      orderId: record?.orderId,
      orderStatus: selectedStatus,
    };
    axiosInstance
      .put("/staff/orders/status", data)
      .then((res) => {
        notify("Đã cập nhật trạng thái đơn hàng", { type: "success" });
        refresh();
        setOpen(false);
      })
      .catch((error) => {
        console.log(error);
        const message = error.response?.data?.message || "Có lỗi xảy ra";
        notify(message, { type: "warning" });
      });
  };

  return (
    <>
      <Labeled label="Trạng thái đơn hàng">
        <FunctionField
          render={() => (
            <Typography>{formatOrderStatus(record?.orderStatus)}</Typography>
          )}
        />
      </Labeled>

      <Box mt={1}>
        <Button
          onClick={() => setOpen(true)}
          label="Thay đổi trạng thái đơn hàng"
        />
      </Box>

      <Dialog
        open={open}
        onClose={() => setOpen(false)}
        fullWidth
        maxWidth="sm"
      >
        <DialogTitle sx={{ textAlign: "center" }}>
          Thay đổi trạng thái đơn hàng
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
            flexDirection="column"
            alignItems="center"
            sx={{ mt: 2 }}
          >
            <Select
              value={selectedStatus}
              onChange={(e) => setSelectedStatus(e.target.value)}
              sx={{ width: "100%", maxWidth: 400 }}
            >
              {ORDER_STATUSES.map((status) => (
                <MenuItem key={status} value={status}>
                  {formatOrderStatus(status)}
                </MenuItem>
              ))}
            </Select>

            <Button
              sx={{ mt: 2, p: 1, font: 18 }}
              variant="contained"
              onClick={handleChangeStatus}
              label="Xác nhận"
            />
          </Box>
        </DialogContent>
      </Dialog>
    </>
  );
};
const OrderShow = () => (
  <Show actions={<ShowActions />}>
    <Box display="flex" flexDirection="row" gap={2} flexWrap="wrap" px={2}>
      <SimpleShowLayout flex={1} minWidth={150}>
        <TextField source="orderCode" label="Mã đơn hàng" />
        {/* <TextField source="user.userId" label="User ID" /> */}
        <TextField source="deliveryName" label="Tên người nhận" />
        <TextField source="deliveryPhone" label="Số điện thoại" />
        <TextField source="email" label="Email" />
        <DateField source="orderDateTime" label="Ngày đặt hàng" showTime />
        <FunctionField
          label="Giá trị đơn hàng"
          render={(record) =>
            record.subTotal.toLocaleString("vi-VN", {
              style: "currency",
              currency: "VND",
            })
          }
        />
        <FunctionField
          label="Phí vận chuyển"
          render={(record) =>
            record.priceShip.toLocaleString("vi-VN", {
              style: "currency",
              currency: "VND",
            })
          }
        />
        <FunctionField
          label="Tổng tiền"
          render={(record) =>
            record.totalAmount.toLocaleString("vi-VN", {
              style: "currency",
              currency: "VND",
            })
          }
        />
      </SimpleShowLayout>
      <SimpleShowLayout flex={1} minWidth={150}>
        <FunctionField
          label="Voucher áp dụng"
          render={(record) => {
            return (
              <div>
                {record.coupon ? (
                  <div>
                    <div>Tên : {record.coupon.promotionName}</div>
                    <div>Mã : {record.coupon.promotionCode}</div>
                  </div>
                ) : (
                  "Không có thông tin"
                )}
              </div>
            );
          }}
        />
        <FunctionField
          label="Freeship áp dụng"
          render={(record) => {
            return (
              <div>
                {record.freeship ? (
                  <div>
                    <div>Tên : {record.freeship.promotionName}</div>
                    <div>Mã : {record.freeship.promotionCode}</div>
                  </div>
                ) : (
                  "Không có thông tin"
                )}
              </div>
            );
          }}
        />
        <TextField
          source="payment.paymentMethod"
          label="Phương thức thanh toán"
        />
        <PaymentCodeField />
        {/* <TextField source="payment.paymentCode" label="Mã thanh toán" /> */}
        <TextField source="orderType" label="Kiểu đơn hàng" />
        <FunctionField
          label="Trạng thái đơn hàng"
          render={(record) => formatOrderStatus(record.orderStatus)}
          sortBy="orderStatus"
        />
        <OrderStatus />
      </SimpleShowLayout>
    </Box>
    <SimpleShowLayout>
      <ArrayField source="orderItems" label="Sản phẩm trong đơn hàng">
        <Datagrid bulkActionButtons={false} rowClick={false}>
          <TextField source="product.productId" label="Product ID" />
          <TextField source="product.productName" label="Tên sản phẩm" />
          <TextField source="product.isbn" label="Mã sản phẩm" />
          <ImageField
            source="product.images[0]"
            label="Hình ảnh"
            title="Tên sản phẩm"
          />
          <FunctionField
            label="Danh mục"
            render={(record) => {
              return (
                <div>
                  {record.product.categories.map(
                    (category: any, index: number) => (
                      <div key={index}>{category.categoryName}</div>
                    ),
                  )}
                </div>
              );
            }}
          />
          <NumberField
            source="price"
            label="Giá"
            options={{ style: "currency", currency: "VND" }}
            locales="vi-VN"
          />
          <NumberField source="quantity" label="Số lượng" />
          <FunctionField
            label="% Giảm giá"
            render={(record) => `${record.discount ?? 0} %`}
            sortBy="discount"
          />
          <FunctionField
            label="Tổng giá"
            render={(record) =>
              (
                ((record.product?.price ?? 0) *
                  (record.quantity ?? 0) *
                  (100 - (record.discount ?? 0))) /
                100
              ).toLocaleString("vi-VN", {
                style: "currency",
                currency: "VND",
              })
            }
          />
        </Datagrid>
      </ArrayField>
    </SimpleShowLayout>
  </Show>
);
export default OrderShow;

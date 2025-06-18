import {
  Datagrid,
  DateField,
  FunctionField,
  List,
  ShowButton,
  TextField,
} from "react-admin";
import { formatOrderStatus } from "../../utils/OrderStatus";
import { useEffect } from "react";

const OrderList = () => {
  useEffect(() => {
    document.title = "Danh sách đơn hàng";
  }, []);
  return (
    <List>
      <Datagrid>
        {/* <TextField source="orderId" label="Order ID" /> */}
        <TextField source="orderCode" label="Mã đơn hàng" />
        {/* <TextField source="user.userId" label="User ID" /> */}
        <TextField source="deliveryName" label="Tên người nhận" />
        <TextField source="deliveryPhone" label="Số điện thoại" />
        <TextField source="email" label="Email" />
        <DateField source="orderDateTime" label="Ngày đặt hàng" showTime />
        <FunctionField
          label="Tổng tiền"
          render={(record) =>
            record.totalAmount.toLocaleString("vi-VN", {
              style: "currency",
              currency: "VND",
            })
          }
          sortBy="totalAmount"
        />
        <FunctionField
          label="Trạng thái đơn hàng"
          render={(record) => formatOrderStatus(record.orderStatus)}
          sortBy="orderStatus"
        />
        <TextField source="orderType" label="Kiểu đơn hàng" />
      </Datagrid>
    </List>
  );
};

export default OrderList;

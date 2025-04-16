import {
  Datagrid,
  FunctionField,
  List,
  ShowButton,
  TextField,
} from "react-admin";

const CartList = () => {
  return (
    <List>
      <Datagrid>
        <TextField source="cartId" label="Cart ID" />
        <TextField source="user.userId" label="User ID" />
        <TextField source="user.fullName" label="Tên người dùng" />
        <TextField source="user.email" label="Email" />
        <TextField source="user.mobileNumber" label="Số điện thoại" />
        <FunctionField
          label="Tổng tiền"
          render={(record) => {
            const totalPrice = record.cartItems.reduce(
              (total: number, item: any) => {
                return (
                  total +
                  (item.product.price *
                    item.quantity *
                    (100 - item.product.discount)) /
                    100
                );
              },
              0,
            );
            return (
              <span>
                {totalPrice.toLocaleString("vi-VN", {
                  style: "currency",
                  currency: "VND",
                })}
              </span>
            );
          }}
        />
        <ShowButton />
      </Datagrid>
    </List>
  );
};

export default CartList;

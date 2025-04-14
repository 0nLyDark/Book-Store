import {
  Datagrid,
  List,
  NumberField,
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
        <NumberField
          source="totalPrice"
          label="Tổng tiền"
          options={{ style: "currency", currency: "VND" }}
          locales="vi-VN"
        />
        <ShowButton />
      </Datagrid>
    </List>
  );
};

export default CartList;

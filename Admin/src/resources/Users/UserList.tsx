import { useEffect } from "react";
import {
  BooleanField,
  Datagrid,
  DateField,
  DeleteButton,
  EditButton,
  FunctionField,
  List,
  TextField,
  Title,
} from "react-admin";

const UserList = () => {
  useEffect(() => {
    document.title = "Danh sách tài khoản";
  }, []);
  return (
    <List title={<Title title="Danh sách tài khoản" />}>
      <Datagrid>
        <TextField source="userId" label="User ID" />
        <TextField source="fullName" label="Họ tên" />
        <TextField source="email" label="Email" />
        <TextField source="accountType" label="Kiểu tài khoản" />
        <FunctionField
          label="Trạng thái"
          render={(record) => (record.enabled ? "Hoạt động" : "Bị khóa")}
          sortBy="enabled"
        />
        <DateField source="createdAt" label="Ngày tạo" />
        {/* <DeleteButton /> */}
      </Datagrid>
    </List>
  );
};

export default UserList;

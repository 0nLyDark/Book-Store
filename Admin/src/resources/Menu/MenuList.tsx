import { useEffect } from "react";
import {
  Datagrid,
  DeleteButton,
  EditButton,
  FunctionField,
  List,
  TextField,
} from "react-admin";

const MenuList = () => {
  useEffect(() => {
    document.title = "Danh sách menu";
  }, []);
  return (
    <List>
      <Datagrid>
        <TextField source="menuId" label="ID" />
        <TextField source="name" label="Tên menu" />
        <TextField source="link" label="Đường dẫn liên kết" />
        <TextField source="parent.name" label="Menu cha" />
        <TextField source="type" label="Kiểu" />
        <TextField source="position" label="Vị trí" />
        <FunctionField
          label="Trạng thái"
          render={(record) => (record.status ? "Hiện" : "Ẩn")}
          sortBy="status"
        />
        <EditButton />
        <DeleteButton />
      </Datagrid>
    </List>
  );
};

export default MenuList;

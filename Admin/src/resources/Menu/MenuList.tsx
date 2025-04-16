import {
  Datagrid,
  DeleteButton,
  EditButton,
  FunctionField,
  List,
  TextField,
} from "react-admin";

const MenuList = () => {
  return (
    <List>
      <Datagrid>
        <TextField source="menuId" label="ID" />
        <TextField source="name" label="Tên menu" />
        <TextField source="link" label="Đường dẫn liên kết" />
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

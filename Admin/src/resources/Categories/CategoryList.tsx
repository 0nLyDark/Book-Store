import { useEffect } from "react";
import {
  BooleanField,
  Datagrid,
  DeleteButton,
  EditButton,
  FunctionField,
  ImageField,
  List,
  TextField,
  TextInput,
} from "react-admin";
const categoryFields = [
  <TextInput label="Tìm theo tên" source="keyword" alwaysOn />,
];
const CategoryList = () => {
  useEffect(() => {
    document.title = "Danh sách danh mục";
  }, []);
  return (
    <List filters={categoryFields}>
      <Datagrid>
        <TextField source="categoryId" label="ID" />
        <TextField source="categoryName" label="Tên danh mục" />
        <TextField source="parent.categoryName" label="Danh mục cha" />
        <ImageField source="image" label="Hình ảnh" />
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

export default CategoryList;

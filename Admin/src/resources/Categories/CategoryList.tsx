import {
  BooleanField,
  Datagrid,
  DeleteButton,
  EditButton,
  FunctionField,
  ImageField,
  List,
  TextField,
} from "react-admin";

const CategoryList = () => {
  return (
    <List>
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

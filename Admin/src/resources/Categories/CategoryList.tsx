import {
  BooleanField,
  Datagrid,
  DeleteButton,
  EditButton,
  FunctionField,
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

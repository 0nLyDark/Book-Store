import {
  BooleanField,
  DateField,
  FunctionField,
  Show,
  SimpleShowLayout,
  TextField,
} from "react-admin";

const CategoryShow = () => (
  <Show>
    <SimpleShowLayout>
      <TextField source="categoryId" label="Category ID" />
      <TextField source="categoryName" label="Tên danh mục" />
      <TextField source="slug" label="Slug" />
       <FunctionField
          label="Trạng thái"
          render={(record) => (record.status ? "Hiện" : "Ẩn")}
          sortBy="status"
        />
      <TextField source="parent.categoryName" label="Danh mục cha" />
      <TextField source="createdBy" label="ID người tạo" />
      <DateField source="createdAt" label="Ngày tạo" showTime />
      <TextField source="updatedBy" label="ID người cập nhật" />
      <DateField source="updatedAt" label="Ngày cập nhật" showTime />
    </SimpleShowLayout>
  </Show>
);
export default CategoryShow;

import {
  BooleanField,
  DateField,
  FunctionField,
  ImageField,
  Show,
  SimpleShowLayout,
  TextField,
} from "react-admin";

const AuthorShow = () => (
  <Show>
    <SimpleShowLayout>
      <TextField source="authorId" label="Author ID" />
      <TextField source="authorName" label="Tên tác giả" />
      <ImageField source="image" title="Hình ảnh" label="Hình ảnh" />
       <FunctionField
          label="Trạng thái"
          render={(record) => (record.status ? "Hiện" : "Ẩn")}
          sortBy="status"
        />
      <FunctionField
        label="Mô tả"
        render={(record) => record?.description ?? "Không có mô tả"}
      />
      <TextField source="createdBy" label="ID người tạo" />
      <DateField source="createdAt" label="Ngày tạo" showTime />
      <TextField source="updatedBy" label="ID người cập nhật" />
      <DateField source="updatedAt" label="Ngày cập nhật" showTime />
    </SimpleShowLayout>
  </Show>
);
export default AuthorShow;

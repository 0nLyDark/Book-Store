import {
  BooleanField,
  DateField,
  FunctionField,
  ImageField,
  Show,
  SimpleShowLayout,
  TextField,
} from "react-admin";

const BannerShow = () => (
  <Show>
    <SimpleShowLayout>
      <TextField source="bannerId" label="Banner ID" />
      <TextField source="bannerName" label="Tên tác giả" />
      <ImageField source="image" title="Hình ảnh" label="Hình ảnh" />
      <TextField source="link" label="Link liên kết" />
      <TextField source="position" label="Vị trí" />
       <FunctionField
          label="Trạng thái"
          render={(record) => (record.status ? "Hiện" : "Ẩn")}
          sortBy="status"
        />

      <TextField source="createdBy" label="ID người tạo" />
      <DateField source="createdAt" label="Ngày tạo" showTime />
      <TextField source="updatedBy" label="ID người cập nhật" />
      <DateField source="updatedAt" label="Ngày cập nhật" showTime />
    </SimpleShowLayout>
  </Show>
);
export default BannerShow;

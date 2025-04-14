import {
  BooleanField,
  DateField,
  FunctionField,
  Show,
  SimpleShowLayout,
  TextField,
} from "react-admin";

const PublisherShow = () => (
  <Show>
    <SimpleShowLayout>
      <TextField source="publisherId" label="Publisher ID" />
      <TextField source="publisherName" label="Tên nhà sản xuất" />
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
export default PublisherShow;

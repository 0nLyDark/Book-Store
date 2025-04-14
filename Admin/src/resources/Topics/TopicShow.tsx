import {
  BooleanField,
  DateField,
  FunctionField,
  Show,
  SimpleShowLayout,
  TextField,
} from "react-admin";

const TopicShow = () => (
  <Show>
    <SimpleShowLayout>
      <TextField source="topicId" label="Topic ID" />
      <TextField source="topicName" label="Tên chủ đề" />
      <TextField source="slug" label="Slug" />
       <FunctionField
          label="Trạng thái"
          render={(record) => (record.status ? "Hiện" : "Ẩn")}
          sortBy="status"
        />
      <TextField source="description" label="Mô tả" />
      <TextField source="createdBy" label="ID người tạo" />
      <DateField source="createdAt" label="Ngày tạo" showTime />
      <TextField source="updatedBy" label="ID người cập nhật" />
      <DateField source="updatedAt" label="Ngày cập nhật" showTime />
    </SimpleShowLayout>
  </Show>
);
export default TopicShow;

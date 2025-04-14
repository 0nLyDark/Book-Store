import {
  BooleanField,
  DateField,
  FunctionField,
  Show,
  SimpleShowLayout,
  TextField,
} from "react-admin";

const PostShow = () => (
  <Show>
    <SimpleShowLayout>
      <TextField source="postId" label="Post ID" />
      <TextField source="title" label="Tiêu đề" />
      <TextField source="topic.topicName" label="Chủ đề" />
      <TextField source="type" label="Kiểu bài viết" />
       <FunctionField
          label="Trạng thái"
          render={(record) => (record.status ? "Hiện" : "Ẩn")}
          sortBy="status"
        />
      <TextField source="content" label="Nội dung" />
      <TextField source="description" label="Mô tả" />
      <TextField source="createdBy" label="ID người tạo" />
      <DateField source="createdAt" label="Ngày tạo" showTime />
      <TextField source="updatedBy" label="ID người cập nhật" />
      <DateField source="updatedAt" label="Ngày cập nhật" showTime />
    </SimpleShowLayout>
  </Show>
);
export default PostShow;

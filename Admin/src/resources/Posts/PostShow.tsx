import { Box } from "@mui/material";
import { useEffect } from "react";
import {
  BooleanField,
  DateField,
  FunctionField,
  ImageField,
  Show,
  SimpleShowLayout,
  TextField,
} from "react-admin";

const PostShow = () => {
  useEffect(() => {
    document.title = "Chi tiết bài viết";
  }, []);
  return (
    <Show>
      <Box display="flex" flexDirection="row" gap={2} flexWrap="wrap" px={2}>
        <SimpleShowLayout flex={1} minWidth={250}>
          <TextField source="title" label="Tiêu đề" />
          <TextField source="slug" label="Slug" />
          <TextField source="topic.topicName" label="Chủ đề" />
          <ImageField source="image" label="Hình ảnh" />
          <TextField source="type" label="Kiểu bài viết" />
          <FunctionField
            label="Trạng thái"
            render={(record) => (record.status ? "Hiện" : "Ẩn")}
            sortBy="status"
          />
        </SimpleShowLayout>

        <SimpleShowLayout flex={1} minWidth={250}>
          <TextField source="createdBy" label="ID người tạo" />
          <DateField source="createdAt" label="Ngày tạo" showTime />
          <TextField source="updatedBy" label="ID người cập nhật" />
          <DateField source="updatedAt" label="Ngày cập nhật" showTime />
        </SimpleShowLayout>
      </Box>
      <SimpleShowLayout flex={2} px={2}>
        <FunctionField
          label="Nội dung"
          render={(record) => (
            <div
              dangerouslySetInnerHTML={{ __html: record.content }}
              style={{ whiteSpace: "normal" }}
            />
          )}
        />
      </SimpleShowLayout>
    </Show>
  );
};
export default PostShow;

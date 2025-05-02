import { Box } from "@mui/material";
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
    <Box display="flex" flexDirection="row" gap={2} flexWrap="wrap" p={2}>
      <SimpleShowLayout flex={1} minWidth={250}>
        <TextField source="topicName" label="Tên chủ đề" />
        <TextField source="slug" label="Slug" />
        <FunctionField
          label="Trạng thái"
          render={(record) => (record.status ? "Hiện" : "Ẩn")}
          sortBy="status"
        />
        <TextField source="description" label="Mô tả" />
      </SimpleShowLayout>
      <SimpleShowLayout flex={1} minWidth={250}>
        <TextField source="createdBy" label="ID người tạo" />
        <DateField source="createdAt" label="Ngày tạo" showTime />
        <TextField source="updatedBy" label="ID người cập nhật" />
        <DateField source="updatedAt" label="Ngày cập nhật" showTime />
      </SimpleShowLayout>
    </Box>
  </Show>
);
export default TopicShow;

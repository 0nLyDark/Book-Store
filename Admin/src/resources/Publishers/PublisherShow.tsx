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

const PublisherShow = () => {
  useEffect(() => {
    document.title = "Chi tiết nhà xuất bản";
  }, []);
  return (
    <Show>
      <Box display="flex" flexDirection="row" gap={2} flexWrap="wrap" p={2}>
        <SimpleShowLayout flex={1} minWidth={150}>
          <TextField source="publisherName" label="Tên nhà sản xuất" />
          <ImageField source="image" label="Hình ảnh" />
          <FunctionField
            label="Trạng thái"
            render={(record) => (record.status ? "Hiện" : "Ẩn")}
            sortBy="status"
          />
        </SimpleShowLayout>
        <SimpleShowLayout flex={1} minWidth={150}>
          <TextField source="createdBy" label="ID người tạo" />
          <DateField source="createdAt" label="Ngày tạo" showTime />
          <TextField source="updatedBy" label="ID người cập nhật" />
          <DateField source="updatedAt" label="Ngày cập nhật" showTime />
        </SimpleShowLayout>
      </Box>
    </Show>
  );
};
export default PublisherShow;

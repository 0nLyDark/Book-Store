import { Box } from "@mui/material";
import {
  DateField,
  FunctionField,
  ImageField,
  Show,
  SimpleShowLayout,
  TextField,
} from "react-admin";

const AuthorShow = () => (
  <Show>
    <Box display="flex" flexDirection="row" gap={2} flexWrap="wrap" px={2}>
      <Box flex={1} minWidth={150}>
        <SimpleShowLayout>
          <TextField source="authorName" label="Tên tác giả" />
          <ImageField source="image" title="Hình ảnh" label="Hình ảnh" />
          <FunctionField
            label="Trạng thái"
            render={(record) => (record.status ? "Hiện" : "Ẩn")}
            sortBy="status"
          />
        </SimpleShowLayout>
      </Box>
      <Box flex={1} minWidth={150}>
        <SimpleShowLayout>
          <TextField source="createdBy" label="ID người tạo" />
          <DateField source="createdAt" label="Ngày tạo" showTime />
          <TextField source="updatedBy" label="ID người cập nhật" />
          <DateField source="updatedAt" label="Ngày cập nhật" showTime />
        </SimpleShowLayout>
      </Box>
    </Box>
    <SimpleShowLayout minWidth={300} px={2}>
      <FunctionField
        label="Mô tả"
        render={(record) => record?.description ?? "Không có mô tả"}
      />
    </SimpleShowLayout>
  </Show>
);
export default AuthorShow;

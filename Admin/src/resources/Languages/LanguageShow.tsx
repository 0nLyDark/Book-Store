import { Box } from "@mui/material";
import {
  BooleanField,
  DateField,
  FunctionField,
  Show,
  SimpleShowLayout,
  TextField,
} from "react-admin";

const LanguageShow = () => (
  <Show>
    <Box display="flex" flexDirection="row" gap={2} flexWrap="wrap" p={2}>
      <Box flex={1} minWidth={150}>
        <SimpleShowLayout>
          <TextField source="languageId" label="Language ID" />
          <TextField source="name" label="Tên ngôn ngữ" />
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
  </Show>
);
export default LanguageShow;

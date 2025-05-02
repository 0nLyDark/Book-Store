import { Box } from "@mui/material";
import {
  BooleanField,
  DateField,
  EmailField,
  FunctionField,
  Show,
  SimpleShowLayout,
  TextField,
} from "react-admin";

const SupplierShow = () => (
  <Show>
    <Box display="flex" flexDirection="row" gap={2} flexWrap="wrap" p={2}>
      <SimpleShowLayout flex={1} minWidth={150}>
        <TextField source="supplierName" label="Tên nhà cung cấp" />
        <EmailField source="email" label="Email" />
        <TextField source="mobieNumber" label="Số điện thoại" />
        <TextField source="address" label="Địa chỉ" />
      </SimpleShowLayout>
      <SimpleShowLayout flex={1} minWidth={150}>
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
    </Box>
  </Show>
);
export default SupplierShow;

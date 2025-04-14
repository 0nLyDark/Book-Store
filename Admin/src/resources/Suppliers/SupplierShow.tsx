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
    <SimpleShowLayout>
      <TextField source="supplierId" label="Supplier ID" />
      <TextField source="supplierName" label="Tên nhà cung cấp" />
      <EmailField source="email" label="Email" />
      <TextField source="mobieNumber" label="Số điện thoại" />
      <TextField source="address" label="Địa chỉ" />
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
export default SupplierShow;

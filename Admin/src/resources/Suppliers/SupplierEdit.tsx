import { BooleanInput, Edit, regex, SimpleForm, TextInput } from "react-admin";
const SupplierEdit = () => (
  <Edit>
    <SimpleForm>
      <TextInput source="supplierId" label="Supplier ID" disabled />
      <TextInput source="supplierName" label="Tên nhà cung cấp" />
      <TextInput source="email" label="Email" />
      <TextInput
        source="mobieNumber"
        label="Số điện thoại"
        validate={[regex(/^\d{10}$/, "Số điện thoại phải có đúng 10 chữ số")]}
      />
      <TextInput source="address" label="Địa chỉ" />
      <BooleanInput source="status" label="Trạng thái" />
    </SimpleForm>
  </Edit>
);
export default SupplierEdit;

import { useEffect } from "react";
import { Create, regex, SimpleForm, TextInput } from "react-admin";

const SupplierCreate = () => {
  useEffect(() => {
    document.title = "Tạo mới nhà cung cấp";
  }, []);
  return (
    <Create mutationMode="pessimistic">
      <SimpleForm>
        <TextInput source="supplierName" label="Tên nhà cung cấp" />
        <TextInput source="email" label="Email" />
        <TextInput
          source="mobieNumber"
          label="Số điện thoại"
          validate={[regex(/^\d{10}$/, "Số điện thoại phải có đúng 10 chữ số")]}
        />
        <TextInput source="address" label="Địa chỉ" />
      </SimpleForm>
    </Create>
  );
};

export default SupplierCreate;

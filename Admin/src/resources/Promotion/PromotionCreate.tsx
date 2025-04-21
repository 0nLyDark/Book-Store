import {
  Create,
  DateInput,
  DateTimeInput,
  NumberInput,
  regex,
  SelectInput,
  SimpleForm,
  TextInput,
} from "react-admin";

const SupplierCreate = () => (
  <Create>
    <SimpleForm>
      <TextInput source="promotionName" label="Tên phiếu giảm giá" />
      <TextInput source="promotionCode" label="mã giảm giá" />
      <SelectInput
        label="Loại phiếu giảm giá"
        source="promotionType"
        choices={[
          { id: "VOUCHER", name: "VOUCHER" },
          { id: "FREESHIP", name: "FREESHIP" },
        ]}
      />
      <DateTimeInput source="startDate" label="Ngày bắt đầu" />
      <DateTimeInput source="endDate" label="Ngày kết thúc" />
      <NumberInput source="value" label="giá trị" />
      <NumberInput source="valueApply" label="giá trị áp dụng" />
      <SelectInput
        label="Kiểu giảm giá"
        source="valueType"
        choices={[
          { id: true, name: "Kiểu %" },
          { id: false, name: "Kiểu số" },
        ]}
      />
      <TextInput source="description" label="Mô tả" multiline />
    </SimpleForm>
  </Create>
);

export default SupplierCreate;

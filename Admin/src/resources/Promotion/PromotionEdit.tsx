import {
  BooleanInput,
  DateInput,
  DateTimeInput,
  Edit,
  NumberInput,
  regex,
  SelectInput,
  SimpleForm,
  TextInput,
} from "react-admin";
const PromotionEdit = () => (
  <Edit>
    <SimpleForm>
      <TextInput source="promotionId" label="ID phiếu giảm giá" readOnly />
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
        source="valueType"
        choices={[
          { id: true, name: "Kiểu %" },
          { id: false, name: "Kiểu số" },
        ]}
      />
      <BooleanInput source="status" label="Trạng thái" />
      <TextInput source="description" label="Mô tả" multiline />
    </SimpleForm>
  </Edit>
);
export default PromotionEdit;

import {
  BooleanField,
  DateField,
  EmailField,
  FunctionField,
  NumberField,
  Show,
  SimpleShowLayout,
  TextField,
} from "react-admin";

const PromotionShow = () => (
  <Show>
    <SimpleShowLayout>
      <TextField source="promotionId" label="ID" />
      <TextField source="promotionName" label="Tên phiếu giảm giá" />
      <TextField source="promotionCode" label="mã giảm giá" />
      <TextField source="promotionType" label="Loại phiếu giảm giá" />
      <DateField source="startDate" label="Ngày bắt đầu" />
      <DateField source="startDaendDatete" label="Ngày kết thúc" />
      <NumberField source="value" label="giá trị" />
      <NumberField source="valueApply" label="giá trị áp dụng" />
      <FunctionField
        label="Kiểu giá trị"
        render={(record) => (record.valueType ? "%" : "Số")}
      />
      <FunctionField
        label="Trạng thái"
        render={(record) => (record.status ? "Hiện" : "Ẩn")}
      />
      <TextField source="createdBy" label="ID người tạo" />
      <DateField source="createdAt" label="Ngày tạo" showTime />
      <TextField source="updatedBy" label="ID người cập nhật" />
      <DateField source="updatedAt" label="Ngày cập nhật" showTime />
      <TextField source="description" label="Mô tả" />
    </SimpleShowLayout>
  </Show>
);
export default PromotionShow;

import {
  BooleanField,
  Datagrid,
  DateField,
  DeleteButton,
  EditButton,
  EmailField,
  FunctionField,
  List,
  NumberField,
  TextField,
} from "react-admin";

const PromotionList = () => {
  return (
    <List>
      <Datagrid>
        <TextField source="promotionId" label="ID" />
        <TextField source="promotionName" label="Tên phiếu giảm giá" />
        <TextField source="promotionCode" label="mã giảm giá" />
        <TextField source="promotionType" label="Loại phiếu giảm giá" />
        <DateField source="startDate" label="Ngày bắt đầu" />
        <DateField source="endDate" label="Ngày kết thúc" />
        <NumberField source="value" label="giá trị" />
        <NumberField source="valueApply" label="giá trị áp dụng" />
        <FunctionField
          label="Kiểu giá trị"
          render={(record) => (record.valueType ? "%" : "Số")}
          sortBy="valueType"
        />
        <FunctionField
          label="Trạng thái"
          render={(record) => (record.status ? "Hiện" : "Ẩn")}
          sortBy="status"
        />
        <EditButton />
        <DeleteButton />
      </Datagrid>
    </List>
  );
};

export default PromotionList;

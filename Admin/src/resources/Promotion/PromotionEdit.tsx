import { Box } from "@mui/material";
import { useEffect } from "react";
import {
  BooleanInput,
  DateTimeInput,
  Edit,
  NumberInput,
  SelectInput,
  SimpleForm,
  TextInput,
} from "react-admin";
const PromotionEdit = () => {
  useEffect(() => {
    document.title = "Chỉnh sửa phiếu khuyến mãi";
  }, []);
  return (
    <Edit mutationMode="pessimistic">
      <SimpleForm>
        <Box display="flex" flexDirection="row" gap={2} flexWrap="wrap" px={2}>
          <Box flex={1} minWidth={250}>
            <TextInput
              source="promotionId"
              label="ID phiếu giảm giá"
              readOnly
            />
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
          </Box>
          <Box flex={1} minWidth={250}>
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
          </Box>
        </Box>
      </SimpleForm>
    </Edit>
  );
};
export default PromotionEdit;

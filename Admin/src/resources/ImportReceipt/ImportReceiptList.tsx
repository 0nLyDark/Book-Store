import { useEffect } from "react";
import {
  Datagrid,
  DateField,
  DeleteButton,
  EditButton,
  FunctionField,
  List,
  NumberField,
  ReferenceInput,
  SelectInput,
  ShowButton,
  TextField,
  TextInput,
} from "react-admin";
const ImportReceiptFilter = [
  <TextInput label="Tìm theo User ID" source="userId" alwaysOn />,

  <ReferenceInput
    source="supplierId"
    reference="suppliers"
    label="Nhà cung cấp"
    alwaysOn
  >
    <SelectInput optionText="supplierName" label="Nhà cung cấp" />
  </ReferenceInput>,
];
const ImportReceiptList = () => {
  useEffect(() => {
    document.title = "Danh sách phiếu nhập hàng";
  }, []);
  return (
    <List
      filters={ImportReceiptFilter}
      sort={{ field: "importDate", order: "DESC" }}
    >
      <Datagrid>
        <TextField source="importReceiptId" label="ID" />
        <TextField source="supplier.supplierName" label="Nhà cung cấp" />
        <DateField source="importDate" label="Ngày nhập" showTime />
        <NumberField
          source="totalAmount"
          label="Tổng tiền"
          options={{ style: "currency", currency: "VND" }}
          locales="vi-VN"
        />

        <FunctionField
          label="Trạng thái"
          render={(record) => (record.status ? "Tốt" : "Lỗi")}
          sortBy="status"
        />
        <ShowButton />
        <DeleteButton />
      </Datagrid>
    </List>
  );
};

export default ImportReceiptList;

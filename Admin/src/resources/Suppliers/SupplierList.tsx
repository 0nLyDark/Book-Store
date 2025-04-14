import {
  BooleanField,
  Datagrid,
  DeleteButton,
  EditButton,
  EmailField,
  FunctionField,
  List,
  TextField,
} from "react-admin";

const SupplierList = () => {
  return (
    <List>
      <Datagrid>
        <TextField source="supplierId" label="ID" />
        <TextField source="supplierName" label="Tên chủ đề" />
        <EmailField source="email" label="Email" />
        <TextField source="mobieNumber" label="Số điện thoại" />
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

export default SupplierList;

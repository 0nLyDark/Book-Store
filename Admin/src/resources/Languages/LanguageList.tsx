import { useEffect } from "react";
import {
  BooleanField,
  Datagrid,
  DeleteButton,
  EditButton,
  FunctionField,
  List,
  TextField,
  TextInput,
} from "react-admin";
const languageFields = [
  <TextInput label="Tìm theo tên" source="keyword" alwaysOn />,
];
const LanguageList = () => {
  useEffect(() => {
    document.title = "Danh sách ngôn ngữ";
  }, []);
  return (
    <List filters={languageFields}>
      <Datagrid>
        <TextField source="languageId" label="ID" />
        <TextField source="name" label="Tên ngôn ngữ" />
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

export default LanguageList;

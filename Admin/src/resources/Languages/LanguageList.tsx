import {
  BooleanField,
  Datagrid,
  DeleteButton,
  EditButton,
  FunctionField,
  List,
  TextField,
} from "react-admin";

const LanguageList = () => {
  return (
    <List>
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

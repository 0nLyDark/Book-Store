import { useEffect } from "react";
import {
  BooleanField,
  Datagrid,
  DeleteButton,
  EditButton,
  FunctionField,
  ImageField,
  List,
  TextField,
  TextInput,
} from "react-admin";
const authorFields = [
  <TextInput label="Tìm theo tên" source="keyword" alwaysOn />,
];
const AuthorList = () => {
  useEffect(() => {
    document.title = "Danh sách tác giả";
  }, []);
  return (
    <List filters={authorFields}>
      <Datagrid>
        <TextField source="authorId" label="ID" />
        <TextField source="authorName" label="Tên tác giả" />
        <ImageField source="image" title="Hình ảnh" label="Hình ảnh" />
        {/* <TextField source="description" label="Mô tả" /> */}
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

export default AuthorList;

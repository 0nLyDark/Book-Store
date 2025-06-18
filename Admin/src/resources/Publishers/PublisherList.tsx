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
const publisherFields = [
  <TextInput label="Tìm theo tên" source="keyword" alwaysOn />,
];
const PublisherList = () => {
  useEffect(() => {
    document.title = "Danh sách nhà cung cấp";
  }, []);
  return (
    <List filters={publisherFields}>
      <Datagrid>
        <TextField source="publisherId" label="ID" />
        <TextField source="publisherName" label="Tên nhà sản xuất" />
        <ImageField source="image" label="Hình ảnh" />
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

export default PublisherList;

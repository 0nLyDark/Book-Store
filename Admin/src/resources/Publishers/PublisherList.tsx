import {
  BooleanField,
  Datagrid,
  DeleteButton,
  EditButton,
  FunctionField,
  ImageField,
  List,
  TextField,
} from "react-admin";

const PublisherList = () => {
  return (
    <List>
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

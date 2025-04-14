import {
  Datagrid,
  DeleteButton,
  EditButton,
  FunctionField,
  ImageField,
  List,
  TextField,
} from "react-admin";

const BannerList = () => {
  return (
    <List>
      <Datagrid>
        <TextField source="bannerId" label="ID" />
        <TextField source="bannerName" label="Tên Banner" />
        <ImageField source="image" title="Hình ảnh" label="Hình ảnh" />
        <TextField source="position" label="Vị trí" />
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

export default BannerList;

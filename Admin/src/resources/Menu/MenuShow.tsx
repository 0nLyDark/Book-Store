import { useEffect } from "react";
import {
  DateField,
  FunctionField,
  Show,
  SimpleShowLayout,
  TextField,
} from "react-admin";

const MenuShow = () => {
  useEffect(() => {
    document.title = "Chi tiết menu";
  }, []);
  return (
    <Show>
      <SimpleShowLayout>
        <TextField source="menuId" label="Menu ID" />
        <TextField source="name" label="Tên menu" />
        <TextField source="link" label="Đường dẫn liên kết" />
        <TextField source="type" label="Kiểu" />
        <TextField source="sortOrder" label="Thứ tự hiển thị" />
        <TextField source="parent.name" label="Menu cha" />
        <TextField source="position" label="Vị trí hiển thị" />
        <FunctionField
          label="Trạng thái"
          render={(record) => (record.status ? "Hiện" : "Ẩn")}
          sortBy="status"
        />
        <TextField source="createdBy" label="ID người tạo" />
        <DateField source="createdAt" label="Ngày tạo" showTime />
        <TextField source="updatedBy" label="ID người cập nhật" />
        <DateField source="updatedAt" label="Ngày cập nhật" showTime />
      </SimpleShowLayout>
    </Show>
  );
};
export default MenuShow;

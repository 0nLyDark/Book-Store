import { Box } from "@mui/material";
import { useEffect } from "react";
import {
  BooleanField,
  DateField,
  FunctionField,
  ImageField,
  Show,
  SimpleShowLayout,
  TextField,
} from "react-admin";

const CategoryShow = () => {
  useEffect(() => {
    document.title = "Chi tiết danh mục";
  }, []);
  return (
    <Show>
      <Box display="flex" flexDirection="row" gap={2} flexWrap="wrap">
        <Box flex={1} minWidth={150}>
          <SimpleShowLayout>
            <TextField source="categoryName" label="Tên danh mục" />
            <TextField source="slug" label="Slug" />
            <TextField source="parent.categoryName" label="Danh mục cha" />
            <ImageField source="image" label="Hình ảnh" />
          </SimpleShowLayout>
        </Box>
        <Box flex={1} minWidth={150}>
          <SimpleShowLayout>
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
        </Box>
      </Box>
    </Show>
  );
};
export default CategoryShow;

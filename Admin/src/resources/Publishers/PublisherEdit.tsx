import { useEffect } from "react";
import {
  BooleanInput,
  Edit,
  ImageField,
  ImageInput,
  SimpleForm,
  TextInput,
} from "react-admin";
const PublisherEdit = () => {
  useEffect(() => {
    document.title = "Chỉnh sửa nhà cung cấp";
  }, []);
  return (
    <Edit mutationMode="pessimistic">
      <SimpleForm>
        <TextInput source="publisherId" label="Publisher ID" disabled />
        <TextInput source="publisherName" label="Tên nhà sản xuất" />
        <ImageInput
          source="image"
          label="Hình ảnh"
          accept={{ "image/*": [".png", ".jpg", ".jpeg", ".gif", ".webp"] }}
        >
          <ImageField source="src" title="title" />
        </ImageInput>
        <BooleanInput source="status" label="Trạng thái" />
      </SimpleForm>
    </Edit>
  );
};
export default PublisherEdit;

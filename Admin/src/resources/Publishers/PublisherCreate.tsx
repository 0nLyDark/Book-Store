import { useEffect } from "react";
import {
  Create,
  ImageField,
  ImageInput,
  SimpleForm,
  TextInput,
} from "react-admin";

const PublisherCreate = () => {
  useEffect(() => {
    document.title = "Tạo mới nhà cung cấp";
  }, []);
  return (
    <Create mutationMode="pessimistic">
      <SimpleForm>
        <TextInput source="publisherName" label="Tên nhà sản xuất" />
        <ImageInput
          source="image"
          label="Hình ảnh"
          accept={{ "image/*": [".png", ".jpg", ".jpeg", ".gif", ".webp"] }}
        >
          <ImageField source="src" title="title" />
        </ImageInput>
      </SimpleForm>
    </Create>
  );
};

export default PublisherCreate;

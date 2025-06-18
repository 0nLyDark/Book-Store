import { useEffect } from "react";
import {
  Create,
  ImageField,
  ImageInput,
  SimpleForm,
  TextInput,
} from "react-admin";

const AuthorCreate = () => {
  useEffect(() => {
    document.title = "Tạo mới tác giả";
  }, []);
  return (
    <Create mutationMode="pessimistic">
      <SimpleForm>
        <TextInput source="authorName" label="Tên tác giả" />
        <ImageInput
          source="image"
          label="Hình ảnh"
          accept={{ "image/*": [".png", ".jpg", ".jpeg", ".gif", ".webp"] }}
        >
          <ImageField source="src" title="title" />
        </ImageInput>
        <TextInput source="description" label="Mô tả" multiline />
      </SimpleForm>
    </Create>
  );
};

export default AuthorCreate;

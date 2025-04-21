import {
  BooleanInput,
  Edit,
  ImageField,
  ImageInput,
  SimpleForm,
  TextInput,
} from "react-admin";
const AuthorEdit = () => (
  <Edit>
    <SimpleForm>
      <TextInput source="authorId" label="Author ID" readOnly />
      <TextInput source="authorName" label="Tên tác giả" />

      <ImageInput
        source="image"
        label="Hình ảnh"
        accept={{ "image/*": [".png", ".jpg", ".jpeg", ".gif", ".webp"] }}
        placeholder={"Nhấn để tải ảnh lên"}
        format={(value) =>
          typeof value === "string"
            ? { src: value, title: "Ảnh hiện tại" }
            : value
        }
      >
        <ImageField source="src" title="title" />
      </ImageInput>
      <BooleanInput source="status" label="Trạng thái" />
      <TextInput source="description" label="Mô tả" multiline />
    </SimpleForm>
  </Edit>
);
export default AuthorEdit;

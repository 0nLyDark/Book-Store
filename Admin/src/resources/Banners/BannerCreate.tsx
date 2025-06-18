import { useEffect } from "react";
import {
  Create,
  ImageField,
  ImageInput,
  SelectInput,
  SimpleForm,
  TextInput,
} from "react-admin";

const BannerCreate = () => {
  useEffect(() => {
    document.title = "Tạo mới banner";
  }, []);
  return (
    <Create mutationMode="pessimistic">
      <SimpleForm>
        <TextInput source="bannerName" label="Tên Banner" />
        <ImageInput
          source="image"
          label="Hình ảnh"
          accept={{ "image/*": [".png", ".jpg", ".jpeg", ".gif", ".webp"] }}
        >
          <ImageField source="src" title="title" />
        </ImageInput>
        <TextInput source="link" label="Link liên kết" />
        <SelectInput
          source="position"
          label="Vị trí"
          choices={[{ id: "SLIDESHOW", name: "Slide Show" }]}
        />
      </SimpleForm>
    </Create>
  );
};

export default BannerCreate;

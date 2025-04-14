import {
  Create,
  ImageField,
  ImageInput,
  SelectInput,
  SimpleForm,
  TextInput,
} from "react-admin";

const BannerCreate = () => (
  <Create>
    <SimpleForm>
      <TextInput source="bannerName" label="Tên Banner" />
      <ImageInput
        source="image"
        label="Hình ảnh"
        accept={{ "image/*": [".png", ".jpg", ".jpeg", ".gif"] }}
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

export default BannerCreate;

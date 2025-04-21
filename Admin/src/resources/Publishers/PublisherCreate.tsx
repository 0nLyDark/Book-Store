import { Create, ImageInput, SimpleForm, TextInput } from "react-admin";

const PublisherCreate = () => (
  <Create>
    <SimpleForm>
      <TextInput source="publisherName" label="Tên nhà sản xuất" />
      <ImageInput
        source="image"
        label="Hình ảnh"
        accept={{ "image/*": [".png", ".jpg", ".jpeg", ".gif", ".webp"] }}
      />
    </SimpleForm>
  </Create>
);

export default PublisherCreate;

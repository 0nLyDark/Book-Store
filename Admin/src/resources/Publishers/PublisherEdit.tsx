import { BooleanInput, Edit, ImageInput, SimpleForm, TextInput } from "react-admin";
const PublisherEdit = () => (
  <Edit>
    <SimpleForm>
      <TextInput source="publisherId" label="Publisher ID" disabled />
      <TextInput source="publisherName" label="Tên nhà sản xuất" />
      <ImageInput
        source="image"
        label="Hình ảnh"
        accept={{ "image/*": [".png", ".jpg", ".jpeg", ".gif", ".webp"] }}
      />
      <BooleanInput source="status" label="Trạng thái" />
    </SimpleForm>
  </Edit>
);
export default PublisherEdit;

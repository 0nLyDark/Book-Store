import { Create, SimpleForm, TextInput } from "react-admin";

const PublisherCreate = () => (
  <Create>
    <SimpleForm>
      <TextInput source="publisherName" label="Tên nhà sản xuất" />
    </SimpleForm>
  </Create>
);

export default PublisherCreate;

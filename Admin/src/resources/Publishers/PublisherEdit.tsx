import { BooleanInput, Edit, SimpleForm, TextInput } from "react-admin";
const PublisherEdit = () => (
  <Edit>
    <SimpleForm>
      <TextInput source="publisherId" label="Publisher ID" disabled />
      <TextInput source="publisherName" label="Tên nhà sản xuất" />
      <BooleanInput source="status" label="Trạng thái" />
    </SimpleForm>
  </Edit>
);
export default PublisherEdit;

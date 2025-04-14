import { Create, SimpleForm, TextInput } from "react-admin";

const LanguageCreate = () => (
  <Create>
    <SimpleForm>
      <TextInput source="name" label="Tên ngôn ngữ" />
    </SimpleForm>
  </Create>
);

export default LanguageCreate;

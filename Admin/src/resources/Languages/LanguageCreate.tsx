import { useEffect } from "react";
import { Create, SimpleForm, TextInput } from "react-admin";

const LanguageCreate = () => {
  useEffect(() => {
    document.title = "Tạo mới ngôn ngữ";
  }, []);
  return (
    <Create mutationMode="pessimistic">
      <SimpleForm>
        <TextInput source="name" label="Tên ngôn ngữ" />
      </SimpleForm>
    </Create>
  );
};

export default LanguageCreate;

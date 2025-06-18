import { useEffect } from "react";
import { BooleanInput, Edit, SimpleForm, TextInput } from "react-admin";
const LanguageEdit = () => {
  useEffect(() => {
    document.title = "Chỉnh sửa ngôn ngữ";
  }, []);
  return (
    <Edit mutationMode="pessimistic">
      <SimpleForm>
        <TextInput source="languageId" label="Language ID" disabled />
        <TextInput source="name" label="Tên ngôn ngữ" />
        {/* <TextInput source="slug" label="Slug" disabled /> */}
        <BooleanInput source="status" label="Trạng thái" />
      </SimpleForm>
    </Edit>
  );
};
export default LanguageEdit;

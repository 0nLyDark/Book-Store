import { useEffect } from "react";
import { BooleanInput, Edit, SimpleForm, TextInput } from "react-admin";
const TopicEdit = () => {
  useEffect(() => {
    document.title = "Chỉnh sửa chủ đề";
  }, []);
  return (
    <Edit mutationMode="pessimistic">
      <SimpleForm>
        <TextInput source="topicId" label="Topic ID" disabled />
        <TextInput source="topicName" label="Tên chủ đề" />
        <TextInput source="slug" label="Slug" disabled />
        <BooleanInput source="status" label="Trạng thái" />
        <TextInput source="description" label="Mô tả" multiline />
      </SimpleForm>
    </Edit>
  );
};
export default TopicEdit;

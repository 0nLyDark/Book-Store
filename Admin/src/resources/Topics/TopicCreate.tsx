import { useEffect } from "react";
import { Create, SimpleForm, TextInput } from "react-admin";

const TopicCreate = () => {
  useEffect(() => {
    document.title = "Tạo mới chủ đề";
  }, []);
  return (
    <Create mutationMode="pessimistic">
      <SimpleForm>
        <TextInput source="topicName" label="Tên chủ đề" />
        <TextInput source="description" label="Mô tả" multiline />
      </SimpleForm>
    </Create>
  );
};

export default TopicCreate;

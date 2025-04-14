import {
  Create,
  SimpleForm,
  TextInput,
} from "react-admin";

const TopicCreate = () => (
  <Create>
    <SimpleForm>
      <TextInput source="topicName" label="Tên chủ đề" />
      <TextInput source="description" label="Mô tả" multiline />
    </SimpleForm>
  </Create>
);

export default TopicCreate;

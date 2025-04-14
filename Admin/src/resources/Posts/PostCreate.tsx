import {
  BooleanInput,
  Create,
  ReferenceInput,
  SelectInput,
  SimpleForm,
  TextInput,
} from "react-admin";

const PostCreate = () => (
  <Create>
    <SimpleForm>
      <TextInput source="title" label="Tiêu đề" />
      <ReferenceInput source="topic.topicId" reference="topics">
        <SelectInput optionText="topicName" label="Chủ đề" variant="outlined" />
      </ReferenceInput>
      <SelectInput
        source="type"
        label="Kiểu bài viết"
        choices={[
          { id: "POST", name: "Bài viết" },
          { id: "PAGE", name: "Trang đơn" },
        ]}
      />
      <TextInput source="content" label="Nội dung" multiline />
    </SimpleForm>
  </Create>
);

export default PostCreate;

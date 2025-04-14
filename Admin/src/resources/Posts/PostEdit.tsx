import {
  BooleanInput,
  Edit,
  ReferenceInput,
  SelectInput,
  SimpleForm,
  TextInput,
} from "react-admin";
const PostEdit = () => (
  <Edit>
    <SimpleForm>
      <TextInput source="postId" label="Post ID" disabled />
      <TextInput source="title" label="Tiêu đề" />
      <ReferenceInput source="topic.topicId" reference="topics" label="Chủ đề">
        <SelectInput optionText="topicName" />
      </ReferenceInput>
      <SelectInput
        source="type"
        label="Kiểu bài viết"
        choices={[
          { id: "POST", name: "Bài viết" },
          { id: "PAGE", name: "Trang tĩnh" },
        ]}
      />
      <BooleanInput source="status" label="Trạng thái" />
      <TextInput source="content" label="Nội dung" multiline />
    </SimpleForm>
  </Edit>
);
export default PostEdit;

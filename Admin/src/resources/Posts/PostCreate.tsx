import {
  BooleanInput,
  Create,
  ReferenceInput,
  SelectInput,
  SimpleForm,
  TextInput,
} from "react-admin";
import CustomRichTextInput from "../../components/CustomRichTextInput";

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
      <CustomRichTextInput source="content" label="Nội dung bài viết" />
    </SimpleForm>
  </Create>
);

export default PostCreate;

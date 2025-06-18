import {
  AutocompleteInput,
  BooleanInput,
  Edit,
  ImageField,
  ImageInput,
  ReferenceInput,
  SelectInput,
  SimpleForm,
  TextInput,
} from "react-admin";
import CustomRichTextInput from "../../components/CustomRichTextInput";
import { useEffect } from "react";
const PostEdit = () => {
  useEffect(() => {
    document.title = "Chỉnh sửa bài viết";
  }, []);
  return (
    <Edit mutationMode="pessimistic">
      <SimpleForm>
        <TextInput source="postId" label="Post ID" disabled />
        <TextInput source="title" label="Tiêu đề" />
        <ReferenceInput source="topic.topicId" reference="topics">
          <AutocompleteInput
            optionText="topicName"
            label="Chủ đề"
            variant="outlined"
            filterToQuery={(searchText: string) => ({ keyword: searchText })}
          />
        </ReferenceInput>
        <ImageInput
          source="image"
          accept={{ "image/*": [".png", ".jpg", ".jpeg", ".gif", ".webp"] }}
          label="Hình ảnh"
        >
          <ImageField source="src" title="title" />
        </ImageInput>
        <SelectInput
          source="type"
          label="Kiểu bài viết"
          choices={[
            { id: "POST", name: "Bài viết" },
            { id: "PAGE", name: "Trang tĩnh" },
          ]}
        />
        <BooleanInput source="status" label="Trạng thái" />
        <CustomRichTextInput source="content" label="Nội dung bài viết" />
      </SimpleForm>
    </Edit>
  );
};
export default PostEdit;

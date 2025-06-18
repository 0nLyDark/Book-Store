import {
  AutocompleteInput,
  BooleanInput,
  Create,
  ImageField,
  ImageInput,
  ReferenceInput,
  SelectInput,
  SimpleForm,
  TextInput,
} from "react-admin";
import CustomRichTextInput from "../../components/CustomRichTextInput";
import { useEffect } from "react";

const PostCreate = () => {
  useEffect(() => {
    document.title = "Tạo mới bài viết";
  }, []);
  return (
    <Create mutationMode="pessimistic">
      <SimpleForm>
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
            { id: "PAGE", name: "Trang đơn" },
          ]}
        />
        <CustomRichTextInput source="content" label="Nội dung bài viết" />
      </SimpleForm>
    </Create>
  );
};

export default PostCreate;

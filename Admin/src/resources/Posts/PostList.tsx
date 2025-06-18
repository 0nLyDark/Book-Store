import { useEffect } from "react";
import {
  AutocompleteInput,
  BooleanField,
  Datagrid,
  DeleteButton,
  EditButton,
  FunctionField,
  List,
  ReferenceInput,
  SelectInput,
  TextField,
} from "react-admin";
const PostFilter = [
  // <TextInput label="Tìm theo tên" source="keyword" alwaysOn />,
  <SelectInput
    source="type"
    label="Kiểu bài viết"
    choices={[
      { id: "POST", name: "Bài viết" },
      { id: "PAGE", name: "Trang đơn" },
    ]}
    alwaysOn
  />,
];
const PostList = () => {
  useEffect(() => {
    document.title = "Danh sách bài viết";
  }, []);
  return (
    <List filters={PostFilter}>
      <Datagrid>
        <TextField source="postId" label="ID" />
        <TextField source="title" label="Tiêu đề" />
        <TextField source="topic.topicName" label="Chủ đề" />
        <TextField source="type" label="Kiểu bài viết" />
        <FunctionField
          label="Trạng thái"
          render={(record) => (record.status ? "Hiện" : "Ẩn")}
          sortBy="status"
        />
        <EditButton />
        <DeleteButton />
      </Datagrid>
    </List>
  );
};

export default PostList;

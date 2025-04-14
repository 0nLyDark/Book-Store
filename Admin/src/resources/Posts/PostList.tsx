import {
  BooleanField,
  Datagrid,
  DeleteButton,
  EditButton,
  FunctionField,
  List,
  TextField,
} from "react-admin";

const PostList = () => {
  return (
    <List>
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

import { useEffect } from "react";
import {
  BooleanField,
  Datagrid,
  DeleteButton,
  EditButton,
  FunctionField,
  List,
  TextField,
} from "react-admin";

const TopicList = () => {
  useEffect(() => {
    document.title = "Danh sách chủ đề";
  }, []);
  return (
    <List>
      <Datagrid>
        <TextField source="topicId" label="ID" />
        <TextField source="topicName" label="Tên chủ đề" />
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

export default TopicList;

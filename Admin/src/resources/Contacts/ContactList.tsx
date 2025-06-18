import { useEffect } from "react";
import {
  Datagrid,
  DateField,
  DeleteButton,
  EditButton,
  FunctionField,
  List,
  TextField,
} from "react-admin";

const ContactList = () => {
  useEffect(() => {
    document.title = "Danh sách liên hệ";
  }, []);
  return (
    <List sort={{ field: "isRead", order: "ASC" }}>
      <Datagrid>
        <TextField source="contactId" label="ID" />
        <TextField source="email" label="Email" />
        <TextField source="mobileNumber" label="Sổ điện thoai" />
        <FunctionField
          label="Phản hồi"
          render={(record) => (record.isRely ? "Đã trả lời" : "Chưa trả lời")}
        />
        <FunctionField
          label="Trạng thái"
          render={(record) => (record.isRead ? "Đã xem" : "Chưa xem")}
          sortBy="isRead"
        />
        <DateField source="createdAt" label="Ngày nhận" showTime />
        <EditButton />
        <DeleteButton />
      </Datagrid>
    </List>
  );
};

export default ContactList;

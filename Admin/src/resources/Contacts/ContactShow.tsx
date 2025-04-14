import {
  DateField,
  EmailField,
  FunctionField,
  Show,
  SimpleShowLayout,
  TextField,
} from "react-admin";

const ContactShow = () => (
  <Show>
    <SimpleShowLayout>
      <TextField source="contactId" label="Contact ID" />
      <EmailField source="email" label="Email" />
      <TextField source="mobileNumber" label="Số điện thoại" />
      <TextField source="title" label="Tiêu đề" />
      <TextField source="content" label="Nội dung" />
      <FunctionField
        label="Trạng thái"
        render={(record) => (record.isRead ? "Đã xem" : "Chưa xem")}
      />
      <DateField source="createdAt" label="Ngày tạo" showTime />
    </SimpleShowLayout>
  </Show>
);
export default ContactShow;

import {
  Edit,
  FunctionField,
  Labeled,
  RadioButtonGroupInput,
  SimpleForm,
  TextField,
} from "react-admin";
const ContactEdit = () => (
  <Edit mutationMode="pessimistic">
    <SimpleForm>
      <Labeled label="Contact ID" sx={labeledStyle}>
        <TextField source="contactId" />
      </Labeled>
      <Labeled label="Email" sx={labeledStyle}>
        <TextField source="email" />
      </Labeled>
      <Labeled label="Số điện thoại" sx={labeledStyle}>
        <TextField source="mobileNumber" />
      </Labeled>
      <Labeled label="Tiêu đề" sx={labeledStyle}>
        <TextField source="title" />
      </Labeled>
      <Labeled label="Nội dung" sx={labeledStyle}>
        <TextField source="content" />
      </Labeled>
      <Labeled label="Ngày nhận" sx={labeledStyle}>
        <TextField source="createdAt" />
      </Labeled>
      <Labeled label="Phản hồi" sx={labeledStyle}>
        <FunctionField
          label="Phản hồi"
          render={(record) => (record.isRely ? "Đã trả lời" : "Chưa trả lời")}
        />
      </Labeled>

      <RadioButtonGroupInput
        source="isRead"
        label="Trạng thái"
        choices={[
          { id: true, name: "Đã xem" },
          { id: false, name: "Chưa xem" },
        ]}
      />
    </SimpleForm>
  </Edit>
);
export default ContactEdit;

const labeledStyle = {
  mt: 2,
};

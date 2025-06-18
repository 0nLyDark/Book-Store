import { Box, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import {
  Button,
  DateField,
  EmailField,
  FunctionField,
  Labeled,
  Show,
  SimpleShowLayout,
  TextField,
  TextInput,
  useNotify,
  useRecordContext,
} from "react-admin";
import { LoadTinyMCE } from "../../components/CustomRichTextInput";
import { Editor } from "@tinymce/tinymce-react";
import { API_URL, httpClient } from "../../dataProvider";
import { useParams } from "react-router";

const CustomShowLayout = () => {
  const record = useRecordContext();
  if (!record) return null;

  return (
    <Box display="flex" gap={2} flexWrap="wrap" p={2}>
      <Box flex={1} minWidth={300}>
        <Typography>
          <strong>Contact ID: </strong> {record.contactId}
        </Typography>
        <Typography>
          <strong>Email: </strong> {record.email}
        </Typography>
        <Typography>
          <strong>Số điện thoại: </strong> {record.mobileNumber}
        </Typography>
        <Typography>
          <strong>Tiêu đề: </strong> {record.title}
        </Typography>
        <Typography>
          <strong>Nội dung: </strong> {record.content}
        </Typography>
      </Box>
      <Box flex={1} minWidth={300}>
        <Typography>
          <strong>Phản hồi: </strong>
          {record.isRely ? "Đã trả lời" : "Chưa trả lời"}
        </Typography>
        <Typography>
          <strong>Trạng thái:</strong> {record.isRead ? "Đã xem" : "Chưa xem"}
        </Typography>
        <Typography>
          <strong>Ngày tạo: </strong>
          {new Date(record.createdAt).toLocaleString()}
        </Typography>
      </Box>
    </Box>
  );
};

const ContactShow = () => {
  const [subject, setSubject] = useState("");
  const [msgBody, setMsgBody] = useState("");
  const [showReply, setShowReply] = useState(false);
  const notify = useNotify();
  const { id } = useParams();
  const sendEmail = () => {
    const data = { subject, msgBody };
    httpClient
      .post(`${API_URL}/staff/contacts/${id}/mail`, data)
      .then((res) => {
        console.log("res: ", res);
        notify("Gửi Email phản hồi thành công", { type: "success" });
      })
      .catch((error) => {
        console.log("Error: ", error);
      });
  };
  useEffect(() => {
    document.title = "Chi tiết liên hệ";
  }, []);
  return (
    <Show>
      <CustomShowLayout />
      <Box>
        <Button
          sx={{ m: 2 }}
          // variant="contained"
          onClick={() => setShowReply((prev) => !prev)}
        >
          {showReply ? "Ẩn phản hồi" : "Gửi phản hồi"}
        </Button>

        {showReply && (
          <Box p={2}>
            <Typography variant="subtitle1" fontSize={22} mb={2} gutterBottom>
              Gửi Email phản hồi
            </Typography>
            <Typography variant="subtitle2" color="textSecondary">
              Tiêu đề
            </Typography>
            <Box
              component="input"
              type="text"
              value={subject}
              onChange={(e) => setSubject(e.target.value)}
              mb={2}
              sx={{
                width: "100%",
                padding: "8px 12px",
                border: "1px solid #ccc",
                borderRadius: "8px",
                fontSize: "16px",
                "&:focus": {
                  outline: "none",
                  borderColor: "blue",
                  boxShadow: "0 0 0 2px rgba(0, 0, 255, 0.2)",
                },
              }}
            />
            <Typography variant="subtitle2" color="textSecondary">
              Nội dung
            </Typography>
            <LoadTinyMCE />
            <Editor
              tinymceScriptSrc="/tinymce/tinymce.min.js"
              value={msgBody}
              onEditorChange={(content) => setMsgBody(content)}
              init={{
                height: 300,
                width: "100%",
                menubar: false,
                plugins: "link image code lists",
                toolbar:
                  "undo redo | formatselect | bold italic | alignleft aligncenter alignright | bullist numlist | link image | code",
                content_css: "/tinymce/skins/content/default/content.min.css",
                skin_url: "/tinymce/skins/ui/oxide",
                base_url: "/tinymce",
                suffix: ".min",
                branding: false,
              }}
            />
            <Button
              sx={{ my: 2, fontSize: 16 }}
              variant="outlined"
              onClick={() => sendEmail()}
            >
              Gửi Email
            </Button>
          </Box>
        )}
      </Box>
    </Show>
  );
};
export default ContactShow;

import { Dialog, DialogTitle, DialogContent } from "@mui/material";
import {
  Create,
  ImageField,
  ImageInput,
  SimpleForm,
  TextInput,
} from "react-admin";

interface DialogProps {
  open: boolean;
  onClose: () => void;
}

const AuthorCreateDialog: React.FC<DialogProps> = ({ open, onClose }) => {
  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Tạo mới tác giả</DialogTitle>
      <DialogContent>
        <Create
          resource="authors"
          redirect={false}
          mutationOptions={{
            onSuccess: () => {
              onClose(); // đóng dialog khi tạo xong
            },
          }}
        >
          <SimpleForm>
            <TextInput source="authorName" label="Tên tác giả" fullWidth />
            <ImageInput
              source="image"
              label="Hình ảnh"
              accept={{ "image/*": [".png", ".jpg", ".jpeg", ".gif", ".webp"] }}
            >
              <ImageField source="src" title="title" />
            </ImageInput>
            <TextInput source="description" label="Mô tả" multiline fullWidth />
          </SimpleForm>
        </Create>
      </DialogContent>
    </Dialog>
  );
};
export default AuthorCreateDialog;

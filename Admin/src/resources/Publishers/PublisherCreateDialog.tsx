import { Dialog, DialogTitle, DialogContent } from "@mui/material";
import { Create, ImageField, ImageInput, SimpleForm, TextInput } from "react-admin";

interface DialogProps {
  open: boolean;
  onClose: () => void;
}

const PublisherCreateDialog: React.FC<DialogProps> = ({ open, onClose }) => {
  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Tạo mới nhà sản xuất</DialogTitle>
      <DialogContent>
        <Create
          resource="publishers"
          redirect={false}
          mutationOptions={{
            onSuccess: () => {
              onClose(); // đóng dialog khi tạo xong
            },
          }}
        >
          <SimpleForm>
            <TextInput source="publisherName" label="Tên nhà sản xuất" />
            <ImageInput
              source="image"
              label="Hình ảnh"
              accept={{ "image/*": [".png", ".jpg", ".jpeg", ".gif", ".webp"] }}
            >
              <ImageField source="src" title="title" />
            </ImageInput>
          </SimpleForm>
        </Create>
      </DialogContent>
    </Dialog>
  );
};
export default PublisherCreateDialog;

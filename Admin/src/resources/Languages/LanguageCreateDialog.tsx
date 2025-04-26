import { Dialog, DialogTitle, DialogContent } from "@mui/material";
import { Create, SimpleForm, TextInput } from "react-admin";

interface DialogProps {
  open: boolean;
  onClose: () => void;
}

const LanguageCreateDialog: React.FC<DialogProps> = ({ open, onClose }) => {
  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Tạo mới ngôn ngữ</DialogTitle>
      <DialogContent>
        <Create
          resource="languages"
          redirect={false}
          mutationOptions={{
            onSuccess: () => {
              onClose(); // đóng dialog khi tạo xong
            },
          }}
        >
          <SimpleForm>
            <TextInput source="name" label="Tên ngôn ngữ" fullWidth />
          </SimpleForm>
        </Create>
      </DialogContent>
    </Dialog>
  );
};
export default LanguageCreateDialog;

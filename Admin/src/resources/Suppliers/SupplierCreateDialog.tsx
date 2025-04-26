import { Dialog, DialogTitle, DialogContent } from "@mui/material";
import { Create, regex, SimpleForm, TextInput } from "react-admin";

interface DialogProps {
  open: boolean;
  onClose: () => void;
}

const SupplierCreateDialog: React.FC<DialogProps> = ({ open, onClose }) => {
  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Tạo mới nhà cung cấp</DialogTitle>
      <DialogContent>
        <Create
          resource="suppliers"
          redirect={false}
          mutationOptions={{
            onSuccess: () => {
              onClose(); // đóng dialog khi tạo xong
            },
          }}
        >
          <SimpleForm>
            <TextInput source="supplierName" label="Tên nhà cung cấp" />
            <TextInput source="email" label="Email" />
            <TextInput
              source="mobieNumber"
              label="Số điện thoại"
              validate={[
                regex(/^\d{10}$/, "Số điện thoại phải có đúng 10 chữ số"),
              ]}
            />
            <TextInput source="address" label="Địa chỉ" />
          </SimpleForm>
        </Create>
      </DialogContent>
    </Dialog>
  );
};
export default SupplierCreateDialog;

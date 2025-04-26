import { Dialog, DialogTitle, DialogContent } from "@mui/material";
import {
  Create,
  ImageInput,
  ReferenceInput,
  SelectInput,
  SimpleForm,
  TextInput,
} from "react-admin";

interface DialogProps {
  open: boolean;
  onClose: () => void;
}

const CategoryCreateDialog: React.FC<DialogProps> = ({ open, onClose }) => {
  return (
    <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
      <DialogTitle>Tạo mới tác giả</DialogTitle>
      <DialogContent>
        <Create
          resource="categories"
          redirect={false}
          mutationOptions={{
            onSuccess: () => {
              onClose(); // đóng dialog khi tạo xong
            },
          }}
        >
          <SimpleForm>
            <TextInput source="categoryName" label="Tên danh mục" />
            <ReferenceInput source="parent.categoryId" reference="categories">
              <SelectInput
                optionText="categoryName"
                variant="outlined"
                label="Danh mục cha"
              />
            </ReferenceInput>
            <ImageInput
              source="image"
              label="Hình ảnh"
              accept={{ "image/*": [".png", ".jpg", ".jpeg", ".gif", ".webp"] }}
            />
          </SimpleForm>
        </Create>
      </DialogContent>
    </Dialog>
  );
};
export default CategoryCreateDialog;

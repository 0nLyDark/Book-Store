import { useEffect } from "react";
import {
  BooleanInput,
  Edit,
  ImageField,
  ImageInput,
  SelectInput,
  SimpleForm,
  TextInput,
} from "react-admin";
const BannerEdit = () => {
  useEffect(() => {
    document.title = "Chỉnh sửa banner";
  }, []);
  return (
    <Edit mutationMode="pessimistic">
      <SimpleForm>
        <TextInput source="bannerId" label="Banner ID" readOnly />
        <TextInput source="bannerName" label="Tên Banner" />
        <ImageInput
          source="image"
          label="Hình ảnh"
          accept={{ "image/*": [".png", ".jpg", ".jpeg", ".gif", ".webp"] }}
          placeholder={"Nhấn để tải ảnh lên"}
          format={(value) =>
            typeof value === "string"
              ? { src: value, title: "Ảnh hiện tại" }
              : value
          }
        >
          <ImageField source="src" title="title" />
        </ImageInput>
        <TextInput source="link" label="Link liên kết" />
        <SelectInput
          source="position"
          label="Vị trí"
          choices={[{ id: "SLIDESHOW", name: "Slide Show" }]}
        />
        <BooleanInput source="status" label="Trạng thái" />
      </SimpleForm>
    </Edit>
  );
};
export default BannerEdit;

import {
  Create,
  FileInput,
  ImageInput,
  ReferenceInput,
  SelectInput,
  SimpleForm,
  TextInput,
} from "react-admin";

const CategoryCreate = () => (
  <Create>
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
);

export default CategoryCreate;

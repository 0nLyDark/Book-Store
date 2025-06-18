import { useEffect } from "react";
import {
  AutocompleteInput,
  Create,
  FileInput,
  ImageField,
  ImageInput,
  ReferenceInput,
  SelectInput,
  SimpleForm,
  TextInput,
} from "react-admin";

const CategoryCreate = () => {
  useEffect(() => {
    document.title = "Tạo mới danh mục";
  }, []);
  return (
    <Create mutationMode="pessimistic">
      <SimpleForm>
        <TextInput source="categoryName" label="Tên danh mục" />
        <ReferenceInput source="parent.categoryId" reference="categories">
          <AutocompleteInput
            optionText="categoryName"
            variant="outlined"
            label="Danh mục cha"
            filterToQuery={(searchText: string) => ({ keyword: searchText })}
          />
        </ReferenceInput>
        <ImageInput
          source="image"
          label="Hình ảnh"
          accept={{ "image/*": [".png", ".jpg", ".jpeg", ".gif", ".webp"] }}
        >
          <ImageField source="src" title="title" />
        </ImageInput>
      </SimpleForm>
    </Create>
  );
};

export default CategoryCreate;

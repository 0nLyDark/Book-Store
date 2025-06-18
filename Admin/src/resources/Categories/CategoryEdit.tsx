import { useEffect } from "react";
import {
  AutocompleteInput,
  BooleanInput,
  Edit,
  FileInput,
  ImageField,
  ImageInput,
  ReferenceInput,
  SelectInput,
  SimpleForm,
  TextInput,
  useGetList,
} from "react-admin";

const CategoryEdit = () => {
  useEffect(() => {
    document.title = "Chỉnh sửa danh mục";
  }, []);
  return (
    <Edit mutationMode="pessimistic">
      <SimpleForm>
        <TextInput source="categoryId" label="Category ID" disabled />
        <TextInput source="categoryName" label="Tên danh mục" />
        <TextInput source="slug" label="Slug" disabled />
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
        <BooleanInput source="status" label="Trạng thái" />
      </SimpleForm>
    </Edit>
  );
};
export default CategoryEdit;

import {
  BooleanInput,
  Edit,
  FileInput,
  ImageInput,
  SelectInput,
  SimpleForm,
  TextInput,
  useGetList,
} from "react-admin";
import { useWatch } from "react-hook-form";

const ParentCategoryInput = () => {
  const currentId = useWatch({ name: "categoryId" }); // ID của category đang edit

  const { data: categories, isLoading } = useGetList("categories");

  const filteredChoices = categories
    ? categories.filter((category) => category.categoryId !== currentId)
    : [];

  return (
    <SelectInput
      source="parent.categoryId"
      label="Danh mục cha"
      choices={filteredChoices}
      optionText="categoryName"
      optionValue="categoryId"
      isLoading={isLoading}
    />
  );
};
const CategoryEdit = () => (
  <Edit>
    <SimpleForm>
      <TextInput source="categoryId" label="Category ID" disabled />
      <TextInput source="categoryName" label="Tên danh mục" />
      <TextInput source="slug" label="Slug" disabled />
      <ImageInput
        source="image"
        label="Hình ảnh"
        accept={{ "image/*": [".png", ".jpg", ".jpeg", ".gif", ".webp"] }}
      />
      <ParentCategoryInput />
      <BooleanInput source="status" label="Trạng thái" />
    </SimpleForm>
  </Edit>
);
export default CategoryEdit;

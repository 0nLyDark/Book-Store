import {
  Create,
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
    </SimpleForm>
  </Create>
);

export default CategoryCreate;

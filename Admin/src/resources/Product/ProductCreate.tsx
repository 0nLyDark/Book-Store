import {
  Create,
  ImageField,
  ImageInput,
  NumberInput,
  ReferenceInput,
  SelectArrayInput,
  SelectInput,
  SimpleForm,
  TextInput,
} from "react-admin";

const ProductCreate = () => (
  <Create>
    <SimpleForm>
      <TextInput source="productName" label="Tên sách" required />
      <TextInput source="isbn" label="Mã sách" required />
      <TextInput source="size" label="Kích thước" />
      <NumberInput
        source="year"
        label="Năm xuất bản"
        min={0}
        defaultValue={0}
      />
      <NumberInput
        source="pageNumber"
        label="Số trang"
        min={1}
        defaultValue={1}
      />
      <NumberInput source="price" label="Giá" min={0} required />
      <NumberInput source="quantity" label="Số lượng" min={0} required />
      <NumberInput
        source="discount"
        label=" % Giảm giá"
        min={0}
        max={100}
        defaultValue={0}
        required
      />
      <ReferenceInput source="authorIds" reference="authors" multiple>
        <SelectArrayInput
          optionText="authorName"
          label="Tác giả"
          variant="outlined"
        />
      </ReferenceInput>
      <ReferenceInput source="categoryIds" reference="categories" multiple>
        <SelectArrayInput
          optionText="categoryName"
          label="Danh mục"
          variant="outlined"
        />
      </ReferenceInput>
      <ReferenceInput source="languageIds" reference="languages" multiple>
        <SelectArrayInput
          optionText="name"
          label="Ngôn ngữ"
          variant="outlined"
        />
      </ReferenceInput>
      <ReferenceInput source="supplier.supplierId" reference="suppliers">
        <SelectInput
          optionText="supplierName"
          label="Nhà cung cấp"
          variant="outlined"
        />
      </ReferenceInput>
      <ReferenceInput source="publisher.publisherId" reference="publishers">
        <SelectInput
          optionText="publisherName"
          label="Nhà sản xuất"
          variant="outlined"
        />
      </ReferenceInput>
      <ImageInput
        source="images"
        accept={{ "image/*": [".png", ".jpg", ".jpeg", ".gif", ".webp"] }}
        multiple
      >
        <ImageField source="src" title="title" />
      </ImageInput>
      <TextInput source="description" label="Mô tả" multiline />
    </SimpleForm>
  </Create>
);

export default ProductCreate;

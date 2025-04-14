import {
  BooleanField,
  Datagrid,
  DeleteButton,
  EditButton,
  FunctionField,
  List,
  NumberField,
  ReferenceInput,
  SelectInput,
  TextField,
  TextInput,
} from "react-admin";
const ProductFilter = [
  <TextInput label="Tìm theo tên" source="keyword" alwaysOn />,
  <TextInput label="Tìm theo mã sách" source="isbn" alwaysOn />,
  <ReferenceInput source="categoryId" reference="categories" label="Danh mục" alwaysOn>
    <SelectInput optionText="categoryName" label="Danh mục" />
  </ReferenceInput>,
  <ReferenceInput source="authorIds" reference="authors" label="Tác giả">
    <SelectInput optionText="authorName" label="Tác giả" />
  </ReferenceInput>,
  <ReferenceInput source="languageIds" reference="languages" label="Ngôn ngữ">
    <SelectInput optionText="name" label="Ngôn ngữ" />
  </ReferenceInput>,
  <ReferenceInput source="supplierId" reference="suppliers" label="Nhà cung cấp">
    <SelectInput optionText="supplierName" label="Nhà cung cấp" />
  </ReferenceInput>,
  <ReferenceInput source="publisherId" reference="publishers" label="Nhà sản xuất">
    <SelectInput optionText="publisherName" label="Nhà sản xuất" />
  </ReferenceInput>,
];
const ProductList = () => {
  return (
    <List filters={ProductFilter}>
      <Datagrid>
        <TextField source="productId" label="ID" />
        <TextField source="productName" label="Tên sách" />
        <TextField source="isbn" label="Mã sách" />
        <NumberField
          source="price"
          label="Giá"
          options={{ style: "currency", currency: "VND" }}
          locales="vi-VN"
        />
        <FunctionField
          source="categories"
          label="Danh mục"
          render={(record) => {
            return (
              <span>
                {record.categories.map((category: any, index: number) => (
                  <span key={index}>
                    - {category.categoryName}
                    <br />
                  </span>
                ))}
              </span>
            );
          }}
        />
        <FunctionField
          source="authors"
          label="Tác giả"
          render={(record) => {
            return (
              <span>
                {record.authors.map((author: any, index: number) => (
                  <span key={index}>
                    {author.authorName}
                    <br />
                  </span>
                ))}
              </span>
            );
          }}
        />
        <NumberField
          source="quantity"
          label="Số lượng"
          options={{ style: "decimal" }}
        />
         <FunctionField
          label="Trạng thái"
          render={(record) => (record.status ? "Hiện" : "Ẩn")}
          sortBy="status"
        />
        <EditButton />
        <DeleteButton />
      </Datagrid>
    </List>
  );
};

export default ProductList;

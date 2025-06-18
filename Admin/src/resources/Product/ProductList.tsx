import {
  AutocompleteInput,
  Button,
  CreateButton,
  Datagrid,
  DeleteButton,
  EditButton,
  ExportButton,
  FilterButton,
  FunctionField,
  ImageField,
  List,
  NumberField,
  ReferenceInput,
  SelectInput,
  TextField,
  TextInput,
  TopToolbar,
} from "react-admin";
import { useNavigate } from "react-router";
import UploadFileIcon from "@mui/icons-material/UploadFile";
import { useEffect } from "react";

const ProductFilter = [
  <TextInput label="Tìm theo tên" source="keyword" alwaysOn />,
  <TextInput label="Tìm theo mã sách" source="isbn" alwaysOn />,
  <ReferenceInput
    source="categoryId"
    reference="categories"
    label="Danh mục"
    alwaysOn
  >
    <AutocompleteInput
      optionText={(record) =>
        `${record.categoryName} (${record.totalProducts ?? 0})`
      }
      label="Danh mục"
      filterToQuery={(searchText) => ({ keyword: searchText })}
    />
  </ReferenceInput>,
  <ReferenceInput source="authorIds" reference="authors" label="Tác giả">
    <SelectInput optionText="authorName" label="Tác giả" />
  </ReferenceInput>,
  <ReferenceInput source="languageIds" reference="languages" label="Ngôn ngữ">
    <SelectInput optionText="name" label="Ngôn ngữ" />
  </ReferenceInput>,
  <ReferenceInput
    source="supplierId"
    reference="suppliers"
    label="Nhà cung cấp"
  >
    <SelectInput optionText="supplierName" label="Nhà cung cấp" />
  </ReferenceInput>,
  <ReferenceInput
    source="publisherId"
    reference="publishers"
    label="Nhà sản xuất"
  >
    <SelectInput optionText="publisherName" label="Nhà sản xuất" />
  </ReferenceInput>,
];
const MyListActions = () => {
  const navigate = useNavigate();

  return (
    <TopToolbar>
      <FilterButton />
      <CreateButton />
      <Button
        label="Nhập"
        onClick={() => navigate("/products/import")}
        startIcon={<UploadFileIcon />}
      />
      <ExportButton />
    </TopToolbar>
  );
};
const ProductList = () => {
  useEffect(() => {
    document.title = "Danh sách sản phẩm";
  }, []);
  return (
    <List
      filters={ProductFilter}
      actions={<MyListActions />}
      sx={{
        "& .column-image": {
          textAlign: "center",
        },
        "& .RaDatagrid-headerCell": {
          textAlign: "center",
        },
      }}
    >
      <Datagrid>
        <TextField source="productId" label="ID" />
        <TextField source="productName" label="Tên sách" />
        <TextField source="isbn" label="Mã sách" />
        <ImageField source="images[0]" label="Hình ảnh" />
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
                    - {author.authorName}
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

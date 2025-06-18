// src/components/ImportProduct/ExcelImportPage.tsx
import React, { useState, useEffect } from "react";
import {
  SimpleForm,
  TextInput,
  NumberInput,
  BooleanInput,
  SelectArrayInput,
  SelectInput,
  useDataProvider,
} from "react-admin";
import { Box, Button, Typography, Grid } from "@mui/material";
import parseExcelFile from "./utils/excelParser";

const ExcelImportPage = () => {
  const [products, setProducts] = useState<any[]>([]);
  const [selectedIndex, setSelectedIndex] = useState<number | null>(null);
  const [currentProduct, setCurrentProduct] = useState<any>({});
  const [categories, setCategories] = useState<any[]>([]);
  const [authors, setAuthors] = useState<any[]>([]);
  const [languages, setLanguages] = useState<any[]>([]);
  const [suppliers, setSuppliers] = useState<any[]>([]);
  const [publishers, setPublishers] = useState<any[]>([]);
  const dataProvider = useDataProvider();

  useEffect(() => {
    const fetchChoices = async () => {
      const [cat, auth, lang, supp, pub] = await Promise.all([
        dataProvider.getList("categories", {
          pagination: { page: 1, perPage: 100 },
          sort: { field: "id", order: "ASC" },
          filter: {},
        }),
        dataProvider.getList("authors", {
          pagination: { page: 1, perPage: 100 },
          sort: { field: "id", order: "ASC" },
          filter: {},
        }),
        dataProvider.getList("languages", {
          pagination: { page: 1, perPage: 100 },
          sort: { field: "id", order: "ASC" },
          filter: {},
        }),
        dataProvider.getList("suppliers", {
          pagination: { page: 1, perPage: 100 },
          sort: { field: "id", order: "ASC" },
          filter: {},
        }),
        dataProvider.getList("publishers", {
          pagination: { page: 1, perPage: 100 },
          sort: { field: "id", order: "ASC" },
          filter: {},
        }),
      ]);

      setCategories(cat.data);
      setAuthors(auth.data);
      setLanguages(lang.data);
      setSuppliers(supp.data);
      setPublishers(pub.data);
    };

    fetchChoices();
  }, [dataProvider]);

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;
    const parsed = await parseExcelFile(file);
    setProducts(parsed);
  };

  const handleSelectProduct = (index: number) => {
    setSelectedIndex(index);
    setCurrentProduct(products[index]);
  };

  const handleFormChange = (updated: any) => {
    if (selectedIndex === null) return;
    const updatedProducts = [...products];
    updatedProducts[selectedIndex] = updated;
    setProducts(updatedProducts);
    setCurrentProduct(updated);
  };

  const handleSave = async () => {
    if (selectedIndex === null) return;
    const updatedProduct = products[selectedIndex];
    // Gửi dữ liệu đến API
    console.log("datat", updatedProduct);
    // await dataProvider.update("products", {
    //   id: updatedProduct.id,
    //   data: updatedProduct,
    // });
    alert("Sản phẩm đã được lưu!");
  };

  return (
    <Box p={3}>
      <Typography variant="h5" gutterBottom>
        Nhập sản phẩm từ Excel
      </Typography>

      <input type="file" accept=".xlsx, .xls" onChange={handleFileUpload} />

      {products.length > 0 && (
        <Box mt={4}>
          <Grid container spacing={2}>
            <Grid item xs={4}>
              <Typography variant="h6">Danh sách sản phẩm</Typography>
              {products.map((product, index) => (
                <Box
                  key={index}
                  p={1}
                  my={1}
                  border={1}
                  borderRadius={2}
                  borderColor={
                    index === selectedIndex ? "primary.main" : "grey.300"
                  }
                  onClick={() => handleSelectProduct(index)}
                  sx={{ cursor: "pointer" }}
                >
                  <Typography>
                    {product.productName || `Sản phẩm ${index + 1}`}
                  </Typography>
                </Box>
              ))}
            </Grid>

            <Grid item xs={8}>
              {selectedIndex !== null && (
                <>
                  <Typography variant="h6" gutterBottom>
                    Chỉnh sửa sản phẩm
                  </Typography>
                  <SimpleForm
                    record={currentProduct}
                    toolbar={false}
                    onChange={handleFormChange}
                  >
                    <TextInput
                      source="productName"
                      label="Tên sản phẩm"
                      fullWidth
                    />
                    <TextInput source="isbn" label="ISBN" fullWidth />
                    <TextInput source="size" label="Kích thước" />
                    <TextInput source="format" label="Định dạng" />
                    <NumberInput source="weight" label="Trọng lượng" />
                    <NumberInput source="year" label="Năm xuất bản" />
                    <NumberInput source="quantity" label="Số lượng" />
                    <NumberInput source="price" label="Giá" />
                    <NumberInput source="discount" label="Giảm giá (%)" />
                    <NumberInput source="pageNumber" label="Số trang" />
                    <TextInput
                      source="description"
                      label="Mô tả"
                      multiline
                      fullWidth
                    />
                    <BooleanInput source="status" label="Còn hàng" />
                    <SelectArrayInput
                      label="Danh mục"
                      source="categoryIds"
                      choices={categories}
                      optionText="name"
                      optionValue="id"
                    />
                    <SelectArrayInput
                      label="Tác giả"
                      source="authorIds"
                      choices={authors}
                      optionText="name"
                      optionValue="id"
                    />
                    <SelectArrayInput
                      label="Ngôn ngữ"
                      source="languageIds"
                      choices={languages}
                      optionText="name"
                      optionValue="id"
                    />
                    <SelectInput
                      label="Nhà cung cấp"
                      source="supplierId"
                      choices={suppliers}
                      optionText="name"
                      optionValue="id"
                    />
                    <SelectInput
                      label="Nhà xuất bản"
                      source="publisherId"
                      choices={publishers}
                      optionText="name"
                      optionValue="id"
                    />
                  </SimpleForm>
                  <Button
                    variant="contained"
                    color="primary"
                    onClick={handleSave}
                    sx={{ mt: 2 }}
                  >
                    Lưu sản phẩm
                  </Button>
                </>
              )}
            </Grid>
          </Grid>
        </Box>
      )}
    </Box>
  );
};

export default ExcelImportPage;

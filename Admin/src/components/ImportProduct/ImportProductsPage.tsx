import { useEffect, useState } from "react";
import { parseExcelFile } from "./utils/excelParser";
import { Button, Typography, Box } from "@mui/material";
import { useForm, FormProvider } from "react-hook-form";
import {
  ArrayInput,
  SimpleFormIterator,
  SimpleForm,
  ResourceContextProvider,
} from "react-admin";
import ProductInputItem from "./ProductInputItem";
interface Product {
  productName: string;
  isbn: string;
  size: string;
  format: string;
  weight: number;
  year: number;
  quantity: number;
  price: number;
  discount: number;
  pageNumber: number;
  description: string;
  status: boolean;
  categoryIds: number[];
  authorIds: number[];
  languageIds: number[];
  supplierId: number;
  publisherId: number;
  files: File[];
}
const ImportProductsPage = () => {
  const [products, setProducts] = useState<Product[]>([]);
  const [errors, setErrors] = useState<number[]>([]);

  const methods = useForm<{ products: Product[] }>({
    defaultValues: { products: [] },
  });
  useEffect(() => {
    console.log("list product:  ", products);
  }, [products]);
  const handleFileUpload = async (
    event: React.ChangeEvent<HTMLInputElement>,
  ) => {
    const file = event.target.files?.[0];
    if (file) {
      const data = await parseExcelFile(file);
      const invalidIndexes = data
        .map((p, i) => (validateProduct(p) ? -1 : i))
        .filter((i) => i !== -1);

      setErrors(invalidIndexes);
      setProducts(data);
      methods.reset({ products: data });
    }
  };

  const validateProduct = (product: any) => {
    return (
      product.productName &&
      product.price > 0 &&
      Array.isArray(product.categoryIds) &&
      product.categoryIds.length > 0
    );
  };

  const handleSubmit = async () => {
    const values = methods.getValues("products");
    const validProducts = values.filter(validateProduct);
    console.log("values  ", values);
    console.log("validProducts  ", validProducts);
    console.log("products  ", products);

    // await Promise.all(
    //   validProducts.map((product) => {
    //     const formData = new FormData();

    //     for (const key in product) {
    //       const typedKey = key as keyof Product;

    //       if (typedKey === "files" && Array.isArray(product.files)) {
    //         product.files.forEach((file: File) => {
    //           formData.append("files", (file as any).rawFile || file);
    //         });
    //       } else if (Array.isArray(product[typedKey])) {
    //         product[typedKey].forEach((v: any) => formData.append(typedKey, v));
    //       } else {
    //         formData.append(typedKey, product[typedKey] as any);
    //       }
    //     }

    //     return fetch("/api/products", {
    //       method: "POST",
    //       body: formData,
    //     });
    //   }),
    // );

    alert("Import thành công");
  };

  return (
    <ResourceContextProvider value="products">
      <Box sx={{ p: 3 }}>
        <Typography variant="h5" gutterBottom>
          Nhập Sản Phẩm từ file Excel
        </Typography>
        <input type="file" accept=".xlsx,.xls" onChange={handleFileUpload} />

        {products.length > 0 && (
          <FormProvider {...methods}>
            <SimpleForm record={{ products }} toolbar={false}>
              <Typography variant="h6" sx={{ mt: 3 }}>
                Danh sách sản phẩm
                {/* ({errors.length} lỗi) */}
              </Typography>
              <ArrayInput source="products" label={false}>
                <SimpleFormIterator>
                  <ProductInputItem />
                </SimpleFormIterator>
              </ArrayInput>

              <Button
                variant="contained"
                color="primary"
                onClick={handleSubmit}
                disabled={errors.length > 0}
                sx={{ mt: 2 }}
              >
                Gửi lên server
              </Button>
            </SimpleForm>
          </FormProvider>
        )}
      </Box>
    </ResourceContextProvider>
  );
};

export default ImportProductsPage;

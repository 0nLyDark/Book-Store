import React from "react";
import {
  BooleanInput,
  ImageField,
  ImageInput,
  ReferenceInput,
  SelectInput,
  TextInput,
  NumberInput,
  useRecordContext,
  composeValidators,
  required,
} from "react-admin";
import { Box } from "@mui/material";
import CustomReferenceArrayInput from "./CustomReferenceArrayInput";

type ProductInputItemProps = {
  source?: string;
};

const ProductInputItem: React.FC<ProductInputItemProps> = ({ source }) => {
  const record = useRecordContext();

  return (
    <Box
      sx={{
        p: 3,
        mb: 10,
        border: "1px solid #ccc",
        boxShadow: "0 2px 4px rgba(0,0,0,0.1)",
        borderRadius: 5,
        backgroundColor: "#f5f5f5",
      }}
    >
      <Box
        sx={{
          display: "flex",
          flexWrap: "wrap",
          gap: 2,
          alignItems: "center",
        }}
      >
        <TextInput
          source="productName"
          label="Tên sản phẩm"
          sx={{ flex: "1 1 250px" }}
          required={true}
        />
        <TextInput source="isbn" label="Mã ISBN" sx={{ flex: "1 1 150px" }} />
        <TextInput
          source="size"
          label="Kích thước"
          sx={{ flex: "1 1 150px" }}
        />
        <TextInput
          source="format"
          label="Định dạng"
          sx={{ flex: "1 1 150px" }}
        />
        <NumberInput
          source="weight"
          label="Trọng lượng (gram)"
          min={0}
          sx={{ flex: "1 1 150px" }}
          required={true}
        />
        <NumberInput
          source="year"
          label="Năm xuất bản"
          min={2000}
          sx={{ flex: "1 1 150px" }}
          required={true}
        />
        <NumberInput
          source="quantity"
          label="Số lượng"
          min={0}
          sx={{ flex: "1 1 150px" }}
          required={true}
        />
        <NumberInput
          source="price"
          label="Giá (VNĐ)"
          min={0}
          step={0.01}
          sx={{ flex: "1 1 150px" }}
          required={true}
        />
        <NumberInput
          source="discount"
          label="Giảm giá (%)"
          min={0}
          max={100}
          sx={{ flex: "1 1 150px" }}
        />
        <NumberInput
          source="pageNumber"
          label="Số trang"
          min={1}
          sx={{ flex: "1 1 150px" }}
          required={true}
        />
      </Box>

      <Box sx={{ mt: 2 }}>
        <TextInput
          source="description"
          label="Mô tả"
          multiline
          fullWidth
          validate={composeValidators(
            required("Bắt buộc nhập trường mô tả"),
            (value: string) =>
              value && value.trim().length >= 6
                ? undefined
                : "Phải nhập ít nhất 6 ký tự",
          )}
        />
        <BooleanInput source="status" label="Hiển thị" />
      </Box>

      <Box
        sx={{
          display: "flex",
          flexWrap: "wrap",
          gap: 2,
          mt: 2,
          "& > *": { flex: "1 1 30%", minWidth: 250 },
          "@media (max-width:900px)": {
            "& > *": { flex: "1 1 45%", minWidth: 200 },
          },
          "@media (max-width:600px)": {
            "& > *": { flex: "1 1 100%", minWidth: "auto" },
          },
        }}
      >
        <ReferenceInput
          source="supplierId"
          reference="suppliers"
          label="Nhà cung cấp"
        >
          <SelectInput optionText="supplierName" />
        </ReferenceInput>
        <ReferenceInput
          source="publisherId"
          reference="publishers"
          label="Nhà xuất bản"
        >
          <SelectInput optionText="publisherName" />
        </ReferenceInput>
        <CustomReferenceArrayInput
          source="categoryIds"
          reference="categories"
          label="Danh mục"
          optionText="categoryName"
        />
        <CustomReferenceArrayInput
          source="authorIds"
          reference="authors"
          label="Tác giả"
          optionText="authorName"
        />
        <CustomReferenceArrayInput
          source="languageIds"
          reference="languages"
          label="Ngôn ngữ"
          optionText="name"
        />
      </Box>

      <Box sx={{ mt: 2 }}>
        <ImageInput
          source="files"
          label="Hình ảnh"
          multiple
          accept={{
            "image/*": [".png", ".jpg", ".jpeg", ".gif", ".webp"],
          }}
        >
          <ImageField source="src" title="title" />
        </ImageInput>
      </Box>
    </Box>
  );
};

export default ProductInputItem;

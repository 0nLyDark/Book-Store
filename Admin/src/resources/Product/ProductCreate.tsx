import { Box } from "@mui/material";
import {
  Button,
  Create,
  ImageField,
  ImageInput,
  NumberInput,
  ReferenceInput,
  SelectArrayInput,
  SelectInput,
  SimpleForm,
  TextInput,
  useRefresh,
} from "react-admin";
import AuthorCreateDialog from "../Authors/AuthorCreateDialog";
import { useState } from "react";
import CategoryCreateDialog from "../Categories/CategoryCreateDialog";
import LanguageCreateDialog from "../Languages/LanguageCreateDialog";
import SupplierCreateDialog from "../Suppliers/SupplierCreateDialog";
import PublisherCreateDialog from "../Publishers/PublisherCreateDialog";

const ProductCreate = () => {
  const [openAuthorDialog, setOpenAuthorDialog] = useState(false);
  const [openCategoryDialog, setOpenCategoryDialog] = useState(false);
  const [openLanguageDialog, setOpenLanguageDialog] = useState(false);
  const [openPublisherDialog, setOpenPublisherDialog] = useState(false);
  const [openSupplierDialog, setOpenSupplierDialog] = useState(false);

  const refresh = useRefresh();

  return (
    <Create>
      <SimpleForm>
        <Box display="flex" gap={2} flexWrap="wrap">
          {/* Cột 1 */}
          <Box flex={1} minWidth={300}>
            <TextInput
              source="productName"
              label="Tên sách"
              required
              fullWidth
            />
            <TextInput source="isbn" label="Mã sách" required fullWidth />
            <TextInput source="size" label="Kích thước" fullWidth />
            <NumberInput
              source="year"
              label="Năm xuất bản"
              min={2000}
              defaultValue={2000}
              fullWidth
            />
            <NumberInput
              source="pageNumber"
              label="Số trang"
              min={1}
              defaultValue={1}
              fullWidth
            />
            <NumberInput
              source="price"
              label="Giá"
              min={0}
              required
              fullWidth
            />
            <NumberInput
              source="quantity"
              label="Số lượng"
              min={0}
              required
              fullWidth
            />
            <NumberInput
              source="discount"
              label="% Giảm giá"
              min={0}
              max={100}
              defaultValue={0}
              required
              fullWidth
            />
          </Box>

          {/* Cột 2 */}
          <Box flex={1} minWidth={300}>
            <Box display="flex" gap={2} flexWrap="wrap">
              <Box flex={1}>
                <ReferenceInput source="authorIds" reference="authors" multiple>
                  <SelectArrayInput
                    optionText="authorName"
                    label="Tác giả"
                    variant="outlined"
                    fullWidth
                  />
                </ReferenceInput>
              </Box>
              <Box display={"flex"} mb={2}>
                <Button onClick={() => setOpenAuthorDialog(true)}>
                  + Thêm tác giả
                </Button>
              </Box>
            </Box>
            <Box display="flex" gap={2} flexWrap="wrap">
              <Box flex={1}>
                <ReferenceInput
                  source="categoryIds"
                  reference="categories"
                  multiple
                >
                  <SelectArrayInput
                    optionText="categoryName"
                    label="Danh mục"
                    variant="outlined"
                    fullWidth
                  />
                </ReferenceInput>
              </Box>
              <Box display={"flex"} mb={2}>
                <Button onClick={() => setOpenCategoryDialog(true)}>
                  + Thêm danh mục
                </Button>
              </Box>
            </Box>
            <Box display="flex" gap={2} flexWrap="wrap">
              <Box flex={1}>
                <ReferenceInput
                  source="languageIds"
                  reference="languages"
                  multiple
                >
                  <SelectArrayInput
                    optionText="name"
                    label="Ngôn ngữ"
                    variant="outlined"
                    fullWidth
                  />
                </ReferenceInput>
              </Box>
              <Box display={"flex"} mb={2}>
                <Button onClick={() => setOpenLanguageDialog(true)}>
                  + Thêm ngôn ngữ
                </Button>
              </Box>
            </Box>
            <Box display="flex" gap={2} flexWrap="wrap">
              <Box flex={1}>
                <ReferenceInput
                  source="supplier.supplierId"
                  reference="suppliers"
                >
                  <SelectInput
                    optionText="supplierName"
                    label="Nhà cung cấp"
                    variant="outlined"
                    fullWidth
                  />
                </ReferenceInput>
              </Box>
              <Box display={"flex"} mb={2}>
                <Button onClick={() => setOpenSupplierDialog(true)}>
                  + Thêm nhà cung cấp
                </Button>
              </Box>
            </Box>
            <Box display="flex" gap={2} flexWrap="wrap">
              <Box flex={1}>
                <ReferenceInput
                  source="publisher.publisherId"
                  reference="publishers"
                >
                  <SelectInput
                    optionText="publisherName"
                    label="Nhà sản xuất"
                    variant="outlined"
                    fullWidth
                  />
                </ReferenceInput>
              </Box>
              <Box display={"flex"} mb={2}>
                <Button onClick={() => setOpenPublisherDialog(true)}>
                  + Thêm nhà sản xuất
                </Button>
              </Box>
            </Box>

            <ImageInput
              source="images"
              accept={{ "image/*": [".png", ".jpg", ".jpeg", ".gif", ".webp"] }}
              multiple
            >
              <ImageField source="src" title="title" sx={{ width: 135 }} />
            </ImageInput>
            <TextInput source="description" label="Mô tả" multiline fullWidth />
          </Box>
        </Box>
        <AuthorCreateDialog
          open={openAuthorDialog}
          onClose={() => {
            setOpenAuthorDialog(false);
            refresh();
          }}
        />
        <CategoryCreateDialog
          open={openCategoryDialog}
          onClose={() => {
            setOpenCategoryDialog(false);
            refresh();
          }}
        />
        <LanguageCreateDialog
          open={openLanguageDialog}
          onClose={() => {
            setOpenLanguageDialog(false);
            refresh();
          }}
        />
        <SupplierCreateDialog
          open={openSupplierDialog}
          onClose={() => {
            setOpenSupplierDialog(false);
            refresh();
          }}
        />
        <PublisherCreateDialog
          open={openPublisherDialog}
          onClose={() => {
            setOpenPublisherDialog(false);
            refresh();
          }}
        />
      </SimpleForm>
    </Create>
  );
};

export default ProductCreate;

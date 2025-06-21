import { useEffect, useState } from "react";
import {
  AutocompleteInput,
  BooleanInput,
  Button,
  Edit,
  ImageField,
  ImageInput,
  NumberInput,
  ReferenceInput,
  SelectArrayInput,
  SelectInput,
  SimpleForm,
  TextInput,
} from "react-admin";
import CategoryCreateDialog from "../Categories/CategoryCreateDialog";
import LanguageCreateDialog from "../Languages/LanguageCreateDialog";
import SupplierCreateDialog from "../Suppliers/SupplierCreateDialog";
import PublisherCreateDialog from "../Publishers/PublisherCreateDialog";
import AuthorCreateDialog from "../Authors/AuthorCreateDialog";
import { Box } from "@mui/material";
import { validateIsbn } from "./ProductCreate";
import CustomRichTextInput from "../../components/CustomRichTextInput";

const ProductEdit = () => {
  const [openAuthorDialog, setOpenAuthorDialog] = useState(false);
  const [openCategoryDialog, setOpenCategoryDialog] = useState(false);
  const [openLanguageDialog, setOpenLanguageDialog] = useState(false);
  const [openPublisherDialog, setOpenPublisherDialog] = useState(false);
  const [openSupplierDialog, setOpenSupplierDialog] = useState(false);
  useEffect(() => {
    document.title = "Chỉnh sửa sản phẩm";
  }, []);
  return (
    <Edit mutationMode="pessimistic">
      <SimpleForm
        defaultValues={(record: any) => ({
          ...record,
          categoryIds: record.categories?.map((c: any) => c.categoryId), // Khi load
          authorIds: record.authors?.map((c: any) => c.authorId), // Khi load
          languageIds: record.languages?.map((c: any) => c.languageId), // Khi load
        })}
      >
        <Box display="flex" gap={2} flexWrap="wrap">
          {/* Cột 1 */}
          <Box flex={1} minWidth={300}>
            <TextInput
              source="productName"
              label="Tên sách"
              required
              fullWidth
            />
            <TextInput
              source="isbn"
              label="Mã sách"
              validate={validateIsbn}
              inputProps={{ maxLength: 13 }}
              fullWidth
            />
            <TextInput source="size" label="Kích thước" fullWidth />{" "}
            <TextInput source="weight" label="Trọng lượng" fullWidth />
            <TextInput source="format" label="Hình thức" fullWidth />
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
              source="discount"
              label="% Giảm giá"
              min={0}
              max={100}
              defaultValue={0}
              required
              fullWidth
            />
            <BooleanInput source="status" label="Trạng thái" />
          </Box>

          {/* Cột 2 */}
          <Box flex={1} minWidth={300}>
            <Box display="flex" gap={2} flexWrap="wrap">
              <Box flex={1}>
                <ReferenceInput source="authorIds" reference="authors" multiple>
                  <AutocompleteInput
                    optionText="authorName"
                    label="Tác giả"
                    filterToQuery={(searchText) => ({ keyword: searchText })}
                    variant="outlined"
                    fullWidth
                    multiple
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
                  <AutocompleteInput
                    optionText="categoryName"
                    label="Danh mục"
                    filterToQuery={(searchText) => ({ keyword: searchText })}
                    variant="outlined"
                    fullWidth
                    multiple
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
                  <AutocompleteInput
                    optionText="name"
                    label="Ngôn ngữ"
                    filterToQuery={(searchText) => ({ keyword: searchText })}
                    variant="outlined"
                    fullWidth
                    multiple
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
                  <AutocompleteInput
                    optionText="supplierName"
                    label="Nhà cung cấp"
                    filterToQuery={(searchText) => ({ keyword: searchText })}
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
                  <AutocompleteInput
                    optionText="publisherName"
                    label="Nhà sản xuất"
                    filterToQuery={(searchText) => ({ keyword: searchText })}
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
              placeholder={"Nhấn để tải ảnh lên"}
              multiple
              format={(value) =>
                Array.isArray(value)
                  ? value.map((img: any) => ({
                      fileId: img.fileId || null,
                      src: img.fileName || img.src || img, // hỗ trợ cả string hoặc object
                      title: img.title || "Ảnh hiện tại",
                    }))
                  : []
              }
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
          }}
        />
        <CategoryCreateDialog
          open={openCategoryDialog}
          onClose={() => {
            setOpenCategoryDialog(false);
          }}
        />
        <LanguageCreateDialog
          open={openLanguageDialog}
          onClose={() => {
            setOpenLanguageDialog(false);
          }}
        />
        <SupplierCreateDialog
          open={openSupplierDialog}
          onClose={() => {
            setOpenSupplierDialog(false);
          }}
        />
        <PublisherCreateDialog
          open={openPublisherDialog}
          onClose={() => {
            setOpenPublisherDialog(false);
          }}
        />
      </SimpleForm>
    </Edit>
  );
};
export default ProductEdit;

import { Label } from "@mui/icons-material";
import { Box } from "@mui/material";
import {
  ArrayField,
  ChipField,
  FunctionField,
  Labeled,
  NumberField,
  Show,
  SimpleShowLayout,
  SingleFieldList,
  TextField,
} from "react-admin";

const ProductShow = () => (
  <Show>
    <SimpleShowLayout sx={{ mb: 4 }}>
      <Box
        sx={{
          display: "grid",
          gridTemplateColumns: "1fr 1fr 1fr",
          gap: 3,
        }}
      >
        <Box>
          <Labeled label="Product ID" sx={{ fontWeight: "bold" }}>
            <TextField source="productId" />
          </Labeled>
        </Box>
        <Box>
          <Labeled label="Tên sách" sx={{ fontWeight: "bold" }}>
            <TextField source="productName" />
          </Labeled>
        </Box>
        <Box>
          <Labeled label="Mã sách" sx={{ fontWeight: "bold" }}>
            <TextField source="isbn" />
          </Labeled>
        </Box>
        <Box>
          <Labeled label="Kích thước" sx={{ fontWeight: "bold" }}>
            <TextField source="size" />
          </Labeled>
        </Box>
        <Box>
          <Labeled label="Năm xuất bản" sx={{ fontWeight: "bold" }}>
            <NumberField source="year" />
          </Labeled>
        </Box>
        <Box>
          <Labeled label="Số trang" sx={{ fontWeight: "bold" }}>
            <NumberField source="pageNumber" />
          </Labeled>
        </Box>
        <Box>
          <Labeled label="Giá" sx={{ fontWeight: "bold" }}>
            <NumberField
              source="price"
              options={{ style: "currency", currency: "VND" }}
              locales="vi-VN"
            />
          </Labeled>
        </Box>
        <Box>
          <Labeled label="Số lượng" sx={{ fontWeight: "bold" }}>
            <NumberField source="quantity" />
          </Labeled>
        </Box>
        <Box>
          <Labeled label="% Giảm giá" sx={{ fontWeight: "bold" }}>
            <NumberField source="discount" />
          </Labeled>
        </Box>

        <Box>
          <Labeled label="Tác giả" sx={{ fontWeight: "bold" }}>
            <ArrayField source="authors" label="Tác giả">
              <SingleFieldList>
                <ChipField source="authorName" />
              </SingleFieldList>
            </ArrayField>
          </Labeled>
        </Box>

        <Box>
          <Labeled label="Danh mục" sx={{ fontWeight: "bold" }}>
            <ArrayField source="categories" label="Danh mục">
              <SingleFieldList>
                <ChipField source="categoryName" />
              </SingleFieldList>
            </ArrayField>
          </Labeled>
        </Box>

        <Box>
          <Labeled label="Ngôn ngữ" sx={{ fontWeight: "bold" }}>
            <ArrayField source="languages" label="Ngôn ngữ">
              <SingleFieldList>
                <ChipField source="name" />
              </SingleFieldList>
            </ArrayField>
          </Labeled>
        </Box>

        <Box>
          <Labeled label="Nhà cung cấp" sx={{ fontWeight: "bold" }}>
            <TextField source="supplier.supplierName" />
          </Labeled>
        </Box>

        <Box>
          <Labeled label="Nhà sản xuất" sx={{ fontWeight: "bold" }}>
            <TextField source="publisher.publisherName" />
          </Labeled>
        </Box>

        <Box>
          <Labeled label="Trạng thái" sx={{ fontWeight: "bold" }}>
            <FunctionField
              label="Trạng thái"
              render={(record) => (record.status ? "Hiện" : "Ẩn")}
            />
          </Labeled>
        </Box>
      </Box>
      <Box>
        <Labeled label="Ảnh" sx={{ fontWeight: "bold" }}>
          <ArrayField source="images" label="Ảnh">
            <SingleFieldList>
              <FunctionField
                render={(imageUrl) => (
                  <img
                    src={imageUrl}
                    alt="Ảnh"
                    style={{
                      width: 100,
                      height: "auto",
                      marginRight: 8,
                      borderRadius: 4,
                    }}
                  />
                )}
              />
            </SingleFieldList>
          </ArrayField>
        </Labeled>
      </Box>

      <Box>
        <Labeled label="Mô tả" sx={{ fontWeight: "bold" }}>
          <TextField source="description" />
        </Labeled>
      </Box>
    </SimpleShowLayout>
  </Show>
);
export default ProductShow;
